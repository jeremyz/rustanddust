package ch.asynk.tankontank.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Unit extends Pawn
{
    public int rng;
    public int def;
    public int cdef;
    public int mp;
    public boolean hq;
    public boolean ht;

    // hard tager
    public Unit(boolean hq, int range, int defense, int movementPoints, TextureRegion region, HexMap map)
    {
        super(region, map);
        this.hq = hq;
        this.rng = range;
        this.def = defense;
        this.mp = movementPoints;
        this.ht = true;
    }

    // soft tager
    public Unit(boolean hq, int range, int defense, int concealedDefense, int movementPoints, TextureRegion region, HexMap map)
    {
        super(region, map);
        this.hq = hq;
        this.rng = range;
        this.def = defense;
        this.cdef = concealedDefense;
        this.mp = movementPoints;
        this.ht = false;
    }
}
