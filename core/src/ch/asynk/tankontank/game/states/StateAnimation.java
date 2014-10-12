package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.State.StateType;

public class StateAnimation extends StateCommon
{
    @Override
    public void enter(boolean flag)
    {
    }

    @Override
    public void leave(StateType nextState)
    {
        if (nextState != StateType.SELECT) {
            from.set(-1, -1);
            to.set(-1, -1);
        }
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
    public void abort()
    {
    }

    @Override
    public void done()
    {
        super.done();
    }
}
