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
    }
}
