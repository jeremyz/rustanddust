package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.Ctrl.MsgType;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateEngage extends StateCommon
{
    // selectedUnit -> fire leader
    // activeUnit -> target / break

    private boolean breakMove = false;

    @Override
    public void enterFrom(StateType prevState)
    {
        if (prevState == StateType.SELECT) {
            breakMove = false;
            if ((to != null) && (activeUnit() != null)) {
                // quick fire
                selectTarget(activeUnit(), to);
            }
            selectedUnit().showAttack();
            map.hexSelect(selectedHex);
        } else {
            breakMove = true;
            activate(null);
            ctrl.hud.actionButtons.show(Buttons.DONE.b);
            ctrl.hud.notify("Break Through possible", Position.MIDDLE_CENTER);
            map.unitsActivableShow();
            map.hexUnselect(selectedHex);
        }
    }

    @Override
    public boolean processMsg(MsgType msg, Object data)
    {
        switch(msg)
        {
            case OK:
                if (breakMove)
                    abortBreakMove();
                return true;
        }

        return false;
    }

    @Override
    public void touch(Hex hex)
    {
        Unit unit = hex.getUnit();

        if (!breakMove) {
            if (unit == selectedUnit())
                abort();
            else if ((activeUnit() == null) && map.unitsTargetContains(unit))
                selectTarget(unit, hex);
            else if (unit == activeUnit())
                engage();
            else if ((activeUnit() != null) && map.unitsActivableContains(unit))
                map.toggleAssist(unit);
        } else {
            if (activeUnit() == null) {
                if (map.unitsActivableContains(unit))
                    selectBreakUnit(unit);
            } else {
                Orientation o = Orientation.fromAdj(to, hex);
                if (o == Orientation.KEEP)
                    unselectBreakUnit();
                else
                    doBreakMove(o);
            }

        }
    }

    private void selectTarget(Unit unit, Hex hex)
    {
        to = hex;
        activate(unit);
        map.unitsTargetHide();
        activeUnit().showTarget();
        map.collectAssists(selectedUnit(), activeUnit(), ctrl.battle.getPlayer().units);
        map.unitsAssistShow();
    }

    private void engage()
    {
        activeUnit().hideTarget();
        selectedUnit().hideAttack();
        map.unitsAssistHide();
        map.hexUnselect(selectedHex);
        Order order = map.getEngageOrder(selectedUnit(), activeUnit());

        if (order.cost == 0)
            ctrl.postOrder(order, StateType.ENGAGE);
        else {
            order.cost = order.engagement.cost;
            ctrl.postOrder(order);
        }
    }

    private void abort()
    {
        map.unitsAssistHide();
        map.unitsTargetHide();
        activeUnit().hideTarget();
        selectedUnit().hideAttack();
        map.hexUnselect(selectedHex);
        map.unitsActivatedClear();
        ctrl.postActionAborted();
    }

    private void selectBreakUnit(Unit unit)
    {
        activate(unit);
        map.hexMoveShow(to);
        map.hexMoveShow(unit.getHex());
        map.hexDirectionsShow(to);
        map.unitsActivableHide();
    }

    private void unselectBreakUnit()
    {
        map.hexMoveHide(to);
        map.hexMoveHide(activeUnit().getHex());
        map.hexDirectionsHide(to);
        map.unitsActivableShow();
        activate(null);
    }

    private void doBreakMove(Orientation o)
    {
        map.hexMoveHide(to);
        map.hexMoveHide(activeUnit().getHex());
        map.hexDirectionsHide(to);
        map.pathsInit(activeUnit());
        map.pathsBuildShortest(to);
        map.pathsSetOrientation(o);
        ctrl.postOrder(map.getMoveOrder(activeUnit(), false));
    }

    private void abortBreakMove()
    {
        if (activeUnit() != null)
            map.hexMoveHide(activeUnit().getHex());
        map.hexMoveHide(to);
        map.hexDirectionsHide(to);
        map.unitsActivableHide();
        ctrl.postOrder(Order.END);
    }
}
