package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;

public class GameStateDirection extends GameStateCommon
{
    @Override
    public void enter()
    {
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Orientation o = Orientation.fromAdj(tmp.x, tmp.y, downHex.x, downHex.y);

        if (o != Orientation.KEEP) {
            map.movePawn(pawn, o);
            clear();
            unselectHex();
            hex.set(tmp.x, tmp.y);
            unselectHex();
            ctrl.setState(State.ANIMATION);
        }
    }

    @Override
    public void abort()
    {
        clear();
        super.abort();
    }

    private void clear()
    {
        map.enableFinalPath(tmp, false);
        map.enableMoveAssist(false);
        if (to.x != -1) unselectHex(to);
        if (from.x != -1) unselectHex(to);
        to.set(-1, -1);
        from.set(-1, -1);
    }
}
