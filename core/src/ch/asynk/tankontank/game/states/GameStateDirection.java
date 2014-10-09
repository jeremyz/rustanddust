package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;

public class GameStateDirection extends GameStateCommon
{
    @Override
    public void enter(boolean reset)
    {
        map.showFinalPath(to, true);
    }

    @Override
    public void leave()
    {
        map.showMoveAssists(false);
        map.showFinalPath(to, false);
        if (to.x != -1) unselectHex(to);
        if (from.x != -1) unselectHex(from);
        to.set(-1, -1);
        from.set(-1, -1);
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Orientation o = Orientation.fromAdj(to.x, to.y, downHex.x, downHex.y);

        if (o != Orientation.KEEP) {
            map.movePawn(pawn, o);
            ctrl.hud.hide();
            ctrl.setState(State.ANIMATION);
        }
    }

    @Override
    public void abort()
    {
        super.abort();
    }
}
