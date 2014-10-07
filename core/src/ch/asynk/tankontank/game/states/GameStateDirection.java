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
            selectHex();
            ctrl.setState(State.ANIMATION);
        }
    }

    @Override
    public void abort()
    {
        super.abort();
        clear();
    }

    private void clear()
    {
        map.enableFinalPath(tmp, false);
    }
}
