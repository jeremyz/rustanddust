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

    public enum UnitType
    {
        HARD_TARGET,
        HARD_TARGET_HQ,
        INFANTRY,
        AT_GUN,
        ARTILLERY
    }

    public enum UnitId
    {
        GE_AT_GUN("German Anti-Tank Gun"),
        GE_INFANTRY("German Infantry"),
        GE_KINGTIGER("German King Tiger"),
        GE_PANZER_IV("German Panzer IV"),
        GE_PANZER_IV_HQ("German Panzer IV HQ"),
        GE_TIGER("German Tiger"),
        GE_WESPE("German Wespe"),

        US_AT_GUN("US Anti-Tank Gun"),
        US_INFANTRY("US Infantry"),
        US_PERSHING("US Pershing"),
        US_PERSHING_HQ("US Pershing HQ"),
        US_PRIEST("US Priest"),
        US_SHERMAN("US Sherman"),
        US_SHERMAN_HQ("US Sherman HQ"),
        US_WOLVERINE("US Wolverine");

        private String s;
        UnitId(String s) { this.s = s; }
        public String toString() { return s; }
    }

    public int rng;
    public int def;
    public int cdef;
    public int mp;
    public UnitType type;
    public UnitId id;
    private boolean hasMoved;
    private boolean hasFired;

    protected Unit(Army army, TextureAtlas atlas, String pawn, String head)
    {
        super(army, atlas, pawn, head);
    }

    // hard tager
    public Unit(Army army, UnitId id, UnitType type, int range, int defense, int movementPoints, TextureAtlas atlas, String unit, String head)
    {
        super(army, atlas, unit, head);
        this.rng = range;
        this.def = defense;
        this.mp = movementPoints;
        this.id = id;
        this.type = type;
        this.hasMoved = false;
        this.hasFired = false;
        this.descr = id.toString() + " (" + rng + "-" + def + "-" + mp + ")";
    }

    // soft tager
    public Unit(Army army, UnitId id, UnitType type, int range, int defense, int concealedDefense, int movementPoints, TextureAtlas atlas, String unit, String head)
    {
        super(army, atlas, unit, head);
        this.rng = range;
        this.def = defense;
        this.cdef = concealedDefense;
        this.mp = movementPoints;
        this.id = id;
        this.type = type;
        this.hasMoved = false;
        this.hasFired = false;
        this.descr = id.toString() + " (" + rng + "-" + def + "/" + cdef + "-" + mp + ")";
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
    public boolean isHardTarget()
    {
        return ((type == UnitType.HARD_TARGET) || (type == UnitType.HARD_TARGET_HQ));
    }

    @Override
    public boolean isHqOf(Pawn other)
    {
        if ((id == UnitId.GE_PANZER_IV_HQ) && (((Unit)other).id == UnitId.GE_PANZER_IV)) return true;
        if ((id == UnitId.US_PERSHING_HQ) && (((Unit)other).id == UnitId.US_PERSHING)) return true;
        if ((id == UnitId.US_SHERMAN_HQ) && (((Unit)other).id == UnitId.US_SHERMAN)) return true;
        return false;
    }

    @Override
    public boolean isUnit()
    {
        return true;
    }

    @Override
    public boolean canRotate()
    {
        if (isHardTarget()) return !hasMoved;
        return (!hasMoved && !hasFired);
    }

    @Override
    public boolean canMove()
    {
        if (isHardTarget()) return !hasMoved;
        return (!hasMoved && !hasFired);
    }

    @Override
    public boolean canAttack()
    {
        if (isHardTarget()) return !hasFired;
        return (!hasMoved && !hasFired);
    }

    @Override
    public boolean canAssistAttackWithoutLos()
    {
        return (type == UnitType.ARTILLERY);
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
