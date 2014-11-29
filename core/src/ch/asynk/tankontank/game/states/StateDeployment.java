package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Zone;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.UnitList;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

import ch.asynk.tankontank.TankOnTank;

public class StateDeployment extends StateCommon
{
    private boolean done;
    private Zone entryZone;
    private UnitList deployedUnits = new UnitList(10);

    @Override
    public void enter(StateType prevState)
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        done = false;
        entryZone = null;
        selectedHex = null;
        ctrl.hud.actionButtons.hide();
        ctrl.hud.playerInfo.unitDock.show();
        ctrl.hud.playerInfo.blockEndOfTurn(true);
    }

    @Override
    public void leave(StateType nextState)
    {
        selectedUnit = null;
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        if (entryZone != null)
            entryZone.enable(Hex.AREA, false);
        ctrl.hud.playerInfo.unitDock.hide();
    }

    @Override
    public StateType abort()
    {
        undo();
        return StateType.DEPLOYMENT;
    }

    @Override
    public StateType done()
    {
        deployedUnits.clear();
        ctrl.hud.playerInfo.blockEndOfTurn(false);
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
        if (!done && (unit != null) && (unit != activeUnit)) {
            showEntryZone(unit);
        } else if (selectedUnit != null) {
            doRotation(Orientation.fromAdj(selectedHex, upHex));
        } else if (!done && (entryZone != null) && (upHex != null)) {
            if (upHex.isEmpty() && entryZone.contains(upHex))
                unitEnter(activeUnit);
        } else {
            unit = downHex.getUnit();
            if (deployedUnits.contains(unit))
                showRotation(unit, downHex);
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
        map.unselectHex(selectedHex);
        map.hideDirections(selectedHex);
        map.leaveBoard(selectedUnit);
        ctrl.player.revertUnitEntry(selectedUnit);
        activeUnit = null;
        selectedUnit = null;
        ctrl.hud.update();
    }

    private void unitEnter(Unit unit)
    {
        selectedUnit = unit;
        selectedHex = upHex;
        ctrl.player.unitEntry(unit);
        map.enterBoard(unit, upHex, entryZone.orientation);
        deployedUnits.add(unit);
        entryZone.enable(Hex.AREA, false);
        showRotation(unit, upHex);
        ctrl.hud.update();
    }

    private void showRotation(Unit unit, Hex hex)
    {
        selectedUnit = unit;
        selectedHex = hex;
        map.selectHex(selectedHex);
        map.showDirections(selectedHex);
        ctrl.hud.playerInfo.unitDock.hide();
        ctrl.hud.actionButtons.show(Buttons.ABORT.b);
    }

    private void doRotation(Orientation o)
    {
        map.unselectHex(selectedHex);
        map.hideDirections(selectedHex);
        if (o != Orientation.KEEP)
            selectedUnit.setRotation(o.r());
        ctrl.hud.actionButtons.hide();
        ctrl.hud.playerInfo.unitDock.show();
        entryZone = null;
        activeUnit = null;
        selectedUnit = null;
        if (ctrl.checkDeploymentDone())
            done = true;
    }
}
