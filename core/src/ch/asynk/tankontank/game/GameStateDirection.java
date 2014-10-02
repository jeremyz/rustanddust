package ch.asynk.tankontank.game;

public class GameStateDirection implements GameState
{
    @Override
    public boolean drag(float dx, float dy)
    {
        return false;
    }

    @Override
    public void touchDown()
    {
        System.out.println("GameStateDirection: touchDown()");
        // enableOverlayOn(to.x, to.y, Hex.ROSE, enable);
    }

    @Override
    public void touchUp()
    {
        System.out.println("GameStateDirection: touchUp()");
    }

    @Override
    public void abort()
    {
        System.err.println("GameStateDirection: abort");
    }
}
