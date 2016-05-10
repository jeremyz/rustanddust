package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Ctrl.MsgType;

public class StateReinforcement extends StateCommon
{
    private Zone entryZone;

    @Override
    public void enterFrom(StateType prevState)
    {
        map.clearMoves();
        map.clearUnits();
        entryZone = null;
        activeUnit = null;
        selectedHex = null;
        selectedUnit = null;
        ctrl.hud.playerInfo.unitDock.show();
    }

    @Override
    public boolean processMsg(MsgType msg, Object data)
    {
        switch(msg)
        {
            case UNIT_DOCK_SELECT:
                showEntryZone((Unit) data);
                return true;
        }

        return false;
    }

    @Override
    public void touch(Hex hex)
    {
        if ((entryZone != null) && hex.isEmpty() && entryZone.contains(hex))
            unitEnter(selectedUnit, hex);
    }

    private void showEntryZone(Unit unit)
    {
        selectedUnit = unit;
        if (entryZone != null)
            entryZone.enable(Hex.AREA, false);
        entryZone = unit.entryZone;
        entryZone.enable(Hex.AREA, true);
    }

    private void unitEnter(Unit unit, Hex hex)
    {
        activeUnit = unit;
        selectedHex = hex;
        map.enterBoard(unit, hex, entryZone);
        entryZone.enable(Hex.AREA, false);
        ctrl.battle.getPlayer().reinforcement.remove(unit);
        ctrl.hud.playerInfo.unitDock.hide();
        ctrl.hud.update();
        ctrl.post(StateType.MOVE);
        entryZone = null;
    }
}
