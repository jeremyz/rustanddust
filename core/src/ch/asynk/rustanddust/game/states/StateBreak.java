package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

import ch.asynk.rustanddust.RustAndDust;

public class StateBreak extends StateCommon
{
    private Orientation o = Orientation.KEEP;

    @Override
    public void enter(StateType prevState)
    {
        activeUnit = null;
        ctrl.hud.actionButtons.show(Buttons.DONE.b);
        ctrl.hud.notify("Break Through possible", Position.MIDDLE_CENTER);
        map.unitsBreakThroughShow();
    }

    @Override
    public void leave(StateType nextState)
    {
        map.unitsBreakThroughHide();
        map.hexMoveHide(to);
        map.hexDirectionsHide(to);
        if (activeUnit != null) map.hexMoveHide(activeUnit.getHex());
    }

    @Override
    public StateType abort()
    {
        return StateType.ABORT;
    }

    @Override
    public StateType execute()
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
        // TODO : cancel preview move before showing rotation
        if (activeUnit == null) {
            Unit unit = upHex.getUnit();
            if (map.unitsBreakThroughContains(unit)) {
                activeUnit = unit;
                map.hexMoveShow(upHex);
                map.hexMoveShow(to);
                map.hexDirectionsShow(to);
                map.unitsBreakThroughHide();
            }
        } else {
            o = Orientation.fromAdj(to, upHex);

            if (o == Orientation.KEEP) return;

            doRotation(o);
            ctrl.setState(StateType.ANIMATION);
        }
    }

    private void doRotation(Orientation o)
    {
        if (activeUnit == null) return;

        map.pathsInit(activeUnit);
        if (map.pathsBuild(to) == 1) {
            map.pathsSetOrientation(o);
            map.moveUnit(activeUnit);
            ctrl.setAfterAnimationState(StateType.DONE);
        } else
            RustAndDust.debug("That's very wrong there should be only one path");
    }
}
