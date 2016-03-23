package ch.asynk.rustanddust.game;

public interface State
{
    enum StateType {
        LOADING,
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

    public void enterFrom(StateType prevState);

    public void leaveFor(StateType nextState);

    public StateType abort();

    public StateType execute();

    public void touch(Hex hex);
}
