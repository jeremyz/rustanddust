package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.HeadedPawn;
import ch.asynk.tankontank.engine.Orientation;

import ch.asynk.tankontank.TankOnTank;

public class Unit extends HeadedPawn
{
    public static final int MOVE        = 0;
    public static final int TARGET      = 1;
    public static final int FIRE        = 2;
    public static final int MAY_FIRE    = 3;

    public enum UnitType implements Pawn.PawnType
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
    public int mpLeft;
    public UnitType type;
    public UnitId id;
    private boolean hasMoved;
    private boolean hasFired;

    protected Unit(Army army, String pawn, String head, TextureAtlas pawns, TextureAtlas overlays)
    {
        super(army, pawn, head, pawns, overlays);
    }

    // hard tager
    public Unit(Army army, UnitId id, UnitType type, int range, int defense, int movementPoints, String unit, String head, TextureAtlas pawns, TextureAtlas overlays)
    {
        this(army, unit, head, pawns, overlays);
        this.rng = range;
        this.def = defense;
        this.mp = movementPoints;
        mpLeft = mp;
        this.id = id;
        this.type = type;
        this.hasMoved = false;
        this.hasFired = false;
        this.descr = id.toString() + " (" + rng + "-" + def + "-" + mp + ")";
    }

    // soft tager
    public Unit(Army army, UnitId id, UnitType type, int range, int defense, int concealedDefense, int movementPoints, String unit, String head, TextureAtlas pawns, TextureAtlas overlays)
    {
        this(army, unit, head, pawns, overlays);
        this.rng = range;
        this.def = defense;
        this.cdef = concealedDefense;
        this.mp = movementPoints;
        mpLeft = mp;
        this.id = id;
        this.type = type;
        this.hasMoved = false;
        this.hasFired = false;
        this.descr = id.toString() + " (" + rng + "-" + def + "/" + cdef + "-" + mp + ")";
    }

    public Hex getHex()
    {
        return (Hex) getTile();
    }

    @Override
    public int getMovementPoints()
    {
        return mpLeft;
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
    public boolean isUnit()
    {
        return true;
    }

    @Override
    public boolean isA(PawnType t)
    {
        return (type == t);
    }

    @Override
    public boolean isHq()
    {
        return (type == UnitType.HARD_TARGET_HQ);
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
    public boolean isHardTarget()
    {
        return ((type == UnitType.HARD_TARGET) || (type == UnitType.HARD_TARGET_HQ) || (type == UnitType.ARTILLERY));
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
        return (isEnemy(other) && canAttack());
    }

    @Override
    public void move()
    {
        TankOnTank.debug(move.toString());
        if (move.cost > mpLeft) TankOnTank.debug("ERROR: Movement point exceeded: " + move.cost + "/" + mpLeft + " please report");

        if (move.isComplete()) {
            hasMoved = true;
        }
        mpLeft -= move.cost;
    }

    @Override
    public void attack()
    {
        TankOnTank.debug(attack.toString());
        hasFired = true;
    }

    @Override
    public void reset()
    {
        super.reset();
        mpLeft = mp;
        hasFired = false;
        hasMoved = false;
    }

    @Override
    public void revertLastMove()
    {
        hasMoved = false;
    }


    // SHOW / HIDE
    public void showMoveable()      { enableOverlay(MOVE, true); }
    public void hideMoveable()      { enableOverlay(MOVE, false); }
    public void showTarget()        { enableOverlay(TARGET, true); }
    public void hideTarget()        { enableOverlay(TARGET, false); }
    public void showAttack()        { enableOverlay(FIRE, true); }
    public void hideAttack()        { enableOverlay(FIRE, false); }
    public void showAttackAssist()  { enableOverlay(MAY_FIRE, true); }
    public void hideAttackAssist()  { enableOverlay(MAY_FIRE, false); }
}
