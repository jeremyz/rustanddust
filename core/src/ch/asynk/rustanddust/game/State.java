package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.game.Ctrl.MsgType;

public interface State
{
    enum StateType
    {
        REPLAY,
        WAIT_EVENT,
        SELECT,
        MOVE,
        ENGAGE,
        PROMOTE,
        ANIMATION,
        REINFORCEMENT,
        DEPLOYMENT,
    };

    public void touch(Hex hex);

    public void enterFrom(StateType prevState);

    public boolean processMsg(MsgType msg, Object data);
}
