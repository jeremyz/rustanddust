package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

import ch.asynk.tankontank.TankOnTank;

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

        if (!map.possiblePaths.isSet()) {
            map.possiblePaths.init(activeUnit);
            map.possiblePaths.build(to);
        }

        if (map.possiblePaths.size() != 1)
            TankOnTank.debug("ERROR: possiblePaths.size() == " + map.possiblePaths.size());

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
        map.hideOrientation(to);
        map.possiblePaths.clear();
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

        ctrl.setAnimationCount(1);
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

        map.possiblePaths.orientation = o;
        rotationSet = true;

        if (ctrl.cfg.mustValidate) {
            map.hideDirections(to);
            map.showOrientation(to, o);
            ctrl.hud.actionButtons.show(Buttons.DONE.b | ((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
        } else {
            execute();
            ctrl.setState(StateType.ANIMATION);
        }
    }
}
