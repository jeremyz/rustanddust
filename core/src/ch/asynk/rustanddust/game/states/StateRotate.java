package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.engine.Orientation;

import ch.asynk.rustanddust.RustAndDust;

public class StateRotate extends StateCommon
{
    private boolean rotateOnly;
    private boolean rotationSet;

    @Override
    public void enter(StateType prevState)
    {
        if (activeUnit == null)
            activeUnit = selectedUnit;
        if (to == null)
            to = activeUnit.getHex();

        if (!map.pathsIsSet()) {
            map.pathsInit(activeUnit);
            map.pathsBuild(to);
        }

        if (map.pathsSize() != 1)
            RustAndDust.debug("ERROR: pathsSize() == " + map.pathsSize());

        rotateOnly = (to == activeUnit.getHex());

        if (!rotateOnly)
            map.pathShow(to);
        map.hexSelect(activeUnit.getHex());
        map.hexDirectionsShow(to);

        rotationSet = false;
    }

    @Override
    public void leave(StateType nextState)
    {
        map.hexUnselect(activeUnit.getHex());
        map.pathHide(to);
        map.hexDirectionsHide(to);
        map.pathsClear();
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
        } else if (map.unitsActivatedSize() == 0) {
            map.unitsMoveableHide();
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

        map.pathsSetOrientation(o);
        rotationSet = true;
        execute();
        ctrl.setState(StateType.ANIMATION);
    }
}
