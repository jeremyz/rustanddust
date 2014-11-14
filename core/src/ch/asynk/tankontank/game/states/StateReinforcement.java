package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.EntryPoint;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

public class StateReinforcement extends StateCommon
{
    private EntryPoint entryPoint;

    @Override
    public void enter(StateType prevState)
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        entryPoint = null;
        selectedHex = null;
        ctrl.hud.playerInfo.unitDock.show();
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
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Unit unit = ctrl.hud.playerInfo.getDockUnit();
        if ((unit != null) && (unit != activeUnit))
            changeUnit(unit);
        else if ((entryPoint != null) && upHex.isEmpty() && entryPoint.contains(upHex))
            unitEnter(activeUnit);
        else
            ctrl.setState(StateType.SELECT);
    }

    @Override
    public StateType abort()
    {
        return StateType.ABORT;
    }

    @Override
    public StateType done()
    {
        return StateType.DONE;
    }

    private void changeUnit(Unit unit)
    {
        activeUnit = unit;
        if (entryPoint != null)
            entryPoint.enable(Hex.AREA, false);
        entryPoint = ctrl.battle.getEntryPoint(activeUnit);
        entryPoint.enable(Hex.AREA, true);
        ctrl.hud.actionButtons.show(((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
    }

    private void unitEnter(Unit unit)
    {
        selectedUnit = unit;
        selectedHex = upHex;
        map.selectHex(selectedHex);
        entryPoint.enable(Hex.AREA, false);
        ctrl.player.unitEntry(unit);
        if (map.enterBoard(unit, upHex, entryPoint.allowedMoves)) {
            if (unit.getMovementPoints() > 0)
                ctrl.setState(StateType.MOVE);
            else
                ctrl.setState(StateType.ROTATE);
        } else {
            ctrl.hud.notify("Impossible to enter map at that position");
        }
    }
}
