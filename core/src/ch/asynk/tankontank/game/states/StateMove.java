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
            // use selectedHex and selectedUnit
            from = selectedHex;
            activeUnit = selectedUnit;
            map.buildAndShowMovesAndAssits(activeUnit);
            if (to != null) {
                // quick move -> replay touchUp
                upHex = to;
                touchUp();
            }
        } else {
            // back from rotation -> use the above and unmodified activeUnit
            if ((activeUnit == selectedUnit) || !selectedUnit.canMove()) {
                upHex = (Hex) map.moveAssists.first();
                activeUnit = upHex.getUnit();
            } else {
                upHex = selectedHex;
            }
            changeUnit(upHex);
        }
    }

    @Override
    public void leave(StateType nextState)
    {
        // hide all but assists : want them when in rotation
        map.possibleMoves.hide();
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

        if (map.moveAssists.contains(upHex) || (selectedUnit.canMove() && (selectedHex == upHex))) {
            if(upHex != from)
                changeUnit(upHex);
        } else if ((s == 0) && map.possibleMoves.contains(upHex)) {
            s = buildPaths();
        } else if (map.possiblePaths.contains(upHex)) {
            s = togglePoint(s);
        }

        if (s == 1) {
            // prevent changeUnit
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
        if (selectedUnit.canMove() && (map.activatedPawns.size() > 0))
            selectedUnit.move(0);
        super.done();
    }

    private void hideAssists()
    {
        map.showAssist(selectedHex, false);
        map.moveAssists.hide();
    }

    private void changeUnit(Hex next)
    {
        if (from != null) {
            // toggle selected to assist
            map.selectHex(from, false);
            map.showAssist(from, true);
        }
        from = next;
        activeUnit = from.getUnit();
        map.selectHex(from, true);
        map.showAssist(from, false);
        map.possibleMoves.hide();
        map.buildPossibleMoves(activeUnit);
        map.possibleMoves.show();
    }

    private int buildPaths()
    {
        to = upHex;
        int s = map.buildPossiblePaths(activeUnit, to);
        map.selectHex(to, true);
        map.possibleMoves.hide();
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
