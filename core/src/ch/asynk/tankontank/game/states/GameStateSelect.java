package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.GameCtrl;

public class GameStateSelect extends GameStateCommon
{
    public GameStateSelect(GameCtrl ctrl, Map map)
    {
        super(ctrl, map);
    }

    @Override
    public void enter()
    {
    }

    @Override
    public void leave()
    {
        showPossibleTargetsMovesAssists(false);
    }

    @Override
    public void touchDown()
    {
        reselectHex();
    }

    @Override
    public void touchUp()
    {
        showPossibleTargetsMovesAssists(false);
        if (hasPawn()) {
            int moves = map.buildPossibleMoves(pawn, hex);
            int targets = map.buildPossibleTargets(pawn, hex);
            int assists = map.buildMoveAssists(pawn, hex);
            showPossibleTargetsMovesAssists(true);
            ctrl.hud.show(
                pawn.canMove(),
                (pawn.canMove() && (moves > 0)),
                (pawn.canAttack() && (targets > 0)),
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
        showPossibleTargetsMovesAssists(false);
        super.abort();
    }
}
