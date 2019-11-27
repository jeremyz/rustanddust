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
    private static final int DB_SCHEMA_VERSION = 2;

    public static final int NO_RECORD = -1;
    private static final String DIGEST = "SHA-256";
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        + " ,mode integer not null, battle integer not null, opponent integer not null"
        + " ,turn integer not null, currentPlayer integer default null"
        + " ,ts datetime default current_timestamp, synched integer default 0"
        + " ,players text default null, playersH text default null"
        + " ,map text default null, mapH text default null"
        + " ,orders text default null, ordersH text default null"
        + " ,foreign key (battle) references battles(_id)"
        + " ,foreign key (opponent) references players(_id)"
        + " ,foreign key (currentPlayer) references players(_id)"
        + " unique (mode, battle, opponent)"
        + ");";

    private static final String TBL_TURNS_CRT = "create table if not exists"
        + " turns ( _id integer primary key autoincrement"
        + " ,game integer not null, turn integer not null, currentPlayer integer not null"
        + " ,players text default null, playersH text default null"
        + " ,map text default null, mapH text default null"
        + " ,orders text default null, ordersH text default null"
        + " ,foreign key (game) references games(_id)"
        + " ,foreign key (currentPlayer) references players(_id)"
        + " unique (game, turn)"
        + ");";

    private static final String FEED_CONFIG = " insert or ignore into config values(\"version\", " + DB_SCHEMA_VERSION + ");";

    public DB(String dbPath, boolean debug)
    {
        this.db = DatabaseFactory.getNewDatabase(dbPath, DB_SCHEMA_VERSION, null, null);
        this.db.setupDatabase();
        this.debug = debug;

        try {
            db.openOrCreateDatabase();
        } catch (SQLiteGdxException e) {
            RustAndDust.error(String.format("openOrCreateDatabase : %s", dbPath));
        }

        try {
            md = MessageDigest.getInstance(DIGEST);
        } catch (java.security.NoSuchAlgorithmException e) { RustAndDust.error("NoSuchAlgorithm"); }

        Boolean version = checkVersion();
        if(version == null)
            createTables();
        else if (version == false) {
            dropTables();
            createTables();
        }
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

    private void dropTables()
    {
        try {
            exec("drop table if exists turns");
            exec("drop table if exists states");
            exec("drop table if exists games");
            exec(String.format("update config set value=%d where key='version';", DB_SCHEMA_VERSION));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("table creation error " + e.getMessage());
        }
    }

    public String getDigest(String str)
    {
        String hash = null;
        try {
            hash = new BigInteger(1, md.digest(str.getBytes("UTF-8"))).toString(16);
        } catch (java.io.UnsupportedEncodingException e) { RustAndDust.error("getDigest"); }

        return hash;
    }

    private boolean checkDigest(String what, int id, String payload, String digest)
    {
        if (payload == null) return true;
        if (digest.equals(getDigest(payload)))
            return true;
        RustAndDust.error(String.format("corrupted %s(%d)", what, id));
        return false;
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

    private static final String STORE_GAME_STATE = "update games set ts=current_timestamp, turn=%d, currentPlayer=%d,"
        + " players='%s', playersH='%s', map='%s', mapH='%s', synched=1 where _id=%d;";

    public boolean storeGameState(int game, int turn, int player, String players, String map)
    {
        RustAndDust.debug("storeGameState");
        try {
            String playersH = getDigest(players);
            if (playersH == null) return false;
            String mapH = getDigest(map);
            if (mapH == null) return false;
            exec(String.format(STORE_GAME_STATE, turn, player, players, playersH, map, mapH, game));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeGameState");
            return false;
        }
        return true;
    }

    private static final String STORE_GAME_ORDERS = "update games set ts=current_timestamp, orders='%s', ordersH='%s', synched=0 where _id=%d;";

    public boolean storeGameOrders(int game, String orders)
    {
        RustAndDust.debug("storeGameOrders");
        try {
            String ordersH = getDigest(orders);
            if (ordersH == null) return false;
            exec(String.format(STORE_GAME_ORDERS, orders, ordersH, game));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeGameOrders");
            return false;
        }
        return true;
    }

    private static final String STORE_TURN_ORDERS = "update turns set orders=(select orders from games where _id=%d), ordersH=(select ordersH from games where _id=%d)"
        + " where game=%d and turn =%d;";

    public boolean storeTurnOrders(int game, int turn, String orders)
    {
        RustAndDust.debug("storeTurnOrders");
        try {
            String ordersH = getDigest(orders);
            if (ordersH == null) return false;
            exec(String.format(STORE_TURN_ORDERS, game, game, game, turn));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeTurnOrders");
            return false;
        }
        return true;
    }

    private static final String STORE_TURN_STATE = "insert into turns(game, turn, currentPlayer, players, playersH, map, mapH, orders, ordersH)"
        + " select _id, turn, currentPlayer, players, playersH, map, mapH, null, null from games where _id=%d;";

    public boolean storeTurnState(int game)
    {
        RustAndDust.debug("storeTurnState");
        try {
            exec(String.format(STORE_TURN_STATE, game));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("storeTurnState");
            return false;
        }
        return true;
    }

    public boolean clearGameOrders(int game)
    {
        RustAndDust.debug("clearGameOrders");
        try {
            exec(String.format("update games set orders=null, ordersH=null, synched=1 where _id=%d;", game));
        } catch (SQLiteGdxException e) {
            RustAndDust.error("clearGameOrders");
            return false;
        }
        return true;
    }

    private static final String LOAD_BASE = "select g._id, g.mode, g.battle, g.opponent, g.turn, g.currentPlayer, g.ts, g.synched";

    private static final String LOAD_GAMES = LOAD_BASE + ", null, null, null, null, null, null, p.name, b.name"
        + " from games g inner join players p on (p._id=g.opponent) inner join battles b on (b._id=g.battle) where g.mode=%d;";

    public void loadGames(int mode)
    {
        RustAndDust.debug("loadGames");
        GameRecord.clearList();
        try {
            DatabaseCursor cursor = query(String.format(LOAD_GAMES, mode));
            if (cursor.getCount() > 0) {
                while(cursor.next()) {
                    GameRecord r = gameRecordFrom(cursor);
                    if (r != null)
                        GameRecord.list.add(r);
                }
            }
        } catch (SQLiteGdxException e) { RustAndDust.error("loadGames"); }
    }

    private static final String LOAD_GAME = LOAD_BASE + ", g.players, g.playersH, g.map, g.mapH, g.orders, g.ordersH, null, null from games g where g._id=%d;";

    public GameRecord loadGame(int game)
    {
        RustAndDust.debug("loadGame");
        return loadGame(game, String.format(LOAD_GAME, game), "loadGAME");
    }

    private static final String LOAD_LAST_TURN = "select g._id, g.mode, g.battle, g.opponent, g.turn, g.currentPlayer, g.ts, g.synched"
        + ", t.players, t.playersH, t.map, t.mapH, g.orders, g.ordersH, null, null"
        +" from games g inner join turns t on (g._id=t.game and t.turn=g.turn) where g._id=%d;";

    public GameRecord loadLastTurn(int game)
    {
        RustAndDust.debug("loadLastTurn");
        return loadGame(game, String.format(LOAD_LAST_TURN, game), "loadLastTurn");
    }

    private static final String LOAD_TURN = "select g._id, g.mode, g.battle, g.opponent, t.turn, t.currentPlayer, g.ts, g.synched"
        + ", t.players, t.playersH, t.map, t.mapH, case when g.turn=t.turn then g.orders else t.orders end, case when g.turn=t.turn then g.ordersH else t.ordersH end, null, null"
        +" from games g inner join turns t on (g._id = t.game) where g._id=%d and t.turn = %d;";

    public GameRecord loadTurn(int game, int turn)
    {
        RustAndDust.debug("loadTurn");
        return loadGame(game, String.format(LOAD_TURN, game, turn), "loadTurn", false);
    }

    private GameRecord loadGame(int game, String sql, String errMsg)
    {
        return loadGame(game, sql, errMsg, true);
    }

    private GameRecord loadGame(int game, String sql, String errMsg, boolean deleteOnError)
    {
        GameRecord r = null;
        try {
            DatabaseCursor cursor = query(sql);
            if (cursor.getCount() > 0) {
                cursor.next();
                r = gameRecordFrom(cursor);
                if (!checkDigest("GameState", game, r.players, r.playersH))
                    r = null;
                if (!checkDigest("GameState", game, r.map, r.mapH))
                    r = null;
                else if(!checkDigest("GameOrders", game, r.orders, r.ordersH))
                    r = null;
            }
        } catch (SQLiteGdxException e) { RustAndDust.error(errMsg); }
        if (deleteOnError && (r == null))
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

    private GameRecord gameRecordFrom(DatabaseCursor cursor)
    {
        GameRecord r = GameRecord.get();

        try {
            r.id = cursor.getInt(0);
            r.mode = GameMode.from(cursor.getInt(1));
            r.battle = cursor.getInt(2);
            r.opponent = cursor.getInt(3);
            r.turn = cursor.getInt(4);
            r.currentPlayer = cursor.getInt(5);
            try { r.ts = df.parse(cursor.getString(6)); }
            catch (java.text.ParseException e) {
                r.ts = null;
                RustAndDust.error(String.format("can't parse", cursor.getString(6)));
            }
            r.synched = ((cursor.getInt(7) == 1) ? true : false);
            r.players = cursor.getString(8);
            r.playersH = cursor.getString(9);
            r.map = cursor.getString(10);
            r.mapH = cursor.getString(11);
            r.orders = cursor.getString(12);
            r.ordersH = cursor.getString(13);
            //
            r.oName = cursor.getString(14);
            r.bName = cursor.getString(15);
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
