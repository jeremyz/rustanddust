package ch.asynk.tankontank.engine;

import java.util.List;

public interface Tile
{
    public int push(Pawn pawn);

    public void remove(Pawn pawn);

    public Pawn getTop();

    public int costFrom(Side side);

    public enum Orientation
    {
        KEEP(0),
        WEST(-90),
        NORTH_WEST(-30),
        NORTH_EAST (30),
        EAST(90),
        SOUTH_EAST(150),
        SOUTH_WEST(-150);

        public final int v;
        Orientation(int v) { this.v = v; }
    }

    public enum Side
    {
        WEST(1),
        NORTH_WEST(2),
        NORTH_EAST (4),
        EAST(8),
        SOUTH_EAST(16),
        SOUTH_WEST(32);

        public final int v;
        Side(int v) { this.v = v; }
    }
}
