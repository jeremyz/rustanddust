package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;

public abstract class Map1Units extends Map0Hex
{
    protected final UnitList moveableUnits;
    protected final UnitList targetUnits;
    protected final UnitList assistUnits;
    protected final UnitList breakthroughUnits;
    protected final UnitList activatedUnits;

    public Map1Units(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);

        moveableUnits = new UnitList(6);
        targetUnits = new UnitList(10);
        assistUnits = new UnitList(6);
        breakthroughUnits = new UnitList(4);
        activatedUnits = new UnitList(7);
    }

    @Override
    public void dispose()
    {
        clearUnits();
        super.dispose();
    }

    public void clearUnits()
    {
        moveableUnits.clear();
        targetUnits.clear();
        assistUnits.clear();
        breakthroughUnits.clear();
        activatedUnits.clear();
    }

    public int collectMoveable(Unit unit)
    {
        if (unit.canHQMove())
            collectMoveAssists(unit, moveableUnits.asPawns());
        else
            moveableUnits.clear();

        if (unit.canMove())
            moveableUnits.add(unit);

        return moveableUnits.size();
    }

    public int collectTargets(Unit unit, UnitList foes)
    {
        if (unit.canEngage())
            return collectPossibleTargets(unit, foes.asPawns(), targetUnits.asPawns());

        targetUnits.clear();
        return 0;
    }

    public int collectAssists(Unit unit, Unit target, UnitList units)
    {
        int s = collectAttackAssists(unit, target, units.asPawns(), assistUnits.asPawns());
        activatedUnits.add(unit);
        return s;
    }

    public boolean toggleAssist(Unit unit)
    {
        if (activatedUnits.contains(unit)) {
            activatedUnits.remove(unit);
            unit.hideAttack();
            unit.showAttackAssist();
            return false;
        } else {
            activatedUnits.add(unit);
            unit.showAttack();
            unit.hideAttackAssist();
            return true;
        }
    }

    public Unit unitsMoveableGet(int i) { return moveableUnits.get(i); }

    public void unitsTargetClear()      { targetUnits.clear(); }
    public void unitsActivatedClear()   { activatedUnits.clear(); }

    public int unitsActivatedSize()     { return activatedUnits.size(); }
    public int unitsMoveableSize()      { return moveableUnits.size(); }
    public int unitsBreakThroughSize()  { return breakthroughUnits.size(); }

    public boolean unitsTargetContains(Unit unit)       { return targetUnits.contains(unit); }
    public boolean unitsAssistContains(Unit unit)       { return assistUnits.contains(unit); }
    public boolean unitsMoveableContains(Unit unit)     { return moveableUnits.contains(unit); }
    public boolean unitsBreakThroughContains(Unit unit) { return breakthroughUnits.contains(unit); }

    public void unitsTargetShow()       { unitsShowOverlay(targetUnits, Unit.TARGET, true); }
    public void unitsTargetHide()       { unitsShowOverlay(targetUnits, Unit.TARGET, false); }
    public void unitsAssistShow()       { unitsShowOverlay(assistUnits, Unit.MAY_FIRE, true); }
    public void unitsAssistHide()       { unitsShowOverlay(assistUnits, Unit.MAY_FIRE, false); unitsShowOverlay(assistUnits, Unit.FIRE, false); }
    public void unitsMoveableShow()     { unitsShowOverlay(moveableUnits, Unit.ACTIVEABLE, true); }
    public void unitsMoveableHide()     { unitsShowOverlay(moveableUnits, Unit.ACTIVEABLE, false); }
    public void unitsBreakThroughShow() { unitsShowOverlay(breakthroughUnits, Unit.ACTIVEABLE, true); }
    public void unitsBreakThroughHide() { unitsShowOverlay(breakthroughUnits, Unit.ACTIVEABLE, false); }

    private void unitsShowOverlay(UnitList units, int overlay, boolean on)
    {
        for (Unit unit : units)
            unit.enableOverlay(overlay, on);
    }
}
