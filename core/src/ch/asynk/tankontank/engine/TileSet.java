package ch.asynk.tankontank.engine;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collection;

public class TileSet extends LinkedHashSet<Tile> implements Board.TileCollection
{
    private final Board board;
    private int overlay;

    public TileSet(Board board, int overlay, int n)
    {
        super(n);
        this.board = board;
        this.overlay = overlay;
    }

    public Tile first()
    {
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
        for (Tile tile : this)
            board.enableOverlayOn(tile, i, enable);
    }

    public void getPawns(Collection<Pawn> pawns)
    {
        pawns.clear();
        for (Tile tile : this)
            pawns.add(tile.getTopPawn());
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
