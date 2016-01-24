package ch.asynk.rustanddust.engine;

import ch.asynk.rustanddust.engine.util.IterableSet;

public class TileSet extends IterableSet<Tile>
{
    protected final Board board;

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
