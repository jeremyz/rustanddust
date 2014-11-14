package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Unit;

public class StatePromote extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        ctrl.hud.actionButtons.hide();
        Unit unit = ctrl.player.promote(selectedUnit);
        if (unit != null) {
            ctrl.hud.notify(unit + " has been promoted");
            map.promote(selectedUnit, unit);
        }
        done();
    }

    @Override
    public void leave(StateType nextState)
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

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
    }
}
