package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.HeadedPawn;

public class Unit extends HeadedPawn
{
    public static final int DISABLED = 0;

    public int rng;
    public int def;
    public int cdef;
    public int mp;
    public boolean hq;
    public boolean ht;
    public Army army;

    @Override
    public int getMvt()
    {
        return mp;
    }

    @Override
    public int roadMarch()
    {
        return 1;
    }

    @Override
    public boolean isEnemy(Pawn other)
    {
        return army.isEnemy(((Unit) other).army);
    }

    // hard tager
    public Unit(Army army, boolean hq, int range, int defense, int movementPoints, TextureRegion unit, TextureRegion head, TextureAtlas overlays)
    {
        super(unit, head, overlays);
        this.army = army;
        this.hq = hq;
        this.rng = range;
        this.def = defense;
        this.mp = movementPoints;
        this.ht = true;
    }

    // soft tager
    public Unit(Army army, boolean hq, int range, int defense, int concealedDefense, int movementPoints, TextureRegion unit, TextureRegion head, TextureAtlas overlays)
    {
        super(unit, head, overlays);
        this.army = army;
        this.hq = hq;
        this.rng = range;
        this.def = defense;
        this.cdef = concealedDefense;
        this.mp = movementPoints;
        this.ht = false;
    }
}
