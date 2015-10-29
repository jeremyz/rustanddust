package ch.asynk.rustanddust.game.states;

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

        map.pathBuilder.init(activeUnit);
        if (map.pathBuilder.build(to) == 1) {
            map.pathBuilder.orientation = o;
            map.moveUnit(activeUnit);
            ctrl.setAfterAnimationState(StateType.DONE);
        } else
            RustAndDust.debug("That's very wrong there should be only one path");
    }
}