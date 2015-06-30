package ch.asynk.creepingarmor.game.states;

import ch.asynk.creepingarmor.game.Unit;

public class StatePromote extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        ctrl.setAfterAnimationState(StateType.DONE);
        ctrl.setState(StateType.ANIMATION);
        map.promoteUnit(selectedUnit);
    }

    @Override
    public void leave(StateType nextState)
    {
        map.unselectHex(selectedHex);
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
    }
}
