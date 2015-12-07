package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Map.UnitType;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateMove extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        ctrl.hud.actionButtons.show(
                ((map.unitsSize(UnitType.ACTIVATED) > 0) ? Buttons.DONE.b : 0)
                | (ctrl.cfg.canCancel ? Buttons.ABORT.b : 0));

        if (prevState == StateType.WITHDRAW) {
            if (map.paths.size() == 1)
                ctrl.setState(StateType.ROTATE);
            return;
        }

        map.paths.clear();

        if (prevState == StateType.SELECT) {
            // use selectedHex and selectedUnit
            activeUnit = selectedUnit;
            activeUnit.showMoveable();
            map.paths.init(activeUnit);
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
                changeUnit(map.unitsGet(UnitType.MOVEABLE, 0));
            }
        }

        activeUnit.enableOverlay(Unit.MOVE, false);
    }

    @Override
    public void leave(StateType nextState)
    {
        if (nextState == StateType.WITHDRAW)
            return;

        // hide all but assists : want them when in rotation
        activeUnit.hideMoveable();
        map.movesHide();
        map.hexUnselect(activeUnit.getHex());
        if (to != null)
            map.pathHide(to);

        if (nextState != StateType.SELECT) {
            if (to == null)
                to = activeUnit.getHex();
        }
    }

    @Override
    public StateType abort()
    {
        hideAssists();
        if (activeUnit.justEntered()) {
            map.revertEnter(activeUnit);
            return StateType.ABORT;
        }
        int n = map.unitsSize(UnitType.ACTIVATED);
        if (n == 0)
            return StateType.ABORT;
        map.revertMoves();
        return StateType.ANIMATION;
    }

    @Override
    public StateType execute()
    {
        hideAssists();
        // be sure that the hq is activated
        if (selectedUnit.canMove() && (map.unitsSize(UnitType.ACTIVATED) > 0))
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
            if (to != null)
                map.pathHide(to);
            to = null;
            map.paths.clear();
            ctrl.setState(StateType.ROTATE);
            return;
        }

        int s = map.paths.size();

        Unit unit = upHex.getUnit();

        if (map.unitsContains(UnitType.MOVEABLE, unit)) {
            if(unit != activeUnit)
                changeUnit(unit);
        } else if ((s == 0) && map.movesContains(upHex)) {
            s = collectPaths(upHex);
        } else if (map.paths.contains(upHex)) {
            s = togglePoint(downHex, s);
        }

        if (s == 1) {
            if (!checkExit(activeUnit, upHex))
                ctrl.setState(StateType.ROTATE);
        }
    }

    private void hideAssists()
    {
        map.unitsHide(UnitType.MOVEABLE);
    }

    private void changeUnit(Unit unit)
    {
        if (activeUnit != null ) {
            map.hexUnselect(activeUnit.getHex());
            if (activeUnit.canMove())
                activeUnit.enableOverlay(Unit.MOVE, true);
        }
        activeUnit = unit;
        Hex hex = activeUnit.getHex();
        map.paths.init(activeUnit, hex);
        activeUnit.showMoveable();
        map.movesHide();
        map.movesCollect(activeUnit);
        map.movesShow();
        map.hexSelect(hex);
        activeUnit.enableOverlay(Unit.MOVE, false);
        ctrl.hud.notify(activeUnit.toString());
        checkExit(activeUnit, hex);
    }

    private int collectPaths(Hex hex)
    {
        to = hex;
        int s = map.paths.build(to);
        if (s > 1)
            s = map.paths.choosePath();
        map.hexMoveShow(to);
        map.movesHide();
        map.pathsShow();
        return s;
    }

    private int togglePoint(Hex hex, int s)
    {
        if (hex == activeUnit.getHex()) {
            //
        } else if (hex == to) {
            //
        } else {
            map.pathsHide();
            s = map.pathsToggleHex(hex);
            map.pathsShow();
        }

        return s;
    }

    private boolean checkExit(Unit unit, Hex hex)
    {
        if ((hex == unit.getHex()) && (unit.justEntered()))
            return false;
        Zone exitZone = ctrl.battle.getExitZone(unit);
        if ((exitZone == null) || !exitZone.contains(hex))
            return false;
        if ((unit.getHex() != hex) && !map.paths.canExit(exitZone.orientation))
            return false;
        ctrl.setState(StateType.WITHDRAW);
        return true;
    }
}
