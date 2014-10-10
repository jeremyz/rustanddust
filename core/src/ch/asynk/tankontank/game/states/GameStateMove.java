package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.GridPoint2;

public class GameStateMove extends GameStateCommon
{
    @Override
    public void enter(boolean fromSelect)
    {
        map.clearPossiblePaths();

        if (fromSelect) {
            from.set(hex);
            activePawn = pawn;
            map.buildAndShowMovesAndAssits(activePawn, from);
            if (to.x != -1) {
                upHex.set(to);
                touchUp();
            }
        } else {
            if ((activePawn == pawn) || !pawn.canMove()) {
                upHex.set(map.getFirstMoveAssist());
                activePawn = map.getTopPawnAt(upHex);
            } else {
                upHex.set(hex);
            }
            from.set(-1, -1);
            changePawn(upHex);
        }

        ctrl.hud.show(true, true, false, ((map.activablePawnsCount() + map.activatedPawnsCount()) > 1), ctrl.cfg.canCancel);
        ctrl.hud.moveBtn.setOn();
    }

    @Override
    public void leave()
    {
        // hide all but assists
        map.showPossibleMoves(false);
        unselectHex(from);
        if (to.x != -1) {
            unselectHex(to);
            map.showFinalPath(to, false);
        }
        ctrl.hud.hide();
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        int s = map.possiblePathsSize();

        if (sameHexes(hex, upHex) || map.isInPossibleMoveAssists(upHex)) {
            if(!sameHexes(upHex, from))
                changePawn(upHex);
        } else if ((s == 0) && map.isInPossibleMoves(upHex)) {
            s = buildPaths();
        } else if (map.isInPossiblePaths(upHex)) {
            s = togglePoint(s);
        }

        if (s == 1)
            ctrl.setState(State.ROTATE);
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
        if (pawn.canMove() && (map.activatedPawnsCount() > 0))
            pawn.move(0);
        hideAssists();
        super.done();
    }

    private void hideAssists()
    {
        showAssist(hex, false);
        map.showMoveAssists(false);
    }

    private void changePawn(GridPoint2 next)
    {
        // do not show the last moved assist
        if (from.x != -1) {
            unselectHex(from);
            showAssist(from, true);
        }
        from.set(next);
        selectHex(from);
        showAssist(from, false);
        activePawn = map.getTopPawnAt(from);
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
