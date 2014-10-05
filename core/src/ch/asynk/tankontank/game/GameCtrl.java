package ch.asynk.tankontank.game;

import ch.asynk.tankontank.game.Map;

import ch.asynk.tankontank.game.states.GameStateCommon;
import ch.asynk.tankontank.game.states.GameStateNone;
import ch.asynk.tankontank.game.states.GameStatePath;
import ch.asynk.tankontank.game.states.GameStateDirection;

public class GameCtrl
{
    private GameState noneState;
    private GameState pathState;
    private GameState directionState ;

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
        if (state.downInMap(x, y))
            state.touchDown();
    }

    public void touchUp(float x, float y)
    {
        if (state.upInMap(x, y))
            state.touchUp();
    }
}
