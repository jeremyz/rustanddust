package ch.asynk.tankontank.engine;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class TileList extends ArrayList<Tile>
{
    private final Board board;
    private int overlay;

    public TileList(Board board, int overlay, int n)
    {
        super(n);
        this.board = board;
        this.overlay = overlay;
    }

    public int fromNodes(List<SearchBoard.Node> nodes)
    {
        clear();
        for (SearchBoard.Node node : nodes) {
            Tile tile = board.getTile(node.col, node.row);
            add(tile);
        }

        return size();
    }

    public int fromNodes(Set<SearchBoard.Node> nodes)
    {
        clear();
        for (SearchBoard.Node node : nodes) {
            Tile tile = board.getTile(node.col, node.row);
            add(tile);
        }

        return size();
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

    public void getPawns(List<Pawn> pawns)
    {
        pawns.clear();
        for (Tile tile : this)
            pawns.add(tile.getTopPawn());
    }
}
