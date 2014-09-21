package ch.asynk.tankontank.engine;

import java.util.List;

import ch.asynk.tankontank.engine.gfx.BasicDrawable;

public interface Tile extends BasicDrawable
{
    public int push(Pawn pawn);

    public int remove(Pawn pawn);

    public Pawn getTopPawn();

    public boolean mustBeDrawn();

    public boolean occupied();

    public boolean hasOverlayEnabled();

    public void enableOverlay(int i, boolean enable);

    public List<Tile> adjacents();

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
