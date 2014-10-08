package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.states.GameStateCommon;
import ch.asynk.tankontank.game.states.GameStateSelect;
import ch.asynk.tankontank.game.states.GameStateMove;
import ch.asynk.tankontank.game.states.GameStateDirection;
import ch.asynk.tankontank.game.states.GameStateRotate;
import ch.asynk.tankontank.game.states.GameStateAnimation;

public class GameCtrl implements Disposable
{
    private final TankOnTank game;

    private GameFactory factory;
    public Map map;
    public Hud hud;

    private GameState selectState;
    private GameState pathState;
    private GameState directionState;
    private GameState rotateState;
    private GameState animationState;

    private GameState state;

    public GameCtrl(final TankOnTank game)
    {
        this.game = game;

        this.factory = new GameFactory(game.manager);
        this.hud = new Hud(this, game);
        this.map = factory.getMap(this, game.manager, GameFactory.MapType.MAP_A);

        this.selectState = new GameStateSelect(this, map);
        this.pathState = new GameStateMove();
        this.directionState = new GameStateDirection();
        this.rotateState = new GameStateRotate();
        this.animationState = new GameStateAnimation();

        this.state = selectState;

        factory.fakeSetup(map);
    }

    @Override
    public void dispose()
    {
        hud.dispose();
        map.dispose();
        factory.dispose();
    }

    public boolean mayProcessTouch()
    {
        return (state != animationState);
    }

    public boolean isInAction()
    {
        return (state != selectState);
    }

    public void animationDone()
    {
        hud.reset();
        setState(GameState.State.VIEW);
    }

    public void setState(GameState.State state)
    {
        switch(state) {
            case VIEW:
                this.state = selectState;
                break;
            case MOVE:
                this.state = pathState;
                break;
            case DIRECTION:
                this.state = directionState;
                break;
            case ROTATE:
                this.state = rotateState;
                break;
            case ANIMATION:
                this.state = animationState;
                hud.disableCancel();
                break;
            default:
                break;
        }

        this.state.enter();
    }

    public void abort()
    {
        state.abort();
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
