package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.State.StateType;

public class StateMove extends StateCommon
{
    @Override
    public void enter(boolean fromSelect)
    {
        map.possiblePaths.clear();
        boolean moreThanOne = ((map.moveablePawns.size() + map.activatedPawns.size()) > 1);
        ctrl.hud.show(false, true, true, false, moreThanOne, ctrl.cfg.canCancel);
        ctrl.hud.moveBtn.setOn();

        if (fromSelect) {
            // use selectedHex and selectedUnit
            activeUnit = selectedUnit;
            activeUnit.showMoveable();
            map.buildAndShowMovesAndAssits(activeUnit);
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
            map.unselectHex(to);
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
            s = buildPaths();
        } else if (map.possiblePaths.contains(upHex)) {
            s = togglePoint(s);
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
        map.hideAssist(selectedHex);
        map.hideMoveablePawns();
    }

    private void changeUnit(Unit unit)
    {
        if (activeUnit != null )
            map.unselectHex(activeUnit.getHex());
        activeUnit = unit;
        Hex hex = activeUnit.getHex();
        map.selectHex(hex);
        map.hideAssist(hex);
        activeUnit.showMoveable();
        map.hidePossibleMoves();
        map.buildPossibleMoves(activeUnit);
        map.showPossibleMoves();
    }

    private int buildPaths()
    {
        to = upHex;
        int s = map.buildPossiblePaths(activeUnit, to);
        map.selectHex(to);
        map.hidePossibleMoves();
        map.showPossiblePaths();
        return s;
    }

    private int togglePoint(int s)
    {
        if (downHex == activeUnit.getHex()) {
            //
        } else if (downHex == to) {
            //
        } else {
            map.hidePossiblePaths();
            map.togglePathOverlay(downHex);
            s = map.possiblePathsPointToggle(downHex);
            map.showPossiblePaths();
        }

        return s;
    }
}
