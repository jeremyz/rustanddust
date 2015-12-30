package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateMove extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        ctrl.hud.actionButtons.show(
                ((map.unitsActivatedSize() > 0) ? Buttons.DONE.b : 0)
                );

        if (prevState == StateType.WITHDRAW) {
            if (map.pathsSize() == 1)
                ctrl.setState(StateType.ROTATE);
            return;
        }

        map.pathsClear();

        if (prevState == StateType.SELECT) {
            // use selectedHex and selectedUnit
            activeUnit = selectedUnit;
            map.pathsInit(activeUnit);
            map.collectUpdate(activeUnit);
            if (to != null) {
                // quick move -> replay touchUp
                touch(to);
            } else
                checkExit(activeUnit, activeUnit.getHex());
        } else {
            // back from rotation -> chose next Pawn
            if (selectedUnit.canMove()) {
                changeUnit(selectedUnit);
            } else {
                changeUnit(map.unitsMoveableGet(0));
            }
        }

        activeUnit.hideActiveable();
    }

    @Override
    public void leave(StateType nextState)
    {
        if (nextState == StateType.WITHDRAW)
            return;

        // hide all but assists : want them when in rotation
        activeUnit.hideActiveable();
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
        int n = map.unitsActivatedSize();
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
        if (selectedUnit.canMove() && (map.unitsActivatedSize() > 0))
            selectedUnit.setMoved();

        return StateType.DONE;
    }

    @Override
    public void touch(Hex hex)
    {
        if (hex == activeUnit.getHex()) {
            if (to != null)
                map.pathHide(to);
            to = null;
            map.pathsClear();
            ctrl.setState(StateType.ROTATE);
            return;
        }

        int s = map.pathsSize();

        Unit unit = hex.getUnit();

        if (map.unitsMoveableContains(unit)) {
            if(unit != activeUnit)
                changeUnit(unit);
        } else if ((s == 0) && map.movesContains(hex)) {
            s = collectPaths(hex);
        } else if (map.pathsContains(hex)) {
            s = togglePoint(hex, s);
        }

        if (s == 1) {
            if (!checkExit(activeUnit, hex))
                ctrl.setState(StateType.ROTATE);
        }
    }

    private void hideAssists()
    {
        map.unitsMoveableHide();
    }

    private void changeUnit(Unit unit)
    {
        if (activeUnit != null ) {
            map.hexUnselect(activeUnit.getHex());
            if (activeUnit.canMove())
                activeUnit.showActiveable();
        }
        activeUnit = unit;
        activeUnit.hideActiveable();
        Hex hex = activeUnit.getHex();
        map.pathsInit(activeUnit, hex);
        map.movesHide();
        map.movesCollect(activeUnit);
        map.movesShow();
        map.hexSelect(hex);
        ctrl.hud.notify(activeUnit.toString(), Position.TOP_CENTER);
        checkExit(activeUnit, hex);
    }

    private int collectPaths(Hex hex)
    {
        to = hex;
        int s = map.pathsBuild(to);
        if (cfg.autoPath && (s > 1))
            s = map.pathsChooseOne();
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
        if ((unit.getHex() != hex) && !map.pathsCanExit(exitZone.orientation))
            return false;
        ctrl.setState(StateType.WITHDRAW);
        return true;
    }
}
