package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateDeployment extends StateCommon
{
    private Zone entryZone;
    private UnitList deployedUnits = new UnitList(10);

    @Override
    public void enterFrom(StateType prevState)
    {
        if (selectedHex != null)
            map.hexUnselect(selectedHex);
        entryZone = null;
        selectedHex = null;
        selectedUnit = null;
        ctrl.hud.actionButtons.hide();
        ctrl.hud.playerInfo.unitDock.show();
    }

    @Override
    public void leaveFor(StateType nextState)
    {
        selectedUnit = null;
        if (selectedHex != null)
            map.hexUnselect(selectedHex);
        if (entryZone != null)
            entryZone.enable(Hex.AREA, false);
        ctrl.hud.playerInfo.unitDock.hide();
    }

    @Override
    public StateType abort()
    {
        if (activeUnit != null)
            undo();
        return StateType.DEPLOYMENT;
    }

    @Override
    public StateType execute()
    {
        deployedUnits.clear();
        return StateType.DONE;
    }

    @Override
    public void touch(Hex hex)
    {
        Unit unit = ctrl.hud.playerInfo.unitDock.selectedUnit;
        if (hex == null) {
            showEntryZone(unit);
        } else if (selectedUnit != null) {
            deployUnit(Orientation.fromAdj(selectedHex, hex));
        } else if (!ctrl.battle.isDeploymentDone() && (entryZone != null) && (hex != null)) {
            if (hex.isEmpty() && entryZone.contains(hex)) {
                showUnit(activeUnit, hex);
            }
        } else {
            unit = hex.getUnit();
            if (deployedUnits.contains(unit))
                showRotation(unit, hex);
        }
    }

    private void showEntryZone(Unit unit)
    {
        activeUnit = unit;
        if (entryZone != null) entryZone.enable(Hex.AREA, false);
        entryZone = activeUnit.entryZone;
        entryZone.enable(Hex.AREA, true);
    }

    private void undo()
    {
        map.hexUnselect(selectedHex);
        map.hexDirectionsHide(selectedHex);
        map.revertEnter(activeUnit);
        activeUnit = null;
        selectedUnit = null;
        ctrl.hud.update();
    }

    private void showUnit(Unit unit, Hex hex)
    {
        selectedUnit = unit;
        selectedHex = hex;
        ctrl.battle.getPlayer().reinforcement.remove(unit);
        map.showOnBoard(unit, hex, entryZone.orientation);
        deployedUnits.add(unit);
        entryZone.enable(Hex.AREA, false);
        showRotation(unit, hex);
        ctrl.hud.update();
    }

    private void showRotation(Unit unit, Hex hex)
    {
        activeUnit = unit;
        selectedUnit = unit;
        selectedHex = hex;
        map.hexSelect(selectedHex);
        map.hexDirectionsShow(selectedHex);
        ctrl.hud.playerInfo.unitDock.hide();
        ctrl.hud.actionButtons.show(Buttons.ABORT.b);
    }

    private void deployUnit(Orientation o)
    {
        if (o == Orientation.KEEP)
            o = selectedUnit.getOrientation();
        map.setOnBoard(selectedUnit, selectedHex, o);

        entryZone = null;
        activeUnit = null;
        selectedUnit = null;
        map.hexUnselect(selectedHex);
        map.hexDirectionsHide(selectedHex);
        ctrl.hud.actionButtons.hide();
        ctrl.hud.playerInfo.unitDock.show();
        ctrl.post(StateType.DONE);
    }
}
