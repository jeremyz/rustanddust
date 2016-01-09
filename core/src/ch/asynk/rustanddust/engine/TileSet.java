package ch.asynk.rustanddust.engine;

import ch.asynk.rustanddust.engine.util.ArrayListIt;

public class TileSet extends ArrayListIt<Tile>
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

    @Override
    public boolean add(Tile tile)
    {
        if (contains(tile)) return false;
        super.add(tile);
        return true;
    }
}
