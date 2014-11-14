package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

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
    }

    @Override
    public void leave(StateType nextState)
    {
        hidePossibleTargetsMovesAssists();
    }

    @Override
    public void touchDown()
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
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
                ctrl.setState(StateType.ATTACK);
                return;
            }
        }

        selectHexAndUnit(upHex);
        hidePossibleTargetsMovesAssists();

        if (hasUnit() && (!isEnemy || ctrl.cfg.showEnemyPossibilities)) {
            // moves and targets == 0 if selectedUnit can't be activated for
            int moves = map.collectPossibleMoves(selectedUnit);
            int targets = 0;
            if (isEnemy)
                targets = map.collectPossibleTargets(selectedUnit, ctrl.player.unitIterator());
            else
                targets = map.collectPossibleTargets(selectedUnit, ctrl.opponent.unitIterator());
            if (moves > 0)
                map.collectMoveablePawns(selectedUnit);
            showPossibleTargetsMovesAssists(selectedUnit);
            ctrl.hud.actionButtons.show(
                ((ctrl.player.canPromote(selectedUnit)) ? Buttons.PROMOTE.b : 0 ) |
                ((selectedUnit.canMove()) ? Buttons.ROTATE.b : 0 ) |
                ((moves > 0) ? Buttons.MOVE.b : 0 ) |
                ((targets > 0) ? Buttons.ATTACK.b : 0)
                );
        } else {
            ctrl.hud.actionButtons.hide();
            map.clearAll();
        }
        if (selectedUnit != null) ctrl.hud.notify(selectedUnit.toString());
    }

    @Override
    public StateType abort()
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        hidePossibleTargetsMovesAssists();
        map.clearAll();
        return StateType.ABORT;
    }

    @Override
    public StateType done()
    {
        return StateType.DONE;
    }
}
