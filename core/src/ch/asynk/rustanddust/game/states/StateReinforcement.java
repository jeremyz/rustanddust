package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateReinforcement extends StateCommon
{
    private Zone entryZone;

    @Override
    public void enter(StateType prevState)
    {
        map.clearAll();
        if (selectedHex != null)
            map.hexUnselect(selectedHex);
        entryZone = null;
        selectedHex = null;
        ctrl.hud.playerInfo.unitDock.show();
    }

    @Override
    public void leave(StateType nextState)
    {
        if (selectedHex != null)
            map.hexUnselect(selectedHex);
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
    public StateType execute()
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
        ctrl.hud.actionButtons.show(((cfg.canCancel) ? Buttons.ABORT.b : 0));
    }

    private void unitEnter(Unit unit)
    {
        selectedUnit = unit;
        selectedHex = upHex;
        map.hexSelect(selectedHex);
        entryZone.enable(Hex.AREA, false);
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
