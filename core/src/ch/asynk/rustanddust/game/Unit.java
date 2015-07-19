package ch.asynk.rustanddust.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.rustanddust.engine.Pawn;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.HeadedPawn;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex.Terrain;

import ch.asynk.rustanddust.RustAndDust;

public class Unit extends HeadedPawn
{
    public static final int MOVE        = 0;
    public static final int TARGET      = 1;
    public static final int FIRE        = 2;
    public static final int MAY_FIRE    = 3;
    public static final int ACE         = 4;
    public static final int HQ          = 5;
    public static final int HAS_FIRED   = 6;
    public static final int HAS_MOVED   = 7;

    public static final int FLANK_ATTACK_BONUS = 1;

    public enum UnitType implements Pawn.PawnType
    {
        HARD_TARGET,
        HARD_TARGET_HQ,
        INFANTRY,
        AT_GUN,
        ARTILLERY
    }

    public enum UnitId implements Pawn.PawnId
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
    public boolean ace;
    private boolean hasMoved;
    private boolean hasFired;

    protected Unit(Army army, String pawn, String head, TextureAtlas pawns, TextureAtlas overlays)
    {
        super(army, pawn, head, pawns, overlays);
        ace = false;

    }

    private void commonSetup()
    {
        mpLeft = mp;
        enableOverlay(HQ, isHq());
        this.hasMoved = false;
        this.hasFired = false;
        updateDescr();
    }

    private void updateDescr()
    {
        if (cdef == -1)
            this.descr = id.toString() + (ace ? " Ace " : "") + " (" + rng + "-" + def + "-" + mp + ")";
        else
            this.descr = id.toString() + (ace ? " Ace " : "") + " (" + rng + "-" + def + "/" + cdef + "-" + mp + ")";
    }

    // hard tager
    public Unit(Army army, UnitId id, UnitType type, int range, int defense, int movementPoints, String unit, String head, TextureAtlas pawns, TextureAtlas overlays)
    {
        this(army, unit, head, pawns, overlays);
        this.rng = range;
        this.def = defense;
        this.cdef = -1;
        this.mp = movementPoints;
        this.id = id;
        this.type = type;
        commonSetup();
    }

    // soft tager
    public Unit(Army army, UnitId id, UnitType type, int range, int defense, int concealedDefense, int movementPoints, String unit, String head, TextureAtlas pawns, TextureAtlas overlays)
    {
        this(army, unit, head, pawns, overlays);
        this.rng = range;
        this.def = defense;
        this.cdef = concealedDefense;
        this.mp = movementPoints;
        this.id = id;
        this.type = type;
        commonSetup();
    }

    public Army getArmy()
    {
        return (Army) getFaction();
    }

    public Hex getHex()
    {
        return (Hex) getTile();
    }

    public boolean isAce()
    {
        return ace;
    }

    public void setAce(boolean ace)
    {
        this.ace = ace;
        updateDescr();
        enableOverlay(ACE, ace);
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
    public int getEngagementRangeFrom(Tile tile)
    {
        if (!isA(UnitType.INFANTRY) && tile.isA(Terrain.HILLS))
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
    public int getDefense(Tile tile)
    {
        if (!isHardTarget() && (tile.isA(Terrain.HILLS) || tile.isA(Terrain.WOODS) || tile.isA(Terrain.TOWN)))
            return cdef;

        return def;
    }

    @Override
    public boolean isUnit()
    {
        return true;
    }

    @Override
    public boolean isA(PawnId i)
    {
        return (id == i);
    }

    @Override
    public boolean isA(PawnType t)
    {
        return (type == t);
    }

    @Override
    public boolean isHq()
    {
        return isA(UnitType.HARD_TARGET_HQ);
    }

    @Override
    public boolean isHqOf(Pawn other)
    {
        if (isA(UnitId.GE_PANZER_IV_HQ) && other.isA(UnitId.GE_PANZER_IV)) return true;
        if (isA(UnitId.US_PERSHING_HQ) && other.isA(UnitId.US_PERSHING)) return true;
        if (isA(UnitId.US_SHERMAN_HQ) && other.isA(UnitId.US_SHERMAN)) return true;
        return false;
    }

    public void promote()
    {
        if (isA(UnitId.GE_PANZER_IV))
            id = UnitId.GE_PANZER_IV_HQ;
        else if (isA(UnitId.US_PERSHING))
            id = UnitId.US_PERSHING_HQ;
        else if (isA(UnitId.US_SHERMAN))
            id = UnitId.US_SHERMAN_HQ;
        else
            return;

        type = UnitType.HARD_TARGET_HQ;
        enableOverlay(HQ, true);
        updateDescr();
    }

    public void degrade()
    {
        if (isA(UnitId.GE_PANZER_IV_HQ))
            id = UnitId.GE_PANZER_IV;
        else if (isA(UnitId.US_PERSHING_HQ))
            id = UnitId.US_PERSHING;
        else if (isA(UnitId.US_SHERMAN_HQ))
            id = UnitId.US_SHERMAN;
        else
            return;

        type = UnitType.HARD_TARGET;
        enableOverlay(HQ, false);
        updateDescr();
    }

    @Override
    public boolean isHardTarget()
    {
        return (isA(UnitType.HARD_TARGET) || isA(UnitType.HARD_TARGET_HQ) || isA(UnitType.ARTILLERY));
    }

    @Override
    public boolean canRotate()
    {
        return canMove();
    }

    @Override
    public boolean canMove()
    {
        if (isHardTarget()) return !hasMoved;
        return (!hasMoved && !hasFired);
    }

    @Override
    public boolean canEngage()
    {
        if (isHardTarget()) return !hasFired;
        return (!hasMoved && !hasFired);
    }

    @Override
    public boolean canAssistEngagementWithoutLos()
    {
        return isA(UnitType.ARTILLERY);
    }

    @Override
    public boolean canEngage(Pawn other)
    {
        return (isEnemy(other) && canEngage());
    }

    public boolean canHQMove()
    {
        return (isHq() && ((move == null) || (!move.isEnter())));
    }

    public void setMoved()
    {
        hasMoved = true;
        updateOverlays();
    }

    @Override
    public void move()
    {
        int cost = move.cost;

        if (move.roadMarch && (cost > mpLeft))
            cost -= getRoadMarchBonus();

        if (cost > mpLeft)
            RustAndDust.debug("ERROR: Movement point exceeded: " + cost + "/" + mpLeft + " please report");

        if (move.isFinal())
            setMoved();

        mpLeft -= cost;
    }

    @Override
    public void engage()
    {
        hasFired = true;
        updateOverlays();
    }

    @Override
    public void reset()
    {
        super.reset();
        mpLeft = mp;
        hasFired = false;
        hasMoved = false;
        hideHasMoved();
        hideHasFired();
    }

    @Override
    public void revertLastMove()
    {
        hasMoved = false;
        mpLeft = mp;
        updateOverlays();
        move = null;
    }

    private void updateOverlays()
    {
        enableOverlay(HAS_MOVED, !canMove());
        enableOverlay(HAS_FIRED, !canEngage());
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
    public void showHasMoved()      { enableOverlay(HAS_MOVED, true); }
    public void hideHasMoved()      { enableOverlay(HAS_MOVED, false); }
    public void showHasFired()      { enableOverlay(HAS_FIRED, true); }
    public void hideHasFired()      { enableOverlay(HAS_FIRED, false); }
}
