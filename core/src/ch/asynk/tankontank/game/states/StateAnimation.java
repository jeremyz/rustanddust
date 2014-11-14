package ch.asynk.tankontank.game.states;

public class StateAnimation extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
    }

    @Override
    public void leave(StateType nextState)
    {
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
    }

    @Override
    public StateType abort()
    {
        return StateType.ABORT;
    }

    @Override
    public StateType done()
    {
        return StateType.DONE;
    }
}
