package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.states.GameStateCommon;
import ch.asynk.tankontank.game.states.GameStateNone;
import ch.asynk.tankontank.game.states.GameStatePath;
import ch.asynk.tankontank.game.states.GameStateDirection;

public class GameCtrl implements Disposable
{
    private final TankOnTank game;

    private GameFactory factory;
    public Map map;
    public Hud hud;

    private GameState noneState;
    private GameState pathState;
    private GameState directionState ;

    private GameState state;

    public GameCtrl(final TankOnTank game)
    {
        this.game = game;

        this.factory = new GameFactory(game.manager);
        this.map = factory.getMap(this, game.manager, GameFactory.MapType.MAP_A);

        this.hud = new Hud(game);

        this.noneState = new GameStateNone(this, map);
        this.pathState = new GameStatePath();
        this.directionState = new GameStateDirection();

        this.state = noneState;

        factory.fakeSetup(map);
    }

    @Override
    public void dispose()
    {
        hud.dispose();
        map.dispose();
        factory.dispose();
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
