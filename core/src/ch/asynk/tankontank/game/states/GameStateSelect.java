package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.GameCtrl;

public class GameStateSelect extends GameStateCommon
{
    private boolean jumpToMove;

    public GameStateSelect(GameCtrl ctrl, Map map)
    {
        super(ctrl, map);
    }

    @Override
    public void enter(boolean flag)
    {
        ctrl.hud.hide();
        jumpToMove = false;
    }

    @Override
    public void leave()
    {
        hidePossibleTargetsMovesAssists();
    }

    @Override
    public void touchDown()
    {
        if (map.isInPossibleMoves(downHex))
            jumpToMove = true;
        else
            reselectHex();
    }

    @Override
    public void touchUp()
    {
        if (jumpToMove) {
            to.set(downHex);
            ctrl.setState(State.MOVE);
            return;
        }

        hidePossibleTargetsMovesAssists();
        if (hasPawn()) {
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
            map.clearPossibleTargetsMovesAssists();
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
