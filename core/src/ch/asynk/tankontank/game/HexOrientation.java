package ch.asynk.tankontank.game;

public enum HexOrientation
{
    KEEP(0),
    WEST(-90),
    NORTH_WEST(-30),
    NORTH_EAST (30),
    EAST(90),
    SOUTH_EAST(150),
    SOUTH_WEST(-150);

    public final int v;
    HexOrientation(int v) { this.v = v; }
}
