package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Order;

public class StateReplay extends StateCommon
{
    private Order order;

    @Override
    public void enterFrom(StateType prevState)
    {
        Order o = map.stepReplay();
        if (o == null) {
            ctrl.setState(nextState());
        } else {
            this.order = o;
            setup();
            map.replay(order);
            ctrl.setAfterAnimationState(StateType.REPLAY);
            ctrl.setState(StateType.ANIMATION);
        }
    }

    private void setup()
    {
        int s = order.activable.size();

        switch (order.type) {
            case MOVE:
                selectedUnit = ((s > 0) ? order.activable.get(s - 1) : order.unit);
                break;
            case ENGAGE:
                to = order.engagement.defender.getHex();
                if (order.engagement.success) {
                    ctrl.battle.getPlayer().engagementWon += 1;
                    ctrl.battle.getOpponent().casualty(order.engagement.defender);
                } else {
                    ctrl.battle.getPlayer().engagementLost += 1;
                }
                break;
            case PROMOTE:
                break;
            default:
                break;
        }
    }

    private StateType nextState()
    {
        if (map.unitsActivableSize() <= 0)
            return StateType.DONE;

        StateType next = null;

        switch (order.type) {
            case MOVE:
                next = StateType.MOVE;
                break;
            case ENGAGE:
                next = StateType.BREAK;
                break;
            default:
                next = StateType.DONE;
                break;
        }

        return next;
    }

    @Override
    public void leaveFor(StateType nextState)
    {
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
}
