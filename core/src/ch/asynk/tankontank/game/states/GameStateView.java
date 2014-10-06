package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.GameCtrl;

public class GameStateView extends GameStateCommon
{
    public GameStateView(GameCtrl ctrl, Map map)
    {
        super(ctrl, map);
    }

    @Override
    public void touchUp()
    {
        if (hexHasUnit()) {
            // FIXME must be one of it's own
            setPawn();
            map.buildAndShowPossibleMoves(pawn, hex);
            map.buildAndShowPossibleTargets(pawn, hex);
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
        unselectHex();
    }
}
