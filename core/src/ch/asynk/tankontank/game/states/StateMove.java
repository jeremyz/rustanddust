package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.game.State.StateType;

public class StateMove extends StateCommon
{
    @Override
    public void enter(boolean fromSelect)
    {
        map.clearPossiblePaths();
        ctrl.hud.show(true, true, false, ((map.activablePawnsCount() + map.activatedPawnsCount()) > 1), ctrl.cfg.canCancel);
        ctrl.hud.moveBtn.setOn();

        if (fromSelect) {
            // use selectedHex and selectedPawn
            from.set(selectedHex);
            activePawn = selectedPawn;
            map.buildAndShowMovesAndAssits(activePawn, from);
            if (to.x != -1) {
                // quick move -> replay touchUp
                upHex.set(to);
                touchUp();
            }
        } else {
            // back from rotation -> use the above and unmodified activePawn
            if ((activePawn == selectedPawn) || !selectedPawn.canMove()) {
                upHex.set(map.getFirstMoveAssist());
                activePawn = map.getTopPawnAt(upHex);
            } else {
                upHex.set(selectedHex);
            }
            changePawn(upHex);
        }
    }

    @Override
    public void leave(StateType nextState)
    {
        // hide all but assists : want them when in rotation
        map.showPossibleMoves(false);
        unselectHex(from);
        if (to.x != -1) {
            unselectHex(to);
            map.showFinalPath(to, false);
        }

        if (nextState != StateType.SELECT) {
            if (to.x == -1 )
                to.set(from);
        }
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        int s = map.possiblePathsSize();

        if (map.isInPossibleMoveAssists(upHex) || (selectedPawn.canMove() && sameHexes(selectedHex, upHex))) {
            if(!sameHexes(upHex, from))
                changePawn(upHex);
        } else if ((s == 0) && map.isInPossibleMoves(upHex)) {
            s = buildPaths();
        } else if (map.isInPossiblePaths(upHex)) {
            s = togglePoint(s);
        }

        if (s == 1) {
            // prevent changePawn
            if (sameHexes(from, selectedHex))
                selectedHex.set(to);
            ctrl.setState(StateType.ROTATE, false);
        }
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
        hideAssists();
        if (selectedPawn.canMove() && (map.activatedPawnsCount() > 0))
            selectedPawn.move(0);
        super.done();
    }

    private void hideAssists()
    {
        showAssist(selectedHex, false);
        map.showMoveAssists(false);
    }

    private void changePawn(GridPoint2 next)
    {
        if (from.x != -1) {
            // toggle selected to assist
            unselectHex(from);
            showAssist(from, true);
        }
        from.set(next);
        activePawn = map.getTopPawnAt(from);
        selectHex(from);
        showAssist(from, false);
        map.showPossibleMoves(false);
        map.buildPossibleMoves(activePawn, from);
        map.showPossibleMoves(true);
    }

    private int buildPaths()
    {
        to.set(upHex.x, upHex.y);
        int s = map.buildPossiblePaths(activePawn, from, to);
        selectHex(to);
        map.showPossibleMoves(false);
        map.showPossiblePaths(true, true);
        return s;
    }

    private int togglePoint(int s)
    {
        if (sameHexes(downHex, from)) {
            //
        } else if (sameHexes(downHex, to)) {
            //
        } else {
            map.showPossiblePaths(false, true);
            map.togglePathOverlay(downHex);
            s = map.possiblePathsPointToggle(downHex);
            map.showPossiblePaths(true, true);
        }

        return s;
    }
}
