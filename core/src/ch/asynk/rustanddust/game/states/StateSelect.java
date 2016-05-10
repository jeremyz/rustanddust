package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Ctrl.MsgType;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateSelect extends StateCommon
{
    private boolean isEnemy;

    @Override
    public void enterFrom(StateType prevState)
    {
        clear();
    }

    @Override
    public boolean processMsg(MsgType msg, Object data)
    {
        switch(msg)
        {
            case OK:
                clear();
                ctrl.postTurnDone();
                return true;
            case PROMOTE:
                changeTo(StateType.PROMOTE);
                return true;
            case CANCEL:
                clear();
                return true;
        }

        return false;
    }

    @Override
    public void touch(Hex hex)
    {
        Unit unit = hex.getUnit();

        if (!isEnemy && (selectedUnit != null)) {
            if (map.movesContains(hex)) {
                // quick move
                to = hex;
                changeTo(StateType.MOVE);
                return;
            }
            if (map.unitsTargetContains(unit)) {
                // quick fire
                to = hex;
                activeUnit = unit;
                changeTo(StateType.ENGAGE);
                return;
            }
        }

        hide();

        if ((unit == null) || hex.isOffMap()) {
            selectedUnit = null;
            return;
        }

        isEnemy = ctrl.battle.getPlayer().isEnemy(unit);

        if (!isEnemy && (selectedUnit == unit) && unit.canMove()) {
            if (unit.isHq() && (map.unitsActivableSize() > 1)) {
                ctrl.hud.notify("HQ activation", Position.MIDDLE_CENTER);
                to = null;
            } else
                to = hex;
            changeTo(StateType.MOVE);
        } else {
            select(hex, unit);
            ctrl.hud.notify(selectedUnit.toString());
        }
    }

    private void select(Hex hex, Unit unit)
    {
        selectedHex = hex;
        selectedUnit = unit;
        RustAndDust.debug(String.format("  %s - %s", selectedUnit, selectedHex));

        map.hexSelect(selectedHex);

        if (isEnemy && !cfg.showEnemyPossibilities)
            return;

        if(map.movesCollect(selectedUnit) > 0) {
            map.collectMoveable(selectedUnit);
            if (cfg.showMoves) map.movesShow();
            if (cfg.showMoveAssists) map.unitsActivableShow();
            unit.hideActiveable();
        }

        if (map.collectTargets(selectedUnit, (isEnemy ? ctrl.battle.getPlayer() : ctrl.battle.getOpponent()).units) > 0) {
            if (cfg.showTargets) map.unitsTargetShow();
            unit.hideActiveable();
        }

        ctrl.hud.actionButtons.show((ctrl.battle.getPlayer().canPromote(selectedUnit)) ? Buttons.PROMOTE.b : 0 );
    }

    private void changeTo(StateType nextState)
    {
        hide();
        ctrl.post(nextState);
    }

    private void hide()
    {
        if (selectedHex != null)
            map.hexUnselect(selectedHex);
        map.movesHide();
        map.unitsTargetHide();
        map.unitsActivableHide();
        ctrl.hud.actionButtons.hide();
    }

    private void clear()
    {
        hide();
        map.clearMoves();
        map.clearUnits();
        to = null;
        isEnemy = false;
        selectedHex = null;
        selectedUnit = null;
        activeUnit = null;
    }
}
