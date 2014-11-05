package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Unit;

public class StatePromote extends StateCommon
{
    @Override
    public void enter(boolean flag)
    {
        ctrl.hud.hide();
        Unit p = ctrl.player.promote(selectedUnit);
        if (p != null) {
            ctrl.hud.notify(p + " has been promoted");
            map.promote(selectedUnit, p);
        }
        done();
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
    public void abort()
    {
    }

    @Override
    public void done()
    {
        super.done();
    }
}
