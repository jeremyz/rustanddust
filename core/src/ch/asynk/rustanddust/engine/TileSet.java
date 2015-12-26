package ch.asynk.rustanddust.engine;

import java.util.LinkedHashSet;

public class TileSet extends LinkedHashSet<Tile>
{
    private final Board board;

    public TileSet(Board board, int n)
    {
        super(n);
        this.board = board;
    }

    public void enable(int i, boolean enable)
    {
        for (Tile tile : this)
            board.enableOverlayOn(tile, i, enable);
    }
}
