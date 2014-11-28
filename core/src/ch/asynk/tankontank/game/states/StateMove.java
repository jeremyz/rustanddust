package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

public class StateMove extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        ctrl.hud.actionButtons.show(
                ((map.activatedUnits.size() > 0) ? Buttons.DONE.b : 0)
                | (ctrl.cfg.canCancel ? Buttons.ABORT.b : 0));

        if (prevState == StateType.ESCAPE) {
            if (map.possiblePaths.size() == 1)
                ctrl.setState(StateType.ROTATE);
            return;
        }

        map.possiblePaths.clear();

        if (prevState == StateType.SELECT) {
            // use selectedHex and selectedUnit
            activeUnit = selectedUnit;
            activeUnit.showMoveable();
            map.possiblePaths.init(activeUnit);
            map.collectAndShowMovesAndAssits(activeUnit);
            if (to != null) {
                // quick move -> replay touchUp
                upHex = to;
                touchUp();
            } else
                checkExit(activeUnit, activeUnit.getHex());
        } else {
            // back from rotation -> chose next Pawn
            if (selectedUnit.canMove()) {
                changeUnit(selectedUnit);
            } else {
                changeUnit(map.moveableUnits.get(0));
            }
        }
        activeUnit.enableOverlay(Unit.MOVE, false);
    }

    @Override
    public void leave(StateType nextState)
    {
        if (nextState == StateType.ESCAPE)
            return;

        // hide all but assists : want them when in rotation
        activeUnit.hideMoveable();
        map.hidePossibleMoves();
        map.unselectHex(activeUnit.getHex());
        if (to != null) {
            map.hidePath(to);
        }

        if (nextState != StateType.SELECT) {
            if (to == null)
                to = activeUnit.getHex();
        }
    }

    @Override
    public StateType abort()
    {
        hideAssists();
        if (activeUnit.movement.entryMove) {
            map.leaveBoard(activeUnit);
            ctrl.player.revertUnitEntry(activeUnit);
            return StateType.ABORT;
        }
        int n = map.activatedUnits.size();
        if (n == 0)
            return StateType.ABORT;
        ctrl.setAnimationCount(n);
        map.revertMoves();
        return StateType.ANIMATION;
    }

    @Override
    public StateType done()
    {
        hideAssists();
        // be sure that the hq is activated
        if (selectedUnit.canMove() && (map.activatedUnits.size() > 0))
            selectedUnit.setMoved();

        return StateType.DONE;
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        if (upHex == activeUnit.getHex()) {
            ctrl.setState(StateType.ROTATE);
            return;
        }

        int s = map.possiblePaths.size();

        Unit unit = upHex.getUnit();

        if (map.moveableUnits.contains(unit)) {
            if(unit != activeUnit)
                changeUnit(unit);
        } else if ((s == 0) && map.possibleMoves.contains(upHex)) {
            s = collectPaths(upHex);
        } else if (map.possiblePaths.contains(upHex)) {
            s = togglePoint(downHex, s);
        }

        if (s == 1) {
            if (!checkExit(activeUnit, to))
                ctrl.setState(StateType.ROTATE);
        }
    }

    private void hideAssists()
    {
        map.hideMoveableUnits();
    }

    private void changeUnit(Unit unit)
    {
        if (activeUnit != null ) {
            map.unselectHex(activeUnit.getHex());
            activeUnit.enableOverlay(Unit.MOVE, true);
        }
        activeUnit = unit;
        Hex hex = activeUnit.getHex();
        map.possiblePaths.init(activeUnit, hex);
        activeUnit.showMoveable();
        map.hidePossibleMoves();
        map.collectPossibleMoves(activeUnit);
        map.showPossibleMoves();
        map.selectHex(hex);
        activeUnit.enableOverlay(Unit.MOVE, false);
        checkExit(activeUnit, hex);
        ctrl.hud.notify(activeUnit.toString());
    }

    private int collectPaths(Hex hex)
    {
        to = hex;
        int s = map.possiblePaths.build(to);
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
