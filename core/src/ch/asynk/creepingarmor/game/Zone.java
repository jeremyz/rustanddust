package ch.asynk.creepingarmor.game;

import ch.asynk.creepingarmor.engine.Orientation;

public class Zone extends HexSet
{
    public int allowedMoves;
    public Orientation orientation;

    public Zone(Map map, int n)
    {
        super(map, n);
    }
}
