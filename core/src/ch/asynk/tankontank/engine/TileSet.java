package ch.asynk.tankontank.engine;

import java.util.LinkedHashSet;

public class TileSet extends LinkedHashSet<Tile> implements Board.TileCollection
{
    private final Board board;

    public TileSet(Board board, int n)
    {
        super(n);
        this.board = board;
    }

    public Tile first()
    {
        if (isEmpty()) return null;
        return iterator().next();
    }

    public void enable(int i, boolean enable)
    {
        for (Tile tile : this)
            board.enableOverlayOn(tile, i, enable);
    }
}
