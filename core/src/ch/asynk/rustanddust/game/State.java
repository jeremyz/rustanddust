package ch.asynk.rustanddust.game;

public interface State
{
    enum StateType {
        SELECT,
        MOVE,
        ROTATE,
        ENGAGE,
        BREAK,
        PROMOTE,
        ANIMATION,
        REINFORCEMENT,
        DEPLOYMENT,
        WITHDRAW,
        ABORT,
        DONE,
        TURN_OVER
    };

    public void enter(StateType prevState);

    public void leave(StateType nextState);

    public StateType abort();

    public StateType execute();

    public void touchDown();

    public void touchUp();

    public boolean downInMap(float x, float y);

    public boolean upInMap(float x, float y);
}
