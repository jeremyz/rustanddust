package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

import ch.asynk.rustanddust.RustAndDust;

public class StateRotate extends StateCommon
{
    private boolean rotateOnly;
    private boolean rotationSet;

    @Override
    public void enter(StateType prevState)
    {
        ctrl.hud.actionButtons.show((ctrl.cfg.canCancel && (map.moveableUnits.size() > 1))? Buttons.ABORT.b : 0);

        if (activeUnit == null)
            activeUnit = selectedUnit;
        if (to == null)
            to = activeUnit.getHex();

        if (!map.pathBuilder.isSet()) {
            map.pathBuilder.init(activeUnit);
            map.pathBuilder.build(to);
        }

        if (map.pathBuilder.size() != 1)
            RustAndDust.debug("ERROR: pathBuilder.size() == " + map.pathBuilder.size());

        rotateOnly = (to == activeUnit.getHex());

        if (!rotateOnly)
            map.showPath(to);
        map.selectHex(activeUnit.getHex());
        map.showDirections(to);

        rotationSet = false;
    }

    @Override
    public void leave(StateType nextState)
    {
        map.unselectHex(activeUnit.getHex());
        map.hidePath(to);
        map.hideDirections(to);
        map.pathBuilder.clear();
        to = null;
    }

    @Override
    public StateType abort()
    {
        StateType nextState = StateType.ABORT;
        ctrl.hud.actionButtons.hide();
        if (activeUnit.justEntered()) {
            map.revertEnter(activeUnit);
            nextState = StateType.ABORT;
        } else if (map.activatedUnits.size() == 0) {
            map.hideMoveableUnits();
        } else {
            nextState = StateType.MOVE;
        }
        return nextState;
    }

    @Override
    public StateType execute()
    {
        StateType whenDone = StateType.DONE;

        if (map.moveUnit(activeUnit) > 0)
            whenDone = StateType.MOVE;

        ctrl.setAfterAnimationState(whenDone);
        return StateType.ANIMATION;
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        if (rotationSet) return;

        Orientation o = Orientation.fromAdj(to, upHex);
        if (o == Orientation.KEEP) {
            ctrl.setState(StateType.ABORT);
            return;
        }

        if (!activeUnit.justEntered() && rotateOnly && (o == activeUnit.getOrientation()))
            return;

        map.pathBuilder.orientation = o;
        rotationSet = true;

        if (ctrl.cfg.mustValidate) {
            map.hideDirections(to);
            ctrl.hud.actionButtons.show(Buttons.DONE.b | ((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
        } else {
            execute();
            ctrl.setState(StateType.ANIMATION);
        }
    }
}
