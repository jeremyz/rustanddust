package ch.asynk.rustanddust.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.math.BigInteger;

import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Config.GameMode;

public class DB
{
    private static final int DB_SCHEMA_VERSION = 1;

    public static final int NO_RECORD = -1;
    private static final String DIGEST = "SHA-256";
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    private boolean debug;
    private Database db;
    private MessageDigest md;

    private static final String TBL_CFG_CRT = "create table if not exists"
        + " config ( key text primary key, value text not null);";

    private static final String TBL_PLAYERS_CRT = "create table if not exists"
        + " players ( _id integer primary key autoincrement"
        + " ,hash text unique not null, gmail text unique not null, name text not null"
        + ");";

    private static final String TBL_BATTLES_CRT = "create table if not exists"
        + " battles ( _id integer primary key, name text"
        + ");";

    private static final String TBL_GAMES_CRT = "create table if not exists"
        + " games ( _id integer primary key autoincrement"
        + " ,opponent integer not null, battle integer not null, mode integer not null"
        + " ,turn integer not null, ts datetime default current_timestamp"
        + " ,player integer default null, hash text default null, payload text default null"
        + " ,foreign key (opponent) references players(_id)"
        + " ,foreign key (battle) references battles(_id)"
        + " ,foreign key (player) references players(_id)"
        + " unique (opponent, battle, mode)"
        + ");";

    private static final String TBL_TURNS_CRT = "create table if not exists"
        + " turns ( _id integer primary key autoincrement"
        + " ,game integer not null, turn integer not null, player integer not null"
        + " ,hash text not null, payload text not null"
        + " ,foreign key (game) references games(_id)"
        + " ,foreign key (player) references players(_id)"
        + " unique (game, turn)"
        + ");";

    private static final String FEED_CONFIG = " insert or ignore into config values(\"version\", " + DB_SCHEMA_VERSION + ");";

    public DB(String dbPath, boolean debug)
    {
        this.db = DatabaseFactory.getNewDatabase(dbPath, DB_SCHEMA_VERSION, null, null);
        this.db.setupDatabase();
        this.debug = debug;
    }

    public void setup()
    {
        try {
            md = MessageDigest.getInstance(DIGEST);
        } catch (java.security.NoSuchAlgorithmException e) { RustAndDust.error("NoSuchAlgorithm"); }

        try {
            db.openOrCreateDatabase();
        } catch (SQLiteGdxException e) { RustAndDust.error("openOrCreateDatabase"); }

        Boolean version = checkVersion();
        if(version == null)
            createTables();
        else if (version == false)
            System.err.println("TODO update schema");
    }

    private void createTables()
    {
        try {
            exec(TBL_CFG_CRT);
            exec(TBL_PLAYERS_CRT);
            exec(TBL_BATTLES_CRT);
            exec(TBL_GAMES_CRT);
            exec(TBL_TURNS_CRT);
            exec(FEED_CONFIG);
        } catch (SQLiteGdxException e) {
            RustAndDust.error("table creation error " + e.getMessage());
        }
    }

    private String getDigest(String str)
    {
        String hash = null;
        try {
            hash = new BigInteger(1, md.digest(str.getBytes("UTF-8"))).toString(16);
        } catch (java.io.UnsupportedEncodingException e) { RustAndDust.error("getDigest"); }

        return hash;
    }

    private static final String CHECK_VERSION = "select (value=%d) from config where key='version';";

    public Boolean checkVersion()
    {
        Boolean ret = false;
        try {
            DatabaseCursor cursor = query(String.format(CHECK_VERSION, DB_SCHEMA_VERSION ));
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = (cursor.getInt(0) == 1);
            }
        } catch (SQLiteGdxException e) {
            RustAndDust.error("checkVersion");
            return null;
        }
        return ret;
    }

    private static final String INSERT_CONFIG = "insert or replace into config(key, value) values ('config','%s');";

    public boolean storeConfig(String config)
    {
        try {
            exec(String.format(INSERT_CONFIG, config));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeConfig");
            return false;
        }
        return true;
    }

    private static final String GET_CONFIG = "select value from config where key='config';";

    public String loadConfig()
    {
        String ret = null;
        try {
            DatabaseCursor cursor = query(GET_CONFIG);
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = cursor.getString(0);
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("loadConfig"); }
        return ret;
    }

    public void storePlayer(String gmail, String name)
    {
        String hash = getDigest(String.format("#%s@%s*", gmail, df.format(new Date())));
        if (hash != null)
            storePlayer(gmail, name, hash);
    }

    private static final String INSERT_PLAYER = "insert or ignore into players(hash,gmail,name) values ('%s','%s','%s');";

    public void storePlayer(String gmail, String name, String hash)
    {
        try {
            exec(String.format(INSERT_PLAYER, hash, gmail, name));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storePlayer");
        }
    }

    private static final String GET_PLAYER_ID_FROM_HASH = "select _id from players where hash='%s';";
    private static final String GET_PLAYER_ID_FROM_GMAIL = "select _id from players where gmail='%s';";

    public int getPlayerId(boolean hash, String s)
    {
        int ret = NO_RECORD;
        String sql = (hash ? GET_PLAYER_ID_FROM_HASH : GET_PLAYER_ID_FROM_GMAIL);
        try {
            DatabaseCursor cursor = query(String.format(sql, s));
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = cursor.getInt(0);
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("getPlayerId"); }
        return ret;
    }

    public int storePlayerGetId(String gmail, String name)
    {
        storePlayer(gmail, name);
        return getPlayerId(false, gmail);
    }

    private static final String UPDATE_BATTLE = "insert or replace into battles values (%d,'%s');";

    public void storeBattle(int id, String name)
    {
        try {
            exec(String.format(UPDATE_BATTLE, id, name));
        } catch (SQLiteGdxException e) { RustAndDust.error("storeBattle"); }
    }

    private static final String INSERT_GAME = "insert or ignore into games(opponent,battle,mode,turn) values (%d,%d,%d,0);";

    public void storeGame(int opponent, int battle, int mode)
    {
        try {
            exec(String.format(INSERT_GAME, opponent, battle, mode));
        } catch (SQLiteGdxException e) { RustAndDust.error("storeGame"); }
    }

    private static final String GET_GAME_ID = "select _id from games where opponent=%d and battle=%d and mode=%d;";

    public int getGameId(int opponent, int battle, int mode)
    {
        int ret = NO_RECORD;
        try {
            DatabaseCursor cursor = query(String.format(GET_GAME_ID, opponent, battle, mode));
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = cursor.getInt(0);
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("getGameId"); }
        return ret;
    }

    public int storeGameGetId(int opponent, int battle, int mode)
    {
        storeGame(opponent, battle, mode);
        return getGameId(opponent, battle, mode);
    }

    private static final String INSERT_TURN = "insert into turns(game,turn,player,hash,payload) values (%d,%d,%d,'%s','%s');";

    public boolean storeTurn(int game, int turn, int player, String payload)
    {
        try {
            String hash = getDigest(payload);
            if (hash == null) return false;
            exec(String.format(INSERT_TURN, game, turn, player, hash, payload));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeTurn");
            return false;
        }
        return true;
    }

    private static final String GET_TURNS = "select payload from turns where game=%d order by _id;";

    public String getTurns(int game)
    {
        String ret = null;
        try {
            DatabaseCursor cursor = query(String.format(GET_TURNS, game));
            int n = cursor.getCount();
            if (n <= 0)
                return null;

            StringBuilder builder = new StringBuilder();
            builder.append("[");
            while(cursor.next()) {
                builder.append(cursor.getString(0));
                builder.append(",");
            }
            builder.setCharAt((builder.length() - 1), ']');
            ret = builder.toString();
        } catch (SQLiteGdxException e) { RustAndDust.error("getTurns"); }
        return ret;
    }

    private static final String UPDATE_GAME = "update games set ts=current_timestamp, turn=%d, player=%d, hash='%s', payload='%s' where _id=%d;";

    public boolean storeGame(int game, int turn, int player, String payload)
    {
        RustAndDust.debug("storeGame");
        try {
            String hash = getDigest(payload);
            if (hash == null) return false;
            exec(String.format(UPDATE_GAME, turn, player, hash, payload, game));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeGame");
            return false;
        }
        return true;
    }

    private static final String LOAD_GAME = "select g._id, g.opponent, g.battle, g.mode, g.turn, g.ts, g.player, '', '', g.hash, g.payload"
        + " from games g where g._id=%d;";

    public GameRecord loadGame(int game)
    {
        RustAndDust.debug("loadGame");
        GameRecord r = null;
        try {
            DatabaseCursor cursor = query(String.format(LOAD_GAME, game));
            if (cursor.getCount() > 0) {
                cursor.next();
                r = from(cursor);
                if (!r.hash.equals(getDigest(r.payload))) {
                    RustAndDust.error(String.format("corrupted game %d", game));
                    r = null;
                }
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("loadGame"); }
        if (r == null)
            deleteGame(game);
        return r;
    }

    private static final String DELETE_GAME = "delete from games where _id=%d;";
    private static final String DELETE_TURNS = "delete from turns where game=%d;";

    public boolean deleteGame(int game)
    {
        RustAndDust.debug("deleteGame");
        try {
            exec(String.format(DELETE_TURNS, game));
            exec(String.format(DELETE_GAME, game));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("deleteGame");
            return false;
        }
        return true;

    }

    private static final String GET_GAMES = "select g._id, g.opponent, g.battle, g.mode, g.turn, g.ts, g.player, p.name, b.name, null, null"
        + " from games g inner join players p on (p._id=g.opponent) inner join battles b on (b._id=g.battle);";

    public void loadGames()
    {
        RustAndDust.debug("loadGames");
        GameRecord.clearList();
        try {
            DatabaseCursor cursor = query(GET_GAMES);
            if (cursor.getCount() > 0) {
                while(cursor.next()) {
                    GameRecord r = from(cursor);
                    if (r != null)
                        GameRecord.list.add(r);
                }
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("loadGames"); }
    }

    private GameRecord from(DatabaseCursor cursor)
    {
        GameRecord r = GameRecord.get();

        try {
            r.id = cursor.getInt(0);
            r.opponent = cursor.getInt(1);
            r.battle = cursor.getInt(2);
            r.mode = GameMode.from(cursor.getInt(3));
            r.turn = cursor.getInt(4);
            try { r.ts = df.parse(cursor.getString(5)); }
            catch (java.text.ParseException e) {
                r.ts = null;
                RustAndDust.error(String.format("can't parse", cursor.getString(5)));
            }
            r.currentPlayer = cursor.getInt(6);
            r.oName = cursor.getString(7);
            r.bName = cursor.getString(8);
            r.hash = cursor.getString(9);
            r.payload = cursor.getString(10);
        } catch (Exception e) {
            r.dispose(); RustAndDust.error("GameRecord from cursor");
        }

        return r;
    }

    private void exec(String sql) throws SQLiteGdxException
    {
        if (debug) RustAndDust.debug(" SQL " + sql);
        db.execSQL(sql);
    }

    private DatabaseCursor query(String sql) throws SQLiteGdxException
    {
        if (debug) RustAndDust.debug(" SQL " + sql);
        DatabaseCursor c = db.rawQuery(sql);
        if (debug) RustAndDust.debug(String.format(" SQL  -> %d", c.getCount()));
        return c;
    }
}
