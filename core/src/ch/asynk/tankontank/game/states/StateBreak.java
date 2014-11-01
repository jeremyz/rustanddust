package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.State.StateType;

public class StateBreak extends StateCommon
{
    private boolean done;
    private Orientation o = Orientation.KEEP;

    @Override
    public void enter(boolean flag)
    {
        done = false;
        activeUnit = null;
        ctrl.hud.hide();
        ctrl.hud.show(false, false, false, false, true, false);
        map.showBreakPawns();
    }

    @Override
    public void leave(StateType nextState)
    {
        map.hideBreakPawns();
        map.hideMove(to);
        map.hideDirections(to);
        map.hideOrientation(to);
        if (activeUnit != null) map.hideMove(activeUnit.getHex());
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        if (activeUnit == null) {
            Unit unit = upHex.getUnit();
            if (map.breakPawns.contains(unit)) {
                activeUnit = unit;
                map.showMove(upHex);
                map.showMove(to);
                map.showDirections(to);
                map.hideBreakPawns();
            }
        } else {
            o = Orientation.fromAdj(to.getCol(), to.getRow(), downHex.getCol(), downHex.getRow());

            if (o == Orientation.KEEP) return;

            if (ctrl.cfg.mustValidate) {
                map.hideDirections(to);
                map.showOrientation(to, o);
                ctrl.hud.show(false, false, false, false, true, false);
            } else
                doRotation(o);
        }
    }

    @Override
    public void abort()
    {
    }

    @Override
    public void done()
    {
        doRotation(o);
        super.done();
    }

    private void doRotation(Orientation o)
    {
        if (done || (activeUnit == null)) return;
        done = true;

        if (map.collectPossiblePaths(activeUnit, to) == 1) {
            map.possiblePaths.orientation = o;
            map.movePawn(activeUnit, o);
            ctrl.setAnimationCount(1);
            ctrl.setState(StateType.ANIMATION);
        } else
            System.err.println("That's very wrong there should be only one path");
    }
}
