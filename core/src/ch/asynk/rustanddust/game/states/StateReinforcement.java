package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit;

public class StateReinforcement extends StateCommon
{
    private Zone entryZone;

    @Override
    public void enterFrom(StateType prevState)
    {
        map.clearAll();
        if (selectedHex != null)
            map.hexUnselect(selectedHex);
        entryZone = null;
        selectedHex = null;
        ctrl.hud.playerInfo.unitDock.show();
    }

    @Override
    public void leaveFor(StateType nextState)
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
    public void touch(Hex hex)
    {
        Unit unit = ctrl.hud.playerInfo.unitDock.selectedUnit;
        if (hex == null)
            changeUnit(unit);
        else if ((entryZone != null) && hex.isEmpty() && entryZone.contains(hex))
            unitEnter(activeUnit, hex);
        else
            ctrl.post(StateType.SELECT);
    }

    private void changeUnit(Unit unit)
    {
        activeUnit = unit;
        if (entryZone != null)
            entryZone.enable(Hex.AREA, false);
        entryZone = activeUnit.entryZone;
        entryZone.enable(Hex.AREA, true);
    }

    private void unitEnter(Unit unit, Hex hex)
    {
        selectedUnit = unit;
        selectedHex = hex;
        map.hexSelect(selectedHex);
        entryZone.enable(Hex.AREA, false);
        if (map.enterBoard(unit, hex, entryZone.allowedMoves)) {
            if (unit.getMovementPoints() > 0)
                ctrl.post(StateType.MOVE);
            else
                ctrl.post(StateType.ROTATE);
        } else {
            ctrl.hud.notify("Can not enter the map at that position");
        }
    }
}
