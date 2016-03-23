package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;
import ch.asynk.rustanddust.RustAndDust;

public class StateSelect extends StateCommon
{
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
            map.hexUnselect(selectedHex);
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
    public void touch(Hex hex)
    {
        if (!isEnemy) {
            if (map.movesContains(hex)) {
                // quick move
                to = hex;
                ctrl.setState(StateType.MOVE);
                return;
            }
            if (map.unitsTargetContains(hex.getUnit())) {
                // quick fire
                to = hex;
                ctrl.setState(StateType.ENGAGE);
                return;
            }
        }

        if (selectedHex != null)
            map.hexUnselect(selectedHex);

        hidePossibilities();
        if (hex.isOffMap()) {
            selectedUnit = null;
            return;
        }

        Unit unit = hex.getUnit();

        if (unit == null) {
            isEnemy = false;
            ctrl.hud.actionButtons.hide();
            map.clearAll();
            selectedUnit = null;
            return;
        }

        isEnemy = ctrl.battle.getPlayer().isEnemy(unit);
        if (!isEnemy && (unit == selectedUnit) && unit.canMove()) {
            if (unit.isHq() && (map.unitsActivableSize() > 1)) {
                ctrl.hud.notify("HQ activation");
                select(hex, unit, isEnemy);
                ctrl.setState(StateType.MOVE);
            } else {
                // quick rotate
                to = hex;
                ctrl.setState(StateType.ROTATE);
            }
        } else {
            select(hex, unit, isEnemy);
            ctrl.hud.notify(selectedUnit.toString(), Position.TOP_CENTER);
        }
    }

    private void select(Hex hex, Unit unit, boolean isEnemy)
    {
        selectedHex = hex;
        selectedUnit = unit;

        if (isEnemy && !cfg.showEnemyPossibilities)
            return;

        int moves = map.movesCollect(selectedUnit);
        int targets = map.collectTargets(selectedUnit, (isEnemy ? ctrl.battle.getPlayer() : ctrl.battle.getOpponent()).units);

        if (moves > 0)
            map.collectMoveable(selectedUnit);

        if ((moves > 0) || (targets > 0)) {
            map.hexSelect(selectedHex);
            showPossibilities(selectedUnit);
        }

        ctrl.hud.actionButtons.show((ctrl.battle.getPlayer().canPromote(selectedUnit)) ? Buttons.PROMOTE.b : 0 );
        RustAndDust.debug("Select", selectedHex.toString() + " " + selectedUnit + (isEnemy ? " enemy " : " friend "));
    }
}
