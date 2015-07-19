package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.engine.Orientation;

public class Zone extends HexSet
{
    public int allowedMoves;
    public Orientation orientation;

    public Zone(Map map, int n)
    {
        super(map, n);
    }
}
