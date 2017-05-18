package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.Ctrl.MsgType;

public class StateReplay extends StateCommon
{
    private Order order = null;

    @Override
    public void enterFrom(StateType prevState)
    {
        if (this.order != null)
            replayStep();

        Order o = map.stepReplay();
        if (o == null) {
            ctrl.postReplayDone(replayDone());
            order = null;
        } else {
            this.order = o;
            setup();
            ctrl.postOrder(o, StateType.REPLAY);
        }
    }

    private void setup()
    {
        int s = order.activables.size();

        switch (order.type)
        {
            case MOVE:
                select(order.leader);
                break;
            case ENGAGE:
                to = order.engagement.defender.getHex();
                break;
            case PROMOTE:
            default:
                break;
        }
    }

    private void replayStep()
    {
        switch (order.type)
        {
            case MOVE:
                moveReplayStep();
                break;
            case ENGAGE:
            case PROMOTE:
            default:
                break;
        }
    }

    private void moveReplayStep()
    {
        switch(order.move.type)
        {
            case SET:
                ctrl.sendMsg(MsgType.UNIT_DEPLOYED, order.move.pawn);
                break;
            case REGULAR:
            case EXIT:
            case ENTER:
                break;
        }
    }

    private StateType replayDone()
    {
        if (order == null) return null;

        boolean more = (order.activables.size() > 0);
        StateType nextState = null;
        switch (order.type)
        {
            case MOVE:
                nextState = moveReplayDone(more);
                break;
            case ENGAGE:
                if (more) nextState = StateType.ENGAGE;
                break;
            case PROMOTE:
            default:
                break;
        }
        return nextState;
    }

    private StateType moveReplayDone(boolean more)
    {
        StateType nextState = null;
        switch(order.move.type)
        {
            case REGULAR:
            case EXIT:
                if (more) nextState = StateType.MOVE;
                break;
            case SET:
                nextState = StateType.DEPLOYMENT;
                break;
            case ENTER:
                break;
        }
        return nextState;
    }
}
