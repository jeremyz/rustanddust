package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;

public class StateMove extends StateCommon
{
    @Override
    public void enter(boolean fromSelect)
    {
        boolean moreThanOne = ((map.moveablePawns.size() + map.activatedPawns.size()) > 1);
        ctrl.hud.show(false, true, true, false, moreThanOne, ctrl.cfg.canCancel);
        ctrl.hud.moveBtn.setOn();
        map.possiblePaths.clear();

        if (fromSelect) {
            // use selectedHex and selectedUnit
            activeUnit = selectedUnit;
            activeUnit.showMoveable();
            map.collectAndShowMovesAndAssits(activeUnit);
            if (to != null) {
                // quick move -> replay touchUp
                upHex = to;
                touchUp();
            }
        } else {
            // back from rotation -> chose next Pawn
            if (selectedUnit.canMove()) {
                changeUnit(selectedUnit);
            } else {
                changeUnit((Unit) map.moveablePawns.first());
            }
        }
    }

    @Override
    public void leave(StateType nextState)
    {
        // hide all but assists : want them when in rotation
        activeUnit.hideMoveable();
        map.hidePossibleMoves();
        map.unselectHex(activeUnit.getHex());
        if (to != null) {
            map.hideFinalPath(to);
        }

        if (nextState != StateType.SELECT) {
            if (to == null)
                to = activeUnit.getHex();
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

        Unit unit = upHex.getUnit();

        if (map.moveablePawns.contains(unit)) {
            if(unit != activeUnit)
                changeUnit(unit);
        } else if ((s == 0) && map.possibleMoves.contains(upHex)) {
            s = collectPaths();
        } else if (map.possiblePaths.contains(upHex)) {
            s = togglePoint(downHex, s);
        }

        if (s == 1) {
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
        // be sure that the hq is activated
        if (selectedUnit.canMove() && (map.activatedPawns.size() > 0))
            selectedUnit.move(0);
        super.done();
    }

    private void hideAssists()
    {
        map.hideMoveablePawns();
    }

    private void changeUnit(Unit unit)
    {
        if (activeUnit != null )
            map.unselectHex(activeUnit.getHex());
        activeUnit = unit;
        Hex hex = activeUnit.getHex();
        map.selectHex(hex);
        activeUnit.showMoveable();
        map.hidePossibleMoves();
        map.collectPossibleMoves(activeUnit);
        map.showPossibleMoves();
    }

    private int collectPaths()
    {
        to = upHex;
        int s = map.collectPossiblePaths(activeUnit, to);
        map.showMove(to);
        map.hidePossibleMoves();
        map.showPossiblePaths();
        return s;
    }

    private int togglePoint(Hex hex, int s)
    {
        if (hex == activeUnit.getHex()) {
            //
        } else if (hex == to) {
            //
        } else {
            map.hidePossiblePaths();
            map.togglePathOverlay(hex);
            s = map.togglePossiblePathHex(hex);
            map.showPossiblePaths();
        }

        return s;
    }
}
