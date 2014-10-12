package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.GameState.State;

public class GameStateAnimation extends GameStateCommon
{
    @Override
    public void enter(boolean flag)
    {
    }

    @Override
    public void leave(State nextState)
    {
        if (nextState != State.SELECT) {
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
