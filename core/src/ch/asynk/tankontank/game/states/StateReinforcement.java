package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Zone;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

public class StateReinforcement extends StateCommon
{
    private Zone entryZone;

    @Override
    public void enter(StateType prevState)
    {
        map.clearAll();
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        entryZone = null;
        selectedHex = null;
        ctrl.hud.playerInfo.unitDock.show();
    }

    @Override
    public void leave(StateType nextState)
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        if (entryZone != null)
            entryZone.enable(Hex.AREA, false);
        ctrl.hud.playerInfo.unitDock.hide();
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

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Unit unit = ctrl.hud.playerInfo.unitDock.selectedUnit;
        if ((unit != null) && (unit != activeUnit))
            changeUnit(unit);
        else if ((entryZone != null) && upHex.isEmpty() && entryZone.contains(upHex))
            unitEnter(activeUnit);
        else
            ctrl.setState(StateType.SELECT);
    }

    private void changeUnit(Unit unit)
    {
        activeUnit = unit;
        if (entryZone != null)
            entryZone.enable(Hex.AREA, false);
        entryZone = ctrl.battle.getEntryZone(activeUnit);
        entryZone.enable(Hex.AREA, true);
        ctrl.hud.actionButtons.show(((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
    }

    private void unitEnter(Unit unit)
    {
        selectedUnit = unit;
        selectedHex = upHex;
        map.selectHex(selectedHex);
        entryZone.enable(Hex.AREA, false);
        ctrl.player.unitEntry(unit);
        if (map.enterBoard(unit, upHex, entryZone.allowedMoves)) {
            if (unit.getMovementPoints() > 0)
                ctrl.setState(StateType.MOVE);
            else
                ctrl.setState(StateType.ROTATE);
        } else {
            ctrl.hud.notify("Can not enter the map at that position");
        }
    }
}
