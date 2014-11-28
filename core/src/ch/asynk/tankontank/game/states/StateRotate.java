package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

import ch.asynk.tankontank.TankOnTank;

public class StateRotate extends StateCommon
{
    private boolean rotateOnly;
    private boolean rotationSet;
    private Orientation o = Orientation.KEEP;

    @Override
    public void enter(StateType prevState)
    {
        ctrl.hud.actionButtons.show(ctrl.cfg.canCancel ? Buttons.ABORT.b : 0);

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
        if (activeUnit.movement.entryMove) {
            map.leaveBoard(activeUnit);
            ctrl.player.revertUnitEntry(activeUnit);
            nextState = StateType.ABORT;
        } else if (map.activatedUnits.size() == 0) {
            hideAssists();
        } else {
            nextState = StateType.MOVE;
        }
        return nextState;
    }

    @Override
    public StateType done()
    {
        doRotation(o);
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

        o = Orientation.fromAdj(to, upHex);

        if (o == Orientation.KEEP) return;
        if (!activeUnit.movement.entryMove && rotateOnly && (o == activeUnit.getOrientation())) return;
        rotationSet = true;


        if (ctrl.cfg.mustValidate) {
            map.hideDirections(to);
            map.showOrientation(to, o);
            ctrl.hud.actionButtons.show(Buttons.DONE.b | ((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
        } else {
            doRotation(o);
            ctrl.setState(StateType.ANIMATION);
        }
    }

    private void hideAssists()
    {
        map.hideMoveableUnits();
    }

    private void doRotation(Orientation o)
    {
        StateType whenDone = StateType.DONE;

        // ctrl.hud.notify("Move " + activeUnit);
        if (map.moveUnit(activeUnit, o) > 0)
            whenDone = StateType.MOVE;

        ctrl.setAnimationCount(1);
        ctrl.setAfterAnimationState(whenDone);
    }
}
