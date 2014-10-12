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
        if (selectedHex.x != -1) unselectHex(selectedHex);
    }

    @Override
    public void touchUp()
    {
        if (!isEnemy && map.isInPossibleMoves(upHex)) {
            // quick move
            to.set(upHex);
            ctrl.setState(StateType.MOVE);
            return;
        }

        selectHexAndPawn(upHex);
        hidePossibleTargetsMovesAssists();

        if (hasPawn() && (!isEnemy || ctrl.cfg.showEnemyPossibilities)) {
            int moves = map.buildPossibleMoves(selectedPawn, selectedHex);
            int targets = map.buildPossibleTargets(selectedPawn, selectedHex);
            int assists = map.buildMoveAssists(selectedPawn, selectedHex);
            showPossibleTargetsMovesAssists(selectedPawn);
            ctrl.hud.show(
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
    }

    @Override
    public void abort()
    {
    }

    @Override
    public void done()
    {
    }
}