package ch.asynk.rustanddust.game.states;

public class StatePromote extends StateCommon
{
    @Override
    public void enterFrom(StateType prevState)
    {
        ctrl.setAfterAnimationState(StateType.DONE);
        ctrl.post(StateType.ANIMATION);
        map.promoteUnit(selectedUnit);
    }

    @Override
    public void leaveFor(StateType nextState)
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
