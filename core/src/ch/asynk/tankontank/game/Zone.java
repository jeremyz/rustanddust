package ch.asynk.tankontank.game;

import ch.asynk.tankontank.engine.Orientation;

public class Zone extends HexSet
{
    public int allowedMoves;
    public Orientation orientation;

    public Zone(Map map, int n)
    {
        super(map, n);
    }
}
