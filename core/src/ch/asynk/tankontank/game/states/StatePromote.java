package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Unit;

public class StatePromote extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        map.promoteUnit(ctrl.player, selectedUnit);
        ctrl.setAnimationCount(1);
        ctrl.setAfterAnimationState(StateType.SELECT);
        ctrl.setState(StateType.ANIMATION);
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
