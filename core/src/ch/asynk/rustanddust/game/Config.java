package ch.asynk.rustanddust.game;

import java.io.StringWriter;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class Config
{
    public enum Graphics {
        CHITS("chits", 0),
        TANKS("tanks", 1);
        public String s;
        public int i;
        Graphics(String s, int i)
        {
            this.s = s;
            this.i = i;
        }
        public Graphics next()
        {
            if (this == TANKS)
                return CHITS;
            return TANKS;
        }
        public Graphics get(int i)
        {
            if (i == CHITS.i)
                return CHITS;
            else if (i == TANKS.i)
                return TANKS;
            return null;
        }
    };

    public enum LoadMode
    {
        NEW("New", 0),
        RESUME("Resume", 1),
        REPLAY_LAST("Replay Last Turn", 2),
        REPLAY_BATTLE("Replay Battle", 3);
        public String s;
        public int i;
        LoadMode(String s, int i)
        {
            this.s = s;
            this.i = i;
        }
    }

    public enum GameMode
    {
        SOLO("Solo", 0),
        PVP("Player vs Player", 1),
        PVE("Player vs AI", 2);
        public String s;
        public int i;
        GameMode(String s, int i)
        {
            this.s = s;
            this.i = i;
        }
        public GameMode next()
        {
            if (this == SOLO)
                return PVP;
            if (this == PVP)
                return PVE;
            return SOLO;
        }
        public static GameMode from(int i)
        {
            GameMode r = SOLO;
            switch(i) {
                case 1: r = PVE; break;
                case 2: r = PVP; break;
            }
            return r;
        }
        public GameMode getLongest() { return PVP; }
    };

    public GameMode gameMode;
    public LoadMode loadMode;
    public boolean showMoves;
    public boolean showTargets;
    public boolean showMoveAssists;
    public boolean showEnemyPossibilities;
    public boolean autoPath;
    public boolean revertAllMoves;
    public boolean debug;
    public Battle battle;
    public int gameId;
    public float fxVolume;
    public Graphics graphics;

    public static String [] fxStrings = { "OFF", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "ON" };

    private static StringWriter writer = new StringWriter(256);

    public Config()
    {
        this.gameMode = GameMode.SOLO;
        this.loadMode = LoadMode.NEW;
        this.debug = false;
        this.autoPath = false;
        this.revertAllMoves = false;
        this.showMoves = true;
        this.showTargets = true;
        this.showMoveAssists = true;
        this.showEnemyPossibilities = false;
        this.graphics = Graphics.CHITS;
        this.battle = null;
        this.fxVolume = 1f;
    }

    public boolean gameModeImplemented()
    {
        return (gameMode == GameMode.SOLO);
    }

    public String unload()
    {
        Json json = new Json(OutputType.json);
        writer.getBuffer().setLength(0);
        json.setWriter(writer);

        json.writeObjectStart();
        json.writeValue("mode", gameMode);
        json.writeValue("debug", debug);
        json.writeValue("autoPath", autoPath);
        json.writeValue("revertAllMoves", revertAllMoves);
        json.writeValue("showMoves", showMoves);
        json.writeValue("showTargets", showTargets);
        json.writeValue("showMoveAssists", showMoveAssists);
        json.writeValue("showEnemyPossibilities", showEnemyPossibilities);
        json.writeValue("graphics", graphics);
        json.writeValue("fxVolume", fxVolume);
        json.writeObjectEnd();

        writer.flush();
        return writer.toString();
    }

    public void load(String payload)
    {
        if (payload == null) return;
        JsonValue root = new JsonReader().parse(payload);
        this.gameMode = GameMode.valueOf(root.getString("mode"));
        this.debug = root.getBoolean("debug");
        this.autoPath = root.getBoolean("autoPath");
        this.revertAllMoves = root.getBoolean("revertAllMoves");
        this.showMoves = root.getBoolean("showMoves");
        this.showTargets = root.getBoolean("showTargets");
        this.showMoveAssists = root.getBoolean("showMoveAssists");
        this.showEnemyPossibilities = root.getBoolean("showEnemyPossibilities");
        this.graphics = Graphics.valueOf(root.getString("graphics"));
        this.fxVolume = root.getFloat("fxVolume");;
    }
}
