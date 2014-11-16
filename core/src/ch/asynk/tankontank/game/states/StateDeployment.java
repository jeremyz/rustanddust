package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.engine.EntryPoint;
import ch.asynk.tankontank.engine.PawnSet;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

import ch.asynk.tankontank.TankOnTank;

public class StateDeployment extends StateCommon
{
    private boolean done;
    private EntryPoint entryPoint;
    private PawnSet deployedUnits = new PawnSet(map, 10);

    @Override
    public void enter(StateType prevState)
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        done = false;
        entryPoint = null;
        selectedHex = null;
        ctrl.hud.actionButtons.hide();
        ctrl.hud.playerInfo.unitDock.show();
        ctrl.hud.playerInfo.blockEndOfTurn(true);
    }

    @Override
    public void leave(StateType nextState)
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        if (entryPoint != null)
            entryPoint.enable(Hex.AREA, false);
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
        Unit unit = ctrl.hud.playerInfo.getDockUnit();
        if (!done && (unit != null) && (unit != activeUnit)) {
            changeUnit(unit);
        } else if (selectedUnit != null) {
            Orientation o = Orientation.fromAdj(selectedHex, upHex);
            if (o != Orientation.KEEP)
                doRotation(o);
        } else if (!done && (entryPoint != null) && (upHex != null)) {
            if (upHex.isEmpty() && entryPoint.contains(upHex))
                unitEnter(activeUnit);
        } else {
            unit = downHex.getUnit();
            if (deployedUnits.contains(unit)) {
                selectedUnit = unit;
                selectedHex = downHex;
                showRotation();
            }
        }
    }

    private void changeUnit(Unit unit)
    {
        activeUnit = unit;
        if (entryPoint != null) entryPoint.enable(Hex.AREA, false);
        entryPoint = ctrl.battle.getEntryPoint(activeUnit);
        entryPoint.enable(Hex.AREA, true);
    }

    private void undo()
    {
        map.unselectHex(selectedHex);
        map.hideDirections(selectedHex);
        map.leaveBoard(selectedUnit);
        ctrl.player.revertUnitEntry(selectedUnit);
        activeUnit = null;
        selectedUnit = null;
    }

    private void unitEnter(Unit unit)
    {
        selectedUnit = unit;
        selectedHex = upHex;
        ctrl.player.unitEntry(unit);
        map.enterBoard(unit, upHex, entryPoint.orientation);
        deployedUnits.add(unit);
        entryPoint.enable(Hex.AREA, false);
        showRotation();
    }

    private void showRotation()
    {
        map.selectHex(selectedHex);
        map.showDirections(selectedHex);
        ctrl.hud.playerInfo.unitDock.hide();
        ctrl.hud.actionButtons.show(Buttons.ROTATE.b | Buttons.ABORT.b);
        ctrl.hud.actionButtons.setOn(Buttons.ROTATE);
    }

    private void doRotation(Orientation o)
    {
        map.unselectHex(selectedHex);
        map.hideDirections(selectedHex);
        selectedUnit.setRotation(o.r());
        ctrl.hud.actionButtons.hide();
        ctrl.hud.playerInfo.unitDock.show();
        entryPoint = null;
        activeUnit = null;
        selectedUnit = null;
        if (ctrl.checkDeploymentDone()) {
            done = true;
            ctrl.hud.actionButtons.show(Buttons.DONE.b);
        }
    }
}
