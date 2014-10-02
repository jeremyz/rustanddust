package ch.asynk.tankontank.game;

public interface GameState
{
    enum State {
        NONE,
        PATH,
        DIRECTION
    };

    public void abort();

    public void touchDown();

    public void touchUp();

    public boolean drag(float dx, float dy);
}
