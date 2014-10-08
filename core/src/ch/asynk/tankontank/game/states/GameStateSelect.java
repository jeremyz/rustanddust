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
    public void touchDown()
    {
        reselectHex();
    }

    @Override
    public void touchUp()
    {
        int moves = 0;
        int targets = 0;
        int assists = 0;
        showPossibleTargetsMovesAssists(false);
        if (hasPawn()) {
            moves = map.buildPossibleMoves(pawn, hex);
            targets = map.buildPossibleTargets(pawn, hex);
            assists = map.buildMoveAssists(pawn, hex);
            showPossibleTargetsMovesAssists(true);
        } else
            map.clearPossibleTargetsMovesAssists();
    }

    @Override
    public void abort()
    {
        showPossibleTargetsMovesAssists(false);
        super.abort();
    }
}
