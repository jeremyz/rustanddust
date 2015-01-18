package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

import ch.asynk.tankontank.TankOnTank;

public class StateSelect extends StateCommon
{
    public StateSelect(Ctrl ctrl, Map map)
    {
        super(ctrl, map);
    }

    @Override
    public void enter(StateType prevState)
    {
        to = null;
        selectedHex = null;
        selectedUnit = null;
        activeUnit = null;
        map.clearAll();
        ctrl.hud.actionButtons.hide();
    }

    @Override
    public void leave(StateType nextState)
    {
        hidePossibilities();
    }

    @Override
    public StateType abort()
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        hidePossibilities();
        map.clearAll();
        return StateType.ABORT;
    }

    @Override
    public StateType execute()
    {
        return StateType.DONE;
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        if (!isEnemy) {
            if (map.possibleMoves.contains(upHex)) {
                // quick move
                to = upHex;
                ctrl.setState(StateType.MOVE);
                return;
            }
            if (map.possibleTargets.contains(upHex.getUnit())) {
                // quick fire
                to = upHex;
                ctrl.setState(StateType.ENGAGE);
                return;
            }
        }

        if (selectedHex != null)
            map.unselectHex(selectedHex);

        hidePossibilities();
        if (upHex.isOffMap()) {
            selectedUnit = null;
            return;
        }

        Unit unit = upHex.getUnit();

        if (unit == null) {
            isEnemy = false;
            ctrl.hud.actionButtons.hide();
            map.clearAll();
            selectedUnit = null;
            return;
        }

        isEnemy = ctrl.player.isEnemy(unit);
        if (!isEnemy && (unit == selectedUnit) && unit.canMove()) {
            if (unit.isHq()) {
                ctrl.hud.notify("HQ activation");
                select(upHex, unit, isEnemy);
                ctrl.setState(StateType.MOVE);
            } else {
                // quick rotate
                to = upHex;
                ctrl.setState(StateType.ROTATE);
            }
        } else {
            select(upHex, unit, isEnemy);
            ctrl.hud.notify(selectedUnit.toString());
        }
    }

    private void select(Hex hex, Unit unit, boolean isEnemy)
    {
        selectedHex = hex;
        selectedUnit = unit;

        if (isEnemy && !ctrl.cfg.showEnemyPossibilities)
            return;

        int moves = map.collectPossibleMoves(selectedUnit);
        int targets = map.collectPossibleTargets(selectedUnit, (isEnemy ? ctrl.player.units : ctrl.opponent.units));

        if (moves > 0)
            map.collectMoveableUnits(selectedUnit);

        if ((moves > 0) || (targets > 0)) {
            map.selectHex(selectedHex);
            showPossibilities(selectedUnit);
        }

        ctrl.hud.actionButtons.show((ctrl.player.canPromote(selectedUnit)) ? Buttons.PROMOTE.b : 0 );
        TankOnTank.debug("Select", selectedHex.toString() + selectedUnit + (isEnemy ? " enemy " : " friend "));
    }
}
