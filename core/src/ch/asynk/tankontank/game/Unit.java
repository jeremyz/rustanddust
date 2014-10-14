package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.HeadedPawn;
import ch.asynk.tankontank.engine.Orientation;

public class Unit extends HeadedPawn
{
    public static final int DISABLED = 0;

    enum UnitType
    {
        HARD_TARGET,
        HARD_TARGET_HQ,
        INFANTRY,
        AT_GUN
    }

    public int rng;
    public int def;
    public int cdef;
    public int mp;
    public UnitType type;
    public Army army;
    private boolean hasMoved;
    private boolean hasFired;

    protected Unit(TextureAtlas atlas, String pawn, String head)
    {
        super(atlas, pawn, head);
    }

    // hard tager
    public Unit(Army army, UnitType type, int range, int defense, int movementPoints, TextureAtlas atlas, String unit, String head)
    {
        super(atlas, unit, head);
        this.army = army;
        this.rng = range;
        this.def = defense;
        this.mp = movementPoints;
        this.type = type;
        this.hasMoved = false;
        this.hasFired = false;
    }

    // soft tager
    public Unit(Army army, UnitType type, int range, int defense, int concealedDefense, int movementPoints, TextureAtlas atlas, String unit, String head)
    {
        super(atlas, unit, head);
        this.army = army;
        this.rng = range;
        this.def = defense;
        this.cdef = concealedDefense;
        this.mp = movementPoints;
        this.type = type;
        this.hasMoved = false;
        this.hasFired = false;
    }

    public boolean isEnemy(Army other)
    {
        return army.isEnemy(other);
    }

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
        if ((type != UnitType.INFANTRY) && (((Hex) tile).terrain == Hex.Terrain.HILLS))
            return rng + 1;
        return rng;
    }

    @Override
    public int getAngleOfAttack()
    {
        return orientation.getFrontSides();
    }

    @Override
    public int getFlankSides()
    {
        return orientation.getBackSides();
    }

    @Override
    public boolean isHq()
    {
        return (type == UnitType.HARD_TARGET_HQ);
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
    public boolean canRotate()
    {
        if (type == UnitType.HARD_TARGET) return !hasMoved;
        return (!hasMoved && !hasFired);
    }

    @Override
    public boolean canMove()
    {
        if (type == UnitType.HARD_TARGET) return !hasMoved;
        return (!hasMoved && !hasFired);
    }

    @Override
    public boolean canAttack()
    {
        if (type == UnitType.HARD_TARGET) return !hasFired;
        return (!hasMoved && !hasFired);
    }

    @Override
    public boolean canAttack(Pawn other)
    {
        return isEnemy(other);
    }

    @Override
    public void rotate(Orientation o)
    {
        hasMoved = true;
    }

    @Override
    public void move(int cost)
    {
        hasMoved = true;
        if (cost > mp) System.err.println("ERROR: Movement point exceeded: " + cost + "/" + mp + " please report");
    }

    @Override
    public void attack(Pawn target)
    {
        hasFired = true;
    }

    @Override
    public void reset()
    {
        hasFired = false;
        hasMoved = false;
    }

    @Override
    public void revertLastMove()
    {
        hasMoved = false;
    }
}
