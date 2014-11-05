package ch.asynk.tankontank.game;

public interface State
{
    enum StateType {
        SELECT,
        MOVE,
        ROTATE,
        ATTACK,
        BREAK,
        PROMOTE,
        ANIMATION,
        ENTRY
    };

    public void enter(boolean flag);

    public void leave(StateType nextState);

    public void abort();

    public void done();

    public void touchDown();

    public void touchUp();

    public boolean downInMap(float x, float y);

    public boolean upInMap(float x, float y);

    public StateType getNextState();

    public void setNextState(StateType next);
}
