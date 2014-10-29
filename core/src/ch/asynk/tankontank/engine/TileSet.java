package ch.asynk.tankontank.engine;

import java.util.Iterator;
import java.util.Collection;
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

    public void collectPawns(Board.PawnCollection pawns)
    {
        pawns.clear();
        for (Tile tile : this) {
            Iterator<Pawn> itr = tile.iterator();
            while(itr.hasNext())
                pawns.add(itr.next());
        }
    }

    public int fromNodes(Collection<SearchBoard.Node> nodes)
    {
        clear();
        for (SearchBoard.Node node : nodes) {
            Tile tile = board.getTile(node.col, node.row);
            add(tile);
        }

        return size();
    }
}
