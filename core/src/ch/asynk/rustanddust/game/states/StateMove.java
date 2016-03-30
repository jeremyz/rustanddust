package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateMove extends StateCommon
{
    @Override
    public void enterFrom(StateType prevState)
    {
        ctrl.hud.actionButtons.show(
                ((map.unitsActivatedSize() > 0) ? Buttons.DONE.b : 0)
                );

        if (prevState == StateType.WITHDRAW) {
            completePath(map.pathsSize());
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
                checkExit(activeUnit);
        } else {
            // back from rotation -> chose next Pawn
            if (selectedUnit.canMove()) {
                changeUnit(selectedUnit);
            } else {
                changeUnit(map.unitsMoveableGet(0));
            }
        }

        map.unitsActivableShow();
        activeUnit.hideActiveable();
    }

    @Override
    public void leaveFor(StateType nextState)
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
        hideActivable();
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
        hideActivable();
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
            ctrl.post(StateType.ROTATE);
            return;
        }

        int s = map.pathsSize();

        Unit unit = hex.getUnit();

        if (map.unitsActivableContains(unit)) {
            if (unit != activeUnit)
                changeUnit(unit);
        } else if ((s == 0) && map.movesContains(hex)) {
            collectPaths(hex);
        } else if (map.pathsContains(hex)) {
            togglePoint(hex, s);
        }
    }

    private void hideActivable()
    {
        map.unitsActivableHide();
    }

    private void changeUnit(Unit unit)
    {
        if (activeUnit != null ) {
            map.hexUnselect(activeUnit.getHex());
            if (activeUnit.canMove())
                activeUnit.showActiveable();
        }
        to = null;
        activeUnit = unit;
        activeUnit.hideActiveable();
        Hex hex = activeUnit.getHex();
        map.pathsInit(activeUnit, hex);
        map.movesHide();
        map.movesCollect(activeUnit);
        map.movesShow();
        map.hexSelect(hex);
        ctrl.hud.notify(activeUnit.toString(), Position.TOP_CENTER);
        checkExit(activeUnit);
    }

    private void collectPaths(Hex hex)
    {
        to = hex;
        map.movesHide();
        map.hexMoveShow(to);
        int s = map.pathsBuild(to);
        if (!checkExit(activeUnit, hex))
            completePath(s);
    }

    private void completePath(int s)
    {
        if (cfg.autoPath && (s > 1))
            s = map.pathsChooseBest();
        map.pathsShow();
        if (s == 1)
            ctrl.post(StateType.ROTATE);
    }

    private void togglePoint(Hex hex, int s)
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

        if (s == 1) {
            if (!checkExit(activeUnit, hex))
                ctrl.post(StateType.ROTATE);
        }
    }

    private boolean checkExit(Unit unit)
    {
        if (unit.justEntered())
            return false;
        if ((unit.exitZone == null) || !unit.exitZone.contains(unit.getHex()))
            return false;
        ctrl.post(StateType.WITHDRAW);
        return true;
    }

    private boolean checkExit(Unit unit, Hex hex)
    {
        if ((unit.exitZone == null) || !unit.exitZone.contains(hex))
            return false;
        if (!map.pathsCanExit(unit.exitZone.orientation))
            return false;
        ctrl.post(StateType.WITHDRAW);
        return true;
    }
}
