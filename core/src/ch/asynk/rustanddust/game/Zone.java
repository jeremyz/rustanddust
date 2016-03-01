package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.TileSet;

public class Zone extends TileSet
{
    public int allowedMoves;
    public Orientation orientation;

    public Zone(Map map, int n)
    {
        super(map, n);
        this.allowedMoves = Orientation.KEEP.s;
        this.orientation = Orientation.KEEP;
    }

    public void add(int col, int row)
    {
        add(((Map) board).getHex(col, row));
    }
}
