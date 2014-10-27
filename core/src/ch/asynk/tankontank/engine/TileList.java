package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.ArrayList;

public class TileList
{
    private final Board board;
    private final List<Tile> tiles;

    public TileList(Board board, int n)
    {
        this.board = board;
        this.tiles = new ArrayList<Tile>(n);
    }

    public int fromNodes(List<SearchBoard.Node> nodes)
    {
        tiles.clear();
        for (SearchBoard.Node node : nodes) {
            Tile tile = board.getTile(node.col, node.row);
            tiles.add(tile);
        }

        return tiles.size();
    }

    public int size()
    {
        return tiles.size();
    }

    public void clear()
    {
        tiles.clear();
    }

    public Tile get(int i)
    {
        return tiles.get(i);
    }

    public void add(Tile tile)
    {
        tiles.add(tile);
    }

    public boolean remove(Tile tile)
    {
        return tiles.remove(tile);
    }

    public boolean contains(Tile tile)
    {
        return tiles.contains(tile);
    }

    public void enable(int i, boolean enable)
    {
        for (Tile tile : tiles)
            board.enableOverlayOn(tile, i, enable);
    }

    public void getPawns(List<Pawn> pawns)
    {
        pawns.clear();
        for (Tile tile : tiles)
            pawns.add(tile.getTopPawn());
    }
}
