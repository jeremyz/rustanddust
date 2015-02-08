package ch.asynk.tankontank.game;

public class Config
{
    public enum Graphics {
        ORIGINAL("original"),
        MINE("mine");
        public String s;
        Graphics(String s)
        {
            this.s = s;
        }
        public Graphics next()
        {
            Graphics next = null;
            switch(this) {
                case ORIGINAL:
                    next = MINE;
                    break;
                case MINE:
                    next = ORIGINAL;
                    break;
            }
            return next;
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
