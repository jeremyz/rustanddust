package ch.asynk.rustanddust.game.states;

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
        map.hexUnselect(selectedHex);
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
