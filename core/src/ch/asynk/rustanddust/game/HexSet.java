package ch.asynk.rustanddust.game;

import java.util.Collection;
import java.util.LinkedHashSet;

import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Board;

public class HexSet extends LinkedHashSet<Hex>
{
    private final Board board;

    public HexSet(Board board, int n)
    {
        super(n);
        this.board = board;
    }

    public void enable(int i, boolean enable)
    {
        for (Hex hex : this)
            board.enableOverlayOn(hex, i, enable);
    }

    @SuppressWarnings("unchecked")
    public Collection<Tile> asTiles()
    {
        return (Collection) this;
    }
}
