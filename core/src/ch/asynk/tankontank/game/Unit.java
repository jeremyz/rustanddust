package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Unit extends PawnImage
{
    public int rng;
    public int def;
    public int cdef;
    public int mp;
    public boolean hq;
    public boolean ht;
    public Army army;

    // hard tager
    public Unit(Army army, boolean hq, int range, int defense, int movementPoints, TextureRegion region)
    {
        super(region);
        this.army = army;
        this.hq = hq;
        this.rng = range;
        this.def = defense;
        this.mp = movementPoints;
        this.ht = true;
    }

    // soft tager
    public Unit(Army army, boolean hq, int range, int defense, int concealedDefense, int movementPoints, TextureRegion region)
    {
        super(region);
        this.army = army;
        this.hq = hq;
        this.rng = range;
        this.def = defense;
        this.cdef = concealedDefense;
        this.mp = movementPoints;
        this.ht = false;
    }
}
