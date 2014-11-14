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
        REINFORCEMENT,
        ABORT,
        DONE
    };

    public void enter(StateType prevState);

    public void leave(StateType nextState);

    public StateType abort();

    public StateType done();

    public void touchDown();

    public void touchUp();

    public boolean downInMap(float x, float y);

    public boolean upInMap(float x, float y);
}
