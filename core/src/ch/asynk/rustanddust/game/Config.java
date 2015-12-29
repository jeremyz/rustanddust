package ch.asynk.rustanddust.game;

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

    public enum GameMode
    {
        SOLO("Solo", 0),
        PVE("Player vs AI", 1),
        PVP("Player vs Player", 2);
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
                return PVE;
            if(this == PVE)
                return PVP;
            return SOLO;
        }
    };

    public GameMode gameMode;
    public boolean showMoves;
    public boolean showTargets;
    public boolean showMoveAssists;
    public boolean showEnemyPossibilities;
    public boolean debug;
    public Battle battle;
    public float fxVolume;
    public Graphics graphics;

    public Config()
    {
        this.gameMode = GameMode.SOLO;
        this.debug = false;
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
}
