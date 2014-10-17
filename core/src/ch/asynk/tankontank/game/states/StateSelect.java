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
        map.hidePossibleTargetsMovesAssists();
    }

    @Override
    public void touchDown()
    {
        if (selectedHex.x != -1) map.selectHex(selectedHex, false);
    }

    @Override
    public void touchUp()
    {
        if (!isEnemy) {
            if (map.isInPossibleMoves(upHex)) {
                // quick move
                to.set(upHex);
                ctrl.setState(StateType.MOVE);
                return;
            }
            if (map.isInPossibleTargets(upHex)) {
                // quick fire
                to.set(upHex);
                ctrl.setState(StateType.ATTACK);
                return;
            }
        }

        selectHexAndPawn(upHex);
        map.hidePossibleTargetsMovesAssists();

        if (hasPawn() && (!isEnemy || ctrl.cfg.showEnemyPossibilities)) {
            int moves = map.buildPossibleMoves(selectedPawn, selectedHex);
            int targets = 0;
            if (isEnemy)
                targets = map.buildPossibleTargets(selectedPawn, selectedHex, ctrl.player.unitIterator());
            else
                targets = map.buildPossibleTargets(selectedPawn, selectedHex, ctrl.opponent.unitIterator());
            int assists = map.buildMoveAssists(selectedPawn, selectedHex);
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
        if (selectedHex.x != -1) map.selectHex(selectedHex, false);
        map.hidePossibleTargetsMovesAssists();
        clearAll();
        map.clearAll();
    }

    @Override
    public void done()
    {
    }
}
