package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;

public abstract class Map1Units extends Map0Hex
{
    protected final UnitList targetUnits;
    protected final UnitList activableUnits;
    protected final UnitList activatedUnits;

    public Map1Units(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);

        targetUnits = new UnitList(10);
        activableUnits = new UnitList(6);
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
        targetUnits.clear();
        activableUnits.clear();
        activatedUnits.clear();
    }

    public int collectMoveable(Unit unit)
    {
        activableUnits.clear();
        if (unit.canHQMove())
            collectMoveAssists(unit, activableUnits.asPawns());

        if (unit.canMove())
            activableUnits.add(unit);

        return activableUnits.size();
    }

    public int collectTargets(Unit unit, UnitList foes)
    {
        targetUnits.clear();
        if (unit.canEngage())
            return collectPossibleTargets(unit, foes.asPawns(), targetUnits.asPawns());
        return 0;
    }

    public int collectAssists(Unit unit, Unit target, UnitList units)
    {
        activableUnits.clear();
        int s = collectAttackAssists(unit, target, units.asPawns(), activableUnits.asPawns());
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

    public Unit getFirstActivable()     { return activableUnits.get(0); }

    public void unitsTargetClear()      { targetUnits.clear(); }
    public void unitsActivatedClear()   { activatedUnits.clear(); }

    public int unitsActivatedSize()     { return activatedUnits.size(); }
    public int unitsActivableSize()     { return activableUnits.size(); }

    public boolean unitsTargetContains(Unit unit)       { return targetUnits.contains(unit); }
    public boolean unitsActivableContains(Unit unit)    { return activableUnits.contains(unit); }

    public void unitsTargetShow()       { unitsShowOverlay(targetUnits, Unit.TARGET, true); }
    public void unitsTargetHide()       { unitsShowOverlay(targetUnits, Unit.TARGET, false); }
    public void unitsAssistShow()       { unitsShowOverlay(activableUnits, Unit.MAY_FIRE, true); }
    public void unitsAssistHide()       { unitsShowOverlay(activableUnits, Unit.MAY_FIRE, false); unitsShowOverlay(activableUnits, Unit.FIRE, false); }
    public void unitsActivableShow()    { unitsShowOverlay(activableUnits, Unit.ACTIVEABLE, true); }
    public void unitsActivableHide()    { unitsShowOverlay(activableUnits, Unit.ACTIVEABLE, false); }

    private void unitsShowOverlay(UnitList units, int overlay, boolean on)
    {
        for (Unit unit : units)
            unit.enableOverlay(overlay, on);
    }
}
