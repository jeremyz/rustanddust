package ch.asynk.tankontank.game;

public class Config
{
    public boolean showMoves;
    public boolean showTargets;
    public boolean showMoveAssists;
    public boolean canCancel;
    public boolean mustValidate;
    public boolean showEnemyPossibilities;

    public Config()
    {
        this.showMoves = true;
        this.showTargets = true;
        this.showMoveAssists = true;
        this.canCancel = true;
        this.mustValidate = false;
        this.showEnemyPossibilities = false;
    }
}