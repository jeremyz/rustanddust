package ch.asynk.tankontank.engine;

public class EntryPoint extends TileSet
{
    public int allowedMoves;
    public Orientation orientation;

    public EntryPoint(Board board, int n)
    {
        super(board, n);
    }
}
