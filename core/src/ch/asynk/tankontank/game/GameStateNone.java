package ch.asynk.tankontank.game;

import ch.asynk.tankontank.game.Map;

public class GameStateNone extends GameStateCommon
{
    public GameStateNone()
    {
        super();
    }

    public GameStateNone(GameCtrl ctrl, Map map)
    {
        super(ctrl, map);
    }

    @Override
    public boolean drag(float dx, float dy)
    {
        return false;
    }

    @Override
    public void touchDown()
    {
        System.out.println("GameStateNone: touchDown()");
        if (map.isInPossibleMoves(downHex)) {
            ctrl.setState(State.PATH, true);
        } else {
            unselectHex();
            setHex();
            selectHex();
            if (hexHasUnit()) {
                setPawn();
                map.showPossibleActions(pawn, hex, true);
            } else {
                map.showPossibleActions(pawn, hex, false);
            }
        }
    }

    @Override
    public void touchUp()
    {
        System.out.println("GameStateNone: touchUp()");
        if (!hexHasUnit())
            unselectHex();
    }

    @Override
    public void abort()
    {
        System.err.println("GameStateNone: abort");
    }
}
