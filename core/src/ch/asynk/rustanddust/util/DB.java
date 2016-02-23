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
            + " players ( _id integer primary key autoincrement,"
            + " hash text unique not null, gmail text unique not null, name text not null"
            + ");";

    private static final String TBL_BATTLES_CRT = "create table if not exists"
            + " battles ( _id integer primary key, name text"
            + ");";

    private static final String TBL_GAMES_CRT = "create table if not exists"
            + " games ( _id integer primary key autoincrement,"
            + " _p1 integer not null, _p2 integer not null, _b integer not null,"
            + " m integer not null, ts datetime default current_timestamp,"
            + " foreign key (_p1) references players(_id),"
            + " foreign key (_p2) references players(_id),"
            + " foreign key (_b) references battles(_id),"
            + " unique (_p1, _p2, _b)"
            + ");";

    private static final String TBL_TURNS_CRT = "create table if not exists"
            + " turns ( _id integer primary key autoincrement, _g integer not null,"
            + " _p integer not null, hash text not null, payload text not null,"
            + " foreign key (_g) references games(_id),"
            + " foreign key (_p) references players(_id)"
            + ");";

    private static final String TBL_STATES_CRT = "create table if not exists"
            + " states ( _g integer unique not null,"
            + " hash text not null, payload text not null,"
            + " foreign key (_g) references games(_id)"
            + ");";

    private static final String FEED_CONFIG = " insert or ignore into config values(\"version\", " + DB_SCHEMA_VERSION + ");";
    private static final String INSERT_CONFIG = "insert or replace into config(key, value) values ('options','%s');";
    private static final String GET_CONFIG = "select value from config where key='options';";
    private static final String INSERT_PLAYER = "insert or ignore into players(hash,gmail,name) values ('%s','%s','%s');";
    private static final String GET_PLAYER_ID_FROM_HASH = "select _id from players where hash='%s';";
    private static final String GET_PLAYER_ID_FROM_GMAIL = "select _id from players where gmail='%s';";
    private static final String UPDATE_BATTLE = "insert or replace into battles values (%d,'%s');";
    private static final String INSERT_GAME = "insert or ignore into games(_p1,_p2,_b,m) values (%d,%d,%d,%d);";
    private static final String GET_GAME_ID = "select _id from games where _p1=%d and _p2=%d and _b=%d and m=%d;";
    private static final String GET_GAME_ID2 = "select _id from games where _b=%d and m=%d;";
    private static final String INSERT_TURN = "insert into turns(_g,_p,hash,payload) values (%d,%d,'%s','%s'); update games set ts=current_timestamp where _id=%d;";
    private static final String INSERT_STATE = "insert or replace into states(_g,hash,payload) values (%d,'%s','%s'); update games set ts=current_timestamp where _id=%d;";
    private static final String UPDATE_GAME = "update games set _p1=%d, _p2=%d, ts=current_timestamp where _id=%d;";
    private static final String GET_STATE = "select payload from states where _g=%d;";
    private static final String GET_GAMES = "select g.*, p1.name, p2.name, b.name from games g inner join players p1 on (g._p1=p1._id) inner join players p2 on (g._p2=p2._id) inner join battles b on (g._b=b._id);";
    private static final String DELETE_GAME = "delete from turns where _g=%d; delete from states where _g=%d; delete from games where _id=%d;";

    // private static final String DB_CRT = TBL_CFG_CRT + TBL_PLAYERS_CRT + TBL_BATTLES_CRT + TBL_GAMES_CRT + TBL_TURNS_CRT + TBL_STATES_CRT;

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

        try {
            exec(TBL_CFG_CRT);
            exec(TBL_PLAYERS_CRT);
            exec(TBL_BATTLES_CRT);
            exec(TBL_GAMES_CRT);
            exec(TBL_TURNS_CRT);
            exec(TBL_STATES_CRT);
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

    public void storePlayer(String gmail, String name, String hash)
    {
        try {
            exec(String.format(INSERT_PLAYER, hash, gmail, name));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storePlayer");
        }
    }

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

    public void storeBattle(int id, String name)
    {
        try {
            exec(String.format(UPDATE_BATTLE, id, name));
        } catch (SQLiteGdxException e) { RustAndDust.error("storeBattle"); }
    }

    public void storeGame(int you, int opponent, int battle, int mode)
    {
        try {
            exec(String.format(INSERT_GAME, you, opponent, battle, mode));
        } catch (SQLiteGdxException e) { RustAndDust.error("storeGame"); }
    }

    public int getGameId(int you, int opponent, int battle, int mode)
    {
        int ret = NO_RECORD;
        try {
            DatabaseCursor cursor = query(String.format(GET_GAME_ID, you, opponent, battle, mode));
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = cursor.getInt(0);
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("getGameId"); }
        return ret;
    }

    public int storeGameGetId(int you, int opponent, int battle, int mode)
    {
        storeGame(you, opponent, battle, mode);
        return getGameId(you, opponent, battle, mode);
    }

    public boolean storeTurn(int game, int player, String payload)
    {
        try {
            String hash = getDigest(payload);
            if (hash == null) return false;
            exec(String.format(INSERT_TURN, game, player, hash, payload, game));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeTurn");
            return false;
        }
        return true;
    }

    public boolean storeState(int game, int p1, int p2, String payload)
    {
        RustAndDust.debug("storeState");
        try {
            String hash = getDigest(payload);
            if (hash == null) return false;
            exec(String.format(INSERT_STATE, game, hash, payload, game));
            exec(String.format(UPDATE_GAME, p1, p2, game));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeState");
            return false;
        }
        return true;
    }

    public String loadState(int game)
    {
        RustAndDust.debug("loadState");
        String ret = null;
        try {
            DatabaseCursor cursor = query(String.format(GET_STATE, game));
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = cursor.getString(0);
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("loadState"); }
        return ret;
    }

    public boolean deleteGame(GameRecord game)
    {
        RustAndDust.debug("deleteGame");
        try {
            exec(String.format(DELETE_GAME, game.g, game.g, game.g));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("deleteGame");
            return false;
        }
        return true;

    }

    public void loadGames()
    {
        RustAndDust.debug("loadGames");
        GameRecord.clearList();
        GameRecord r = null;
        try {
            DatabaseCursor cursor = query(GET_GAMES);
            if (cursor.getCount() > 0) {
                while(cursor.next()) {
                    // cursor.next();
                    r = GameRecord.get();
                    r.g = cursor.getInt(0);
                    r.p1 = cursor.getInt(1);
                    r.p2 = cursor.getInt(2);
                    r.b = cursor.getInt(3);
                    r.m = GameMode.from(cursor.getInt(4));
                    try { r.ts = df.parse(cursor.getString(5)); }
                    catch (java.text.ParseException e) { r.ts = null; RustAndDust.error(String.format("can't parse", cursor.getString(5))); }
                    r.p1Name = cursor.getString(6);
                    r.p2Name = cursor.getString(7);
                    r.bName = cursor.getString(8);
                    GameRecord.list.add(r);
                }
            }
        } catch (SQLiteGdxException e) { r.dispose(); RustAndDust.error("loadGames"); }
    }

    public int gameExists(int battle, int mode)
    {
        int ret = NO_RECORD;
        try {
            DatabaseCursor cursor = query(String.format(GET_GAME_ID2, battle, mode));
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = cursor.getInt(0);
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("loadConfig"); }
        return ret;
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
