package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.GameCtrl;

public class GameStateNone extends GameStateCommon
{
    public GameStateNone(GameCtrl ctrl, Map map)
    {
        super(ctrl, map);
    }

    @Override
    public void touchDown()
    {
        if (map.isInPossibleMoves(downHex)) {
            map.enablePossibleTargets(false);
            ctrl.setState(State.PATH, true);
        } else {
            unselectHex();
            setHex();
            selectHex();
        }
    }

    @Override
    public void touchUp()
    {
        if (hexHasUnit()) {
            setPawn();
            map.showPossibleActions(pawn, hex, true);
        } else {
            map.showPossibleActions(pawn, hex, false);
            unselectHex();
        }
    }
}
