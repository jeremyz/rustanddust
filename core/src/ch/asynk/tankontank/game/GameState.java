package ch.asynk.tankontank.game;

public interface GameState
{
    enum State {
        VIEW,
        MOVE,
        DIRECTION,
        ROTATE,
        ANIMATION
    };

    public void enter();

    public void leave();

    public void abort();

    public void touchDown();

    public void touchUp();

    public boolean downInMap(float x, float y);

    public boolean upInMap(float x, float y);
}
