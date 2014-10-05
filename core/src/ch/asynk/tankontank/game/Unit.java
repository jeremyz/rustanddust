package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
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
    public int getMovementPoints()
    {
        return mp;
    }

    @Override
    public int getRoadMarchBonus()
    {
        return 1;
    }

    @Override
    public int getAttackRangeFrom(Tile tile)
    {
        if (ht && ((Hex) tile).terrain == Hex.Terrain.HILLS) return rng + 1;
        return rng;
    }

    @Override
    public int getAngleOfAttack()
    {
        return orientation.getFrontSides();
    }

    @Override
    public boolean isUnit()
    {
        return true;
    }

    @Override
    public boolean isEnemy(Pawn other)
    {
        return army.isEnemy(((Unit) other).army);
    }

    @Override
    public boolean canAttack(Pawn other)
    {
        return isEnemy(other);
    }

    // hard tager
    public Unit(Army army, boolean hq, int range, int defense, int movementPoints, TextureAtlas atlas, String unit, String head)
    {
        super(atlas, unit, head);
        this.army = army;
        this.hq = hq;
        this.rng = range;
        this.def = defense;
        this.mp = movementPoints;
        this.ht = true;
    }

    // soft tager
    public Unit(Army army, boolean hq, int range, int defense, int concealedDefense, int movementPoints, TextureAtlas atlas, String unit, String head)
    {
        super(atlas, unit, head);
        this.army = army;
        this.hq = hq;
        this.rng = range;
        this.def = defense;
        this.cdef = concealedDefense;
        this.mp = movementPoints;
        this.ht = false;
    }
}
