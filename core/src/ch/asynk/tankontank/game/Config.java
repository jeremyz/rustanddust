package ch.asynk.tankontank.game;

public class Config
{
    public boolean showMoves;
    public boolean showTargets;
    public boolean showMoveAssists;
    public boolean canCancel;
    public boolean mustValidate;
    public boolean showEnemyPossibilities;
    public boolean regularPawns;
    public boolean debug;
    public Battle battle;
    public float fxVolume;

    public Config()
    {
        this.debug = false;
        this.showMoves = true;
        this.showTargets = true;
        this.showMoveAssists = true;
        this.canCancel = true;
        this.mustValidate = false;
        this.showEnemyPossibilities = false;
        this.regularPawns = true;
        this.battle = null;
        this.fxVolume = 1f;
    }
}
