package ch.asynk.tankontank.engine;

public class Zone extends TileSet
{
    public int allowedMoves;
    public Orientation orientation;

    public Zone(Board board, int n)
    {
        super(board, n);
    }
}
