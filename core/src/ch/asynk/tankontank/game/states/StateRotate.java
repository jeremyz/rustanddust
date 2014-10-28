package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.State.StateType;

public class StateRotate extends StateCommon
{
    private boolean rotateOnly;
    private boolean rotationSet;
    private Orientation o = Orientation.KEEP;

    @Override
    public void enter(boolean rotateOnly)
    {
        this.rotateOnly = rotateOnly;

        ctrl.hud.show(false, true, false, false, false, ctrl.cfg.canCancel);
        ctrl.hud.rotateBtn.setOn();

        if (rotateOnly) {
            if (from == null) {
                // rotateBtn from Select state
                from = selectedHex;
                activeUnit = selectedUnit;
            }
            to = from;
        } else {
            // show final path
            map.selectHex(to, true);
            map.showFinalPath(to, true);
        }

        map.selectHex(from, true);
        map.showDirections(to, true);

        rotationSet = false;
    }

    @Override
    public void leave(StateType nextState)
    {
        map.selectHex(to, false);
        map.selectHex(from, false);
        map.showFinalPath(to, false);
        map.showDirections(to, false);
        map.showOrientation(to, false, o);
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        if (rotationSet) return;

        // FIXME: if to is on the border of the board ...
        o = Orientation.fromAdj(to.getCol(), to.getRow(), downHex.getCol(), downHex.getRow());

        if (o == Orientation.KEEP) return;
        rotationSet = true;

        if (ctrl.cfg.mustValidate) {
            map.showDirections(to, false);
            map.showOrientation(to, true, o);
            ctrl.hud.show(false, true, false, false, true, ctrl.cfg.canCancel);
        } else
            doRotation(o);
    }

    @Override
    public void abort()
    {
        ctrl.hud.hide();
        if (map.activatedPawns.size() == 0) {
            hideAssists();
            super.abort();
        } else {
            ctrl.setState(StateType.MOVE, false);
        }
    }

    @Override
    public void done()
    {
        doRotation(o);
        if (selectedUnit.canMove() && (map.activatedPawns.size() > 0))
            selectedUnit.move(0);
        super.done();
    }

    private void hideAssists()
    {
        map.showAssist(selectedHex, false);
        map.moveAssists.hide();
    }

    private void doRotation(Orientation o)
    {
        if (!rotationSet) return;

        if (rotateOnly) {
            ctrl.setAnimationCount(1);
            if (map.rotatePawn(activeUnit, o) > 0)
                setNextState(StateType.MOVE);
            ctrl.setState(StateType.ANIMATION);
        } else {
            ctrl.setAnimationCount(1);
            if (map.movePawn(activeUnit, o) > 0)
                setNextState(StateType.MOVE);
            ctrl.setState(StateType.ANIMATION);
        }
    }
}
