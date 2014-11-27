package ch.asynk.tankontank.game.states;

public class StateAnimation extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        ctrl.blockMap = true;
        ctrl.hud.actionButtons.hide();
    }

    @Override
    public void leave(StateType nextState)
    {
        ctrl.blockMap = false;
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

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
    }
}
