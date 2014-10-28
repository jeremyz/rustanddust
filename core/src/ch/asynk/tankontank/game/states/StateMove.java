package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.State.StateType;

public class StateMove extends StateCommon
{
    @Override
    public void enter(boolean fromSelect)
    {
        map.possiblePaths.clear();
        ctrl.hud.show(false, true, true, false, ((map.activablePawns.size() + map.activatedPawns.size()) > 1), ctrl.cfg.canCancel);
        ctrl.hud.moveBtn.setOn();

        if (fromSelect) {
            // use selectedHex and selectedPawn
            from = selectedHex;
            activePawn = selectedPawn;
            map.buildAndShowMovesAndAssits(activePawn);
            if (to != null) {
                // quick move -> replay touchUp
                upHex = to;
                touchUp();
            }
        } else {
            // back from rotation -> use the above and unmodified activePawn
            if ((activePawn == selectedPawn) || !selectedPawn.canMove()) {
                upHex = map.getFirstMoveAssist();
                activePawn = upHex.getTopPawn();
            } else {
                upHex = selectedHex;
            }
            changePawn(upHex);
        }
    }

    @Override
    public void leave(StateType nextState)
    {
        // hide all but assists : want them when in rotation
        map.showPossibleMoves(false);
        map.selectHex(from, false);
        if (to != null) {
            map.selectHex(to, false);
            map.showFinalPath(to, false);
        }

        if (nextState != StateType.SELECT) {
            if (to == null)
                to = from;
        }
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        int s = map.possiblePaths.size();

        if (map.isInPossibleMoveAssists(upHex) || (selectedPawn.canMove() && (selectedHex == upHex))) {
            if(upHex != from)
                changePawn(upHex);
        } else if ((s == 0) && map.isInPossibleMoves(upHex)) {
            s = buildPaths();
        } else if (map.isInPossiblePaths(upHex)) {
            s = togglePoint(s);
        }

        if (s == 1) {
            // prevent changePawn
            if (from == selectedHex)
                selectedHex = to;
            ctrl.setState(StateType.ROTATE, false);
        }
    }

    @Override
    public void abort()
    {
        hideAssists();
        ctrl.setAnimationCount(map.activatedPawns.size());
        map.revertMoves();
        super.abort();
    }

    @Override
    public void done()
    {
        hideAssists();
        if (selectedPawn.canMove() && (map.activatedPawns.size() > 0))
            selectedPawn.move(0);
        super.done();
    }

    private void hideAssists()
    {
        map.showAssist(selectedHex, false);
        map.showMoveAssists(false);
    }

    private void changePawn(Hex next)
    {
        if (from != null) {
            // toggle selected to assist
            map.selectHex(from, false);
            map.showAssist(from, true);
        }
        from = next;
        activePawn = from.getTopPawn();
        map.selectHex(from, true);
        map.showAssist(from, false);
        map.showPossibleMoves(false);
        map.buildPossibleMoves(activePawn);
        map.showPossibleMoves(true);
    }

    private int buildPaths()
    {
        to = upHex;
        int s = map.buildPossiblePaths(activePawn, to);
        map.selectHex(to, true);
        map.showPossibleMoves(false);
        map.showPossiblePaths(true, true);
        return s;
    }

    private int togglePoint(int s)
    {
        if (downHex == from) {
            //
        } else if (downHex == to) {
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
