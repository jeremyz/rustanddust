package ch.asynk.tankontank.game.states;

public class GameStateAnimation extends GameStateCommon
{
    @Override
    public void enter()
    {
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
    }

    @Override
    public void abort()
    {
        unselectHex(hex);
        pawn = null;
        super.abort();
    }
}
