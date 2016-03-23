package ch.asynk.rustanddust.game.states;

public class StateAnimation extends StateCommon
{
    @Override
    public void enterFrom(StateType prevState)
    {
        ctrl.hud.actionButtons.hide();
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
