package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

import ch.asynk.tankontank.TankOnTank;

public class StateBreak extends StateCommon
{
    private Orientation o = Orientation.KEEP;

    @Override
    public void enter(StateType prevState)
    {
        activeUnit = null;
        ctrl.hud.actionButtons.show(Buttons.DONE.b);
        ctrl.hud.pushNotify("Break move possible");
        map.showBreakUnits();
    }

    @Override
    public void leave(StateType nextState)
    {
        map.hideBreakUnits();
        map.hideMove(to);
        map.hideDirections(to);
        map.hideOrientation(to);
        if (activeUnit != null) map.hideMove(activeUnit.getHex());
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
        if (activeUnit == null) {
            Unit unit = upHex.getUnit();
            if (map.breakUnits.contains(unit)) {
                activeUnit = unit;
                map.showMove(upHex);
                map.showMove(to);
                map.showDirections(to);
                map.hideBreakUnits();
            }
        } else {
            o = Orientation.fromAdj(to, upHex);

            if (o == Orientation.KEEP) return;

            if (ctrl.cfg.mustValidate) {
                map.hideDirections(to);
                map.showOrientation(to, o);
                ctrl.hud.actionButtons.show(Buttons.DONE.b);
            } else {
                doRotation(o);
                ctrl.setState(StateType.ANIMATION);
            }
        }
    }

    private void doRotation(Orientation o)
    {
        if (activeUnit == null) return;

        map.possiblePaths.init(activeUnit);
        if (map.possiblePaths.build(to) == 1) {
            map.possiblePaths.orientation = o;
            map.moveUnit(activeUnit, o);
            ctrl.setAnimationCount(1);
            ctrl.setAfterAnimationState(StateType.DONE);
        } else
            TankOnTank.debug("That's very wrong there should be only one path");
    }
}
