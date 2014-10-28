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
            if (map.possibleTargets.contains(upHex)) {
                // quick fire
                to = upHex;
                ctrl.setState(StateType.ATTACK);
                return;
            }
        }

        selectHexAndPawn(upHex);
        hidePossibleTargetsMovesAssists();

        if (hasPawn() && (!isEnemy || ctrl.cfg.showEnemyPossibilities)) {
            int moves = map.buildPossibleMoves(selectedPawn);
            int targets = 0;
            if (isEnemy)
                targets = map.buildPossibleTargets(selectedPawn, ctrl.player.unitIterator());
            else
                targets = map.buildPossibleTargets(selectedPawn, ctrl.opponent.unitIterator());
            int assists = map.buildMoveAssists(selectedPawn);
            showPossibleTargetsMovesAssists(selectedPawn);
            ctrl.hud.show(
                ctrl.player.canPromote(selectedPawn),
                selectedPawn.canMove(),
                (selectedPawn.canMove() && (moves > 0)),
                (selectedPawn.canAttack() && (targets > 0)),
                false,
                false
                );
        } else {
            ctrl.hud.hide();
            map.clearAll();
        }
        if (selectedPawn != null) ctrl.hud.notify(selectedPawn.toString());
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
