package ch.asynk.tankontank.game;

public interface GameState
{
    enum State {
        SELECT,
        MOVE,
        DIRECTION,
        ROTATE,
        ANIMATION
    };

    public void enter(boolean reset);

    public void leave();

    public void abort();

    public void touchDown();

    public void touchUp();

    public boolean downInMap(float x, float y);

    public boolean upInMap(float x, float y);

    public GameState.State getNextState();

    public void setNextState(GameState.State next);
}
