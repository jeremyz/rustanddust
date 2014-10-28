package ch.asynk.tankontank.engine;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;

public class PawnSet extends LinkedHashSet<Pawn> implements Board.PawnCollection
{
    private final Board board;
    private int overlay;

    public PawnSet(Board board, int overlay, int n)
    {
        super(n);
        this.board = board;
        this.overlay = overlay;
    }

    public Pawn first()
    {
        if (isEmpty()) return null;
        return iterator().next();
    }

    public void show()
    {
        enable(overlay, true);
    }

    public void hide()
    {
        enable(overlay, false);
    }

    public void enable(int i, boolean enable)
    {
        for (Pawn pawn : this)
            pawn.enableOverlay(i, enable);
    }

    public void collectTiles(Board.TileCollection tiles)
    {
        tiles.clear();
        for (Pawn pawn : this)
            tiles.add(pawn.getTile());
    }

    public int fromNodes(Collection<SearchBoard.Node> nodes)
    {
        clear();
        for (SearchBoard.Node node : nodes) {
            Tile tile = board.getTile(node.col, node.row);
            Iterator<Pawn> pawns = tile.iterator();
            while(pawns.hasNext())
                add(pawns.next());
        }

        return size();
    }
}
