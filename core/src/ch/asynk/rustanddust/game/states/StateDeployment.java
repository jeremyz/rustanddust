package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;
import ch.asynk.rustanddust.game.Ctrl.MsgType;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateDeployment extends StateCommon
{
    private Zone entryZone;
    private UnitList deployedUnits = new UnitList(10);

    @Override
    public void enterFrom(StateType prevState)
    {
        clear();
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
            case CANCEL:
                if (activeUnit() != null)
                    undeployUnit();
                return true;
            case OK:
                deployedUnits.clear();
                ctrl.postTurnDone();
                return true;
            case UNIT_DEPLOYED:
                deployedUnits.add((Unit) data);
                return true;
            case UNIT_UNDEPLOYED:
                ctrl.battle.getPlayer().revertUnitEntry((Unit) data);
                return true;
        }

        return false;
    }

    @Override
    public void touch(Hex hex)
    {
        if (activeUnit() != null) {
            deployUnit(Orientation.fromAdj(selectedHex, hex));
        } else if ((selectedUnit() != null) && (entryZone != null)) {
            if (hex.isEmpty() && entryZone.contains(hex)) {
                showUnit(selectedUnit(), hex);
            }
        } else {
            Unit unit = hex.getUnit();
            if (deployedUnits.contains(unit))
                showRotation(unit, hex);
        }
    }

    private void showEntryZone(Unit unit)
    {
        select(unit);
        if (entryZone != null)
            entryZone.enable(Hex.AREA, false);
        entryZone = unit.entryZone;
        entryZone.enable(Hex.AREA, true);
    }

    private void showUnit(Unit unit, Hex hex)
    {
        activate(unit);
        selectedHex = hex;
        ctrl.battle.getPlayer().reinforcement.remove(unit);
        map.setOnBoard(unit, hex, entryZone.orientation);
        deployedUnits.add(unit);
        entryZone.enable(Hex.AREA, false);
        showRotation(unit, hex);
        ctrl.hud.update();
    }

    private void showRotation(Unit unit, Hex hex)
    {
        activate(unit);
        selectedHex = hex;
        map.hexSelect(selectedHex);
        map.hexDirectionsShow(selectedHex);
        ctrl.hud.playerInfo.unitDock.hide();
        ctrl.hud.actionButtons.show(Buttons.ABORT.b);
    }

    private void deployUnit(Orientation o)
    {
        if (o == Orientation.KEEP)
            o = activeUnit().getOrientation();
        ctrl.postOrder(map.getSetOrder(activeUnit(), selectedHex, o), StateType.DEPLOYMENT);
        clear();
    }

    private void undeployUnit()
    {
        ctrl.postOrder(map.getRevertSetOrder(activeUnit()), StateType.DEPLOYMENT);
        ctrl.hud.update();
        clear();
        ctrl.hud.playerInfo.unitDock.show();
    }

    private void clear()
    {
        if (selectedHex != null) {
            map.hexUnselect(selectedHex);
            map.hexDirectionsHide(selectedHex);
        }
        activate(null);
        entryZone = null;
        selectedHex = null;
        select(null);
        ctrl.hud.actionButtons.hide();
    }
}
