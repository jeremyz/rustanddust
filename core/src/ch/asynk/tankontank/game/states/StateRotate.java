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
        ctrl.hud.actionButtons.show(Buttons.ROTATE.b | ((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
        ctrl.hud.actionButtons.setOn(Buttons.ROTATE);

        if (prevState == StateType.MOVE) {
            rotateOnly = false;
            if (to == null)
                TankOnTank.debug("to is null but should not be");
            map.showFinalPath(to);
        } else {
            rotateOnly = true;
            to = activeUnit.getHex();
        }

        map.selectHex(activeUnit.getHex());
        map.showDirections(to);

        rotationSet = false;
    }

    @Override
    public void leave(StateType nextState)
    {
        map.unselectHex(activeUnit.getHex());
        map.hideFinalPath(to);
        map.hideDirections(to);
        map.hideOrientation(to);
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        if (rotationSet) return;

        o = Orientation.fromAdj(to.getCol(), to.getRow(), downHex.getCol(), downHex.getRow());

        if (o == Orientation.KEEP) return;
        if (!activeUnit.move.entryMove && rotateOnly && (o == activeUnit.getOrientation())) return;
        rotationSet = true;


        if (ctrl.cfg.mustValidate) {
            map.hideDirections(to);
            map.showOrientation(to, o);
            ctrl.hud.actionButtons.show(Buttons.ROTATE.b | Buttons.DONE.b | ((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
        } else
            doRotation(o);
    }

    @Override
    public StateType abort()
    {
        StateType nextState = StateType.ABORT;
        ctrl.hud.actionButtons.hide();
        if (activeUnit.move.entryMove) {
            map.leaveBoard(activeUnit);
            ctrl.player.revertUnitEntry(activeUnit);
        }
        if (map.activatedPawns.size() == 0) {
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
        if (selectedUnit.canMove() && (map.activatedPawns.size() > 0))
            selectedUnit.move();
        return StateType.DONE;
    }

    private void hideAssists()
    {
        map.hideMoveablePawns();
    }

    private void doRotation(Orientation o)
    {
        if (!rotationSet) return;

        StateType whenDone = StateType.DONE;

        ctrl.hud.notify("Move " + activeUnit);
        if (rotateOnly) {
            ctrl.setAnimationCount(1);
            if (map.rotatePawn(activeUnit, o) > 0)
                whenDone = StateType.MOVE;
        } else {
            ctrl.setAnimationCount(1);
            if (map.movePawn(activeUnit, o) > 0)
                whenDone = StateType.MOVE;
        }

        ctrl.setState(StateType.ANIMATION, whenDone);
    }
}
