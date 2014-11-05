package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;

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
            // rotateBtn from Select state
            if (activeUnit == null)
                activeUnit = selectedUnit;
            to = activeUnit.getHex();
        } else {
            if (to == null)
                System.err.println("to is null but should not be");
            map.showFinalPath(to);
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
        // FIXME this fucks up entry of 1MP pawns
        // if (rotateOnly && (o == activeUnit.getOrientation())) return;
        rotationSet = true;


        if (ctrl.cfg.mustValidate) {
            map.hideDirections(to);
            map.showOrientation(to, o);
            ctrl.hud.show(false, true, false, false, true, ctrl.cfg.canCancel);
        } else
            doRotation(o);
    }

    @Override
    public void abort()
    {
        ctrl.hud.hide();
        if (activeUnit.move.entryMove) {
            map.leaveBoard(activeUnit);
            ctrl.player.revertUnitEntry(activeUnit);
            super.abort();
        }
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
            selectedUnit.move();
        super.done();
    }

    private void hideAssists()
    {
        map.hideMoveablePawns();
    }

    private void doRotation(Orientation o)
    {
        if (!rotationSet) return;

        ctrl.hud.notify("Move " + activeUnit);
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
