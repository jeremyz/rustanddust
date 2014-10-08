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
            if (ctrl.cfg.showMoves) map.enablePossibleMoves(false);
            if (ctrl.cfg.showTargets) map.enablePossibleTargets(false);
            if (ctrl.cfg.showMoveAssists) map.enableMoveAssists(false);
            map.buildPossibleMoves(pawn, hex);
            map.buildPossibleTargets(pawn, hex);
            map.buildMoveAssists(pawn, hex);
            if (ctrl.cfg.showMoves) map.enablePossibleMoves(true);
            if (ctrl.cfg.showTargets) map.enablePossibleTargets(true);
            if (ctrl.cfg.showMoveAssists) map.enableMoveAssists(true);
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
        map.enableMoveAssists(false);
    }
}
