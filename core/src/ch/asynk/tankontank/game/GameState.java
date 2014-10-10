package ch.asynk.tankontank.game;

public interface GameState
{
    enum State {
        SELECT,
        MOVE,
        ROTATE,
        ANIMATION
    };

    public void enter(boolean normal);

    public void leave();

    public void abort();

    public void done();

    public void touchDown();

    public void touchUp();

    public boolean downInMap(float x, float y);

    public boolean upInMap(float x, float y);

    public GameState.State getNextState();

    public void setNextState(GameState.State next);
}
