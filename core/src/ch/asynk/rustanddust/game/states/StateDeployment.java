package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

import ch.asynk.rustanddust.RustAndDust;

public class StateDeployment extends StateCommon
{
    private boolean completed;
    private Zone entryZone;
    private UnitList deployedUnits = new UnitList(10);

    @Override
    public void enter(StateType prevState)
    {
        if (selectedHex != null)
            map.hexUnselect(selectedHex);
        completed = false;
        entryZone = null;
        selectedHex = null;
        selectedUnit = null;
        ctrl.hud.actionButtons.hide();
        ctrl.hud.playerInfo.unitDock.show();
    }

    @Override
    public void leave(StateType nextState)
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
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Unit unit = ctrl.hud.playerInfo.unitDock.selectedUnit;
        if (!completed && (unit != null) && (unit != activeUnit)) {
            showEntryZone(unit);
        } else if (selectedUnit != null) {
            doRotation(Orientation.fromAdj(selectedHex, upHex));
        } else if (!completed && (entryZone != null) && (upHex != null)) {
            if (upHex.isEmpty() && entryZone.contains(upHex))
                unitEnter(activeUnit);
        } else {
            unit = downHex.getUnit();
            if (deployedUnits.contains(unit)) {
                showRotation(unit, downHex);
                activeUnit = unit;
            }
        }
    }

    private void showEntryZone(Unit unit)
    {
        activeUnit = unit;
        if (entryZone != null) entryZone.enable(Hex.AREA, false);
        entryZone = ctrl.battle.getEntryZone(activeUnit);
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

    private void unitEnter(Unit unit)
    {
        selectedUnit = unit;
        selectedHex = upHex;
        ctrl.battle.getPlayer().reinforcement.remove(unit);
        map.setOnBoard(unit, upHex, entryZone.orientation);
        deployedUnits.add(unit);
        entryZone.enable(Hex.AREA, false);
        showRotation(unit, upHex);
        ctrl.hud.update();
    }

    private void showRotation(Unit unit, Hex hex)
    {
        selectedUnit = unit;
        selectedHex = hex;
        map.hexSelect(selectedHex);
        map.hexDirectionsShow(selectedHex);
        ctrl.hud.playerInfo.unitDock.hide();
        ctrl.hud.actionButtons.show(Buttons.ABORT.b);
    }

    private void doRotation(Orientation o)
    {
        map.hexUnselect(selectedHex);
        map.hexDirectionsHide(selectedHex);

        if (o != Orientation.KEEP)
            map.setOnBoard(selectedUnit, selectedHex, o);

        ctrl.hud.actionButtons.hide();
        ctrl.hud.playerInfo.unitDock.show();
        entryZone = null;
        activeUnit = null;
        selectedUnit = null;
        if (ctrl.checkDeploymentDone())
            completed = true;
    }
}
