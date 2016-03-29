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
        switch (order.type) {
            case MOVE:
                selectedUnit = order.activable.get(order.activable.size() - 1);
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
        StateType next = StateType.DONE;

        if (map.unitsActivableSize() <= 0)
            return next;

        switch (order.type) {
            case MOVE:
                next = StateType.MOVE;
                break;
            case ENGAGE:
                next = StateType.BREAK;
                break;
            default:
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
