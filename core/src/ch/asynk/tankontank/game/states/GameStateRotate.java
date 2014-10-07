package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;

public class GameStateRotate extends GameStateCommon
{
    @Override
    public void enter()
    {
        map.hidePossibles();
        if (hasPawn()) {
            map.enableDirections(hex, true);
        }
    }

    @Override
    public void touchDown()
    {
        if (!hasPawn()) {
            reselect();
            if (hasPawn()) {
                map.enableDirections(hex, true);
            }
        }
    }

    @Override
    public void touchUp()
    {
        if (!hasPawn()) {
            unselectHex();
            return;
        }

        Orientation o = Orientation.fromAdj(hex.x, hex.y, downHex.x, downHex.y);

        if (o != Orientation.KEEP) {
            map.enableDirections(hex, false);
            if (pawn.getOrientation() != o) {
                map.rotatePawn(pawn, o);
                ctrl.setState(State.ANIMATION);
            } else {
                ctrl.animationDone();
            }
        }

    }

    @Override
    public void abort()
    {
        map.enableDirections(hex, false);
        super.abort();
    }
}
