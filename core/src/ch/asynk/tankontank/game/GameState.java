package ch.asynk.tankontank.game;

public interface GameState
{
    enum State {
        NONE,
        PATH,
        DIRECTION
    };

    public void touchDown();

    public void touchUp();
}
