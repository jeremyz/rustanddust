package ch.asynk.tankontank.game;

import ch.asynk.tankontank.game.Map;

public class GameCtrl
{
    private GameState noneState = new GameStateNone();
    private GameState pathState = new GameStatePath();
    private GameState directionState = new GameStateDirection();

    private GameState state;

    public GameCtrl(Map map)
    {
        this.noneState = new GameStateNone(this, map);
        this.pathState = new GameStatePath();
        this.directionState = new GameStateDirection();

        this.state = noneState;
    }

    public void setState(GameState.State state, boolean forward)
    {
        switch(state) {
            case NONE:
                this.state = noneState;
                break;
            case PATH:
                this.state = pathState;
                break;
            case DIRECTION:
                this.state = directionState;
                break;
            default:
                break;
        }

        if (forward)
            this.state.touchDown();
    }

    public void touchDown(float x, float y)
    {
        if (GameStateCommon.down(x, y))
            state.touchDown();
    }

    public void touchUp(float x, float y)
    {
        if (GameStateCommon.up(x, y))
            state.touchUp();
    }

    public boolean drag(float dx, float dy)
    {
        return state.drag(dx, dy);
    }
}
