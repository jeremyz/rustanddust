package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.GameState.State;

public class GameStateRotate extends GameStateCommon
{
    private boolean rotateOnly;
    private boolean rotationSet;
    private Orientation o = Orientation.KEEP;

    @Override
    public void enter(boolean rotateOnly)
    {
        this.rotateOnly = rotateOnly;

        // if ((map.activablePawnsCount() + map.activatedPawnsCount()) == 1)
        ctrl.hud.show(true, false, false, false, ctrl.cfg.canCancel);
        ctrl.hud.rotateBtn.setOn();

        if (rotateOnly) {
            if (from.x == -1) {
                // rotateBtn  from Select state
                from.set(selectedHex);
                activePawn = selectedPawn;
            }
            to.set(from);
        } else {
            // show final path
            selectHex(to);
            map.showFinalPath(to, true);
        }

        selectHex(from);
        map.showDirections(to, true);

        rotationSet = false;
    }

    @Override
    public void leave(State nextState)
    {
        unselectHex(to);
        unselectHex(from);
        map.showFinalPath(to, false);
        map.showDirections(to, false);
        to.set(-1, -1);
        from.set(-1, -1);
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
        o = Orientation.fromAdj(to.x, to.y, downHex.x, downHex.y);

        if (o == Orientation.KEEP) return;
        rotationSet = true;

        if (ctrl.cfg.mustValidate) {
            // TODO show overlay
            ctrl.hud.show(true, false, false, true, ctrl.cfg.canCancel);
        } else
            doRotation(o);
    }

    @Override
    public void abort()
    {
        hideAssists();
        ctrl.setAnimationCount(map.activatedPawnsCount());
        map.revertMoves();
        super.abort();
    }

    @Override
    public void done()
    {
        doRotation(o);
        if (selectedPawn.canMove() && (map.activatedPawnsCount() > 0))
            selectedPawn.move(0);
        super.done();
    }

    private void hideAssists()
    {
        showAssist(selectedHex, false);
        map.showMoveAssists(false);
    }

    private void doRotation(Orientation o)
    {
        if (!rotationSet) return;

        if (rotateOnly) {
            ctrl.setAnimationCount(1);
            if (map.rotatePawn(activePawn, from, o) > 0)
                setNextState(State.MOVE);
            ctrl.setState(State.ANIMATION);
        } else {
            ctrl.setAnimationCount(1);
            if (map.movePawn(activePawn, from, o) > 0)
                setNextState(State.MOVE);
            ctrl.setState(State.ANIMATION);
        }
    }
}
