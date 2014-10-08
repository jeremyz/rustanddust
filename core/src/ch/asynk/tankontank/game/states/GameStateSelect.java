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
        if (hasPawn()) {
            map.buildAndShowPossibleMoves(pawn, hex);
            map.buildAndShowPossibleTargets(pawn, hex);
            map.buildAndShowMoveAssist(pawn, hex);
        } else {
            clear();
        }
    }

    @Override
    public void abort()
    {
        clear();
        super.abort();
    }

    private void clear()
    {
        map.enablePossibleMoves(false);
        map.enablePossibleTargets(false);
        map.enableMoveAssist(false);
    }
}
