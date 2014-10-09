package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;

public class GameStateRotate extends GameStateCommon
{
    @Override
    public void enter()
    {
        map.showDirections(hex, true);
        ctrl.hud.show(true, false, false, ctrl.cfg.mustValidate, ctrl.cfg.canCancel);
    }

    @Override
    public void leave()
    {
        unselectHex(hex);
        map.showDirections(hex, false);
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Orientation o = Orientation.fromAdj(hex.x, hex.y, downHex.x, downHex.y);

        if (o != Orientation.KEEP) {
            if (pawn.getOrientation() != o) {
                map.rotatePawn(pawn, o);
                ctrl.setState(State.ANIMATION);
            } else {
                ctrl.animationDone();
            }
            ctrl.hud.hide();
        }

    }

    @Override
    public void abort()
    {
        super.abort();
    }
}
