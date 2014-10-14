package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.game.State.StateType;

public class StatePromote extends StateCommon
{
    @Override
    public void enter(boolean flag)
    {
        ctrl.hud.hide();
        Pawn p = ctrl.currentPlayer().promote(selectedPawn);
        if (p != null) {
            map.promote(selectedPawn, p);
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
