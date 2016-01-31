package ch.asynk.rustanddust;

import java.security.MessageDigest;
import java.math.BigInteger;

import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

public class DB
{
    private static final int DB_SCHEMA_VERSION = 1;

    public static final int NO_RECORDS = -1;
    private static final String DIGEST = "SHA-256";

    private Database db;
    private MessageDigest md;

    private static final String TBL_CFG_CRT = "create table if not exists"
            + " config ( key text primary key, value text not null);"
            + " insert or ignore into config values(\"version\", " + DB_SCHEMA_VERSION + ");";

    private static final String TBL_PLAYERS_CRT = "create table if not exists"
            + " players ( _id integer primary key autoincrement,"
            + " hash text unique not null, gmail text unique not null, firstname text, lastname text"
            + ");";

    private static final String TBL_BATTLES_CRT = "create table if not exists"
            + " battles ( _id integer primary key, name text"
            + ");";

    private static final String TBL_GAMES_CRT = "create table if not exists"
            + " games ( _id integer primary key autoincrement,"
            + " _p1 integer not null, _p2 integer not null, _b integer not null,"
            + " int mode not null, ts datetime default current_timestamp,"
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

    private static final String INSERT_PLAYER = "insert into players(hash,gmail,firstname,lastname) values ('%s','%s','%s','%s');";
    private static final String GET_PLAYER_ID = "select _id from players where hash='%s';";
    private static final String UPDATE_BATTLE = "insert or replace into battles values (%d,'%s');";
    private static final String INSERT_GAME = "insert into games(_p1,_p2,_b,m) values (%d,%d,%d,%d);";
    private static final String GET_GAME_ID = "select _id from games where _p1=%d and _p2=%d and _b=%d;";
    private static final String INSERT_TURN = "insert into turns(_g,_p,hash,payload) values (%d,%d,'%s','%s'); update games set ts=current_timestamp where _id=%d;";
    private static final String INSERT_STATE = "insert or replace into states(_g,hash,payload) values (%d,'%s','%s'); update games set ts=current_timestamp where _id=%d;";

    private static final String DB_CRT = TBL_CFG_CRT + TBL_PLAYERS_CRT + TBL_BATTLES_CRT + TBL_GAMES_CRT + TBL_TURNS_CRT + TBL_STATES_CRT;

    public DB(String dbPath)
    {
        db = DatabaseFactory.getNewDatabase(dbPath, DB_SCHEMA_VERSION, DB_CRT, null);
        db.setupDatabase();
    }

    public void setup()
    {
        try {
            md = MessageDigest.getInstance(DIGEST);
        } catch (java.security.NoSuchAlgorithmException e) { }
        try {
            db.openOrCreateDatabase();
        } catch (SQLiteGdxException e) { }
    }

    private String getDigest(String str)
    {
        String hash = null;
        try {
            hash = new BigInteger(1, md.digest(str.getBytes("UTF-8"))).toString(16);
        } catch (java.io.UnsupportedEncodingException e) { }

        return hash;
    }

    public String storePlayer(String gmail, String firstname, String lastname)
    {
        String hash = getDigest(gmail + firstname + lastname);
        if (hash == null) return null;
        try {
            if (getPlayerId(hash) == NO_RECORDS) {
                db.execSQL(String.format(INSERT_PLAYER, hash, gmail, firstname, lastname));
            }
        } catch (SQLiteGdxException e) { return null; }
        return hash;
    }

    public int getPlayerId(String hash)
    {
        int ret = NO_RECORDS;
        try {
            DatabaseCursor cursor = db.rawQuery(String.format(GET_PLAYER_ID, hash));
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = cursor.getInt(0);
            }
        } catch (SQLiteGdxException e) { }
        return ret;
    }

    public int storePlayerGetId(String gmail, String firstname, String lastname)
    {
        String hash = storePlayer(gmail, firstname, lastname);
        if (hash == null) return NO_RECORDS;
        return getPlayerId(hash);
    }

    public void storeBattle(int id, String name)
    {
        try {
            db.execSQL(String.format(UPDATE_BATTLE, id, name));
        } catch (SQLiteGdxException e) { }
    }

    public void storeGame(int you, int opponent, int battle, int mode)
    {
        try {
            if (getGameId(you, opponent, battle, mode) == NO_RECORDS)
                db.execSQL(String.format(INSERT_GAME, you, opponent, battle, mode));
        } catch (SQLiteGdxException e) { }
    }

    public int getGameId(int you, int opponent, int battle, int mode)
    {
        int ret = NO_RECORDS;
        try {
            DatabaseCursor cursor = db.rawQuery(String.format(GET_GAME_ID, you, opponent, battle, mode));
            if (cursor.getCount() > 0) {
                cursor.next();
                ret = cursor.getInt(0);
            }
        } catch (SQLiteGdxException e) { }
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
            db.execSQL(String.format(INSERT_TURN, game, player, hash, payload, game));
        } catch (SQLiteGdxException e) {
            return false;
        }
        return true;
    }

    public boolean storeState(int game, String payload)
    {
        try {
            String hash = getDigest(payload);
            if (hash == null) return false;
            db.execSQL(String.format(INSERT_STATE, game, hash, payload, game));
        } catch (SQLiteGdxException e) {
            return false;
        }
        return true;
    }
}
