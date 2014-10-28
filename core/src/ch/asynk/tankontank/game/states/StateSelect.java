package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.State.StateType;

public class StateSelect extends StateCommon
{
    public StateSelect(Ctrl ctrl, Map map)
    {
        super(ctrl, map);
    }

    @Override
    public void enter(boolean flag)
    {
        clearAll();
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
        if (selectedHex != null) map.selectHex(selectedHex, false);
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
            int moves = map.buildPossibleMoves(selectedUnit);
            int targets = 0;
            if (isEnemy)
                targets = map.buildPossibleTargets(selectedUnit, ctrl.player.unitIterator());
            else
                targets = map.buildPossibleTargets(selectedUnit, ctrl.opponent.unitIterator());
            if (moves > 0)
                map.collectMoveablePawns(selectedUnit);
            showPossibleTargetsMovesAssists(selectedUnit);
            ctrl.hud.show(
                ctrl.player.canPromote(selectedUnit),
                selectedUnit.canMove(),
                (moves > 0),
                (targets > 0),
                false,
                false
                );
        } else {
            ctrl.hud.hide();
            map.clearAll();
        }
        if (selectedUnit != null) ctrl.hud.notify(selectedUnit.toString());
    }

    @Override
    public void abort()
    {
        if (selectedHex != null) map.selectHex(selectedHex, false);
        hidePossibleTargetsMovesAssists();
        clearAll();
        map.clearAll();
    }

    @Override
    public void done()
    {
    }
}
