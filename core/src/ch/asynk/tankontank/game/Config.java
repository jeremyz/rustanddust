package ch.asynk.tankontank.game;

public class Config
{
    public enum Graphics {
        MINE("mine", 0),
        ORIGINAL("original", 1);
        public String s;
        public int i;
        Graphics(String s, int i)
        {
            this.s = s;
            this.i = i;
        }
        public Graphics next()
        {
            if (this == ORIGINAL)
                return MINE;
            return ORIGINAL;
        }
    };

    public boolean showMoves;
    public boolean showTargets;
    public boolean showMoveAssists;
    public boolean canCancel;
    public boolean mustValidate;
    public boolean showEnemyPossibilities;
    public boolean debug;
    public Battle battle;
    public float fxVolume;
    public Graphics graphics;

    public Config()
    {
        this.debug = false;
        this.showMoves = true;
        this.showTargets = true;
        this.showMoveAssists = true;
        this.canCancel = false;
        this.mustValidate = false;
        this.showEnemyPossibilities = false;
        this.graphics = Graphics.MINE;
        this.battle = null;
        this.fxVolume = 1f;
    }
}
