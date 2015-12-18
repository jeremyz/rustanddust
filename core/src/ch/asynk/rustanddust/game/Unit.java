package ch.asynk.rustanddust.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import ch.asynk.rustanddust.engine.Pawn;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.HeadedPawn;
import ch.asynk.rustanddust.game.Hex.Terrain;

import ch.asynk.rustanddust.RustAndDust;

public class Unit extends HeadedPawn
{
    public static final int ACTIVEABLE  = 0;
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
        GE_TIGER("German Tiger"),
        GE_WESPE("German Wespe"),

        US_AT_GUN("US Anti-Tank Gun"),
        US_INFANTRY("US Infantry"),
        US_PERSHING("US Pershing"),
        US_PRIEST("US Priest"),
        US_SHERMAN("US Sherman"),
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
    public boolean hq;
    public boolean ace;
    private boolean hasMoved;
    private boolean hasFired;

    protected Unit(Army army, AtlasRegion body, AtlasRegion head, TextureAtlas overlays)
    {
        super(army, body, head, overlays);
        hq = false;
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
            this.descr = id.toString() + (hq ? " HQ " : "") + (ace ? " Ace " : "") + " (" + rng + "-" + def + "-" + mp + ")";
        else
            this.descr = id.toString() + (hq ? " HQ " : "") + (ace ? " Ace " : "") + " (" + rng + "-" + def + "/" + cdef + "-" + mp + ")";
    }

    public Unit(Army army, UnitId id, UnitType type, boolean hq, boolean ace, int range, int defense, int concealedDefense, int movementPoints, AtlasRegion body, AtlasRegion head, TextureAtlas overlays)
    {
        this(army, body, head, overlays);
        this.hq = hq;
        this.ace = ace;
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
        if (tile.isA(Terrain.DEPRESSION))
            return 1;
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
    public boolean preventDefenseOn(Tile tile)
    {
        if (isA(UnitType.INFANTRY) && (tile.isA(Terrain.WOODS) || tile.isA(Terrain.TOWN)))
            return true;

        return false;
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
    public boolean isHardTarget()
    {
        return (isA(UnitType.HARD_TARGET) || isA(UnitType.ARTILLERY));
    }

    @Override
    public boolean isHq()
    {
        return hq;
    }

    @Override
    public boolean isHqOf(Pawn other)
    {
        return (isHq() && other.isA(id));
    }

    public void promote()
    {
        setHq(true);
    }

    public void degrade()
    {
        setHq(false);
    }

    private void setHq(boolean hq)
    {
        this.hq = hq;
        updateDescr();
        enableOverlay(HQ, hq);
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

    @Override
    public boolean canBreak()
    {
        return isA(UnitType.INFANTRY);
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
    public void showActiveable()    { enableOverlay(ACTIVEABLE, true); }
    public void hideActiveable()    { enableOverlay(ACTIVEABLE, false); }
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
