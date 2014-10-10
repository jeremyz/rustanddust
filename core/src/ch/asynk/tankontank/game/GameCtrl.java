package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.states.GameStateCommon;
import ch.asynk.tankontank.game.states.GameStateSelect;
import ch.asynk.tankontank.game.states.GameStateMove;
import ch.asynk.tankontank.game.states.GameStateRotate;
import ch.asynk.tankontank.game.states.GameStateAnimation;

public class GameCtrl implements Disposable
{

    public class Config
    {
        public boolean showMoves;
        public boolean showTargets;
        public boolean showMoveAssists;
        public boolean canCancel;
        public boolean mustValidate;

        public Config()
        {
            this.showMoves = true;
            this.showTargets = true;
            this.showMoveAssists = true;
            this.mustValidate = false;
            this.canCancel = true;
        }
    }

    private final TankOnTank game;

    private GameFactory factory;
    public Map map;
    public Hud hud;
    public Config cfg;

    private GameState selectState;
    private GameState pathState;
    private GameState rotateState;
    private GameState animationState;

    private int animationCount = 0;

    private GameState state;

    public GameCtrl(final TankOnTank game)
    {
        this.game = game;

        this.cfg = new Config();

        this.factory = new GameFactory(game.manager);
        this.hud = new Hud(this, game);
        this.map = factory.getMap(this, game.manager, GameFactory.MapType.MAP_A);

        this.selectState = new GameStateSelect(this, map);
        this.pathState = new GameStateMove();
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

    public void setAnimationCount(int count)
    {
        animationCount = count;
    }

    public void animationDone()
    {
        animationCount -= 1;
        if (animationCount == 0) {
            GameState.State next = state.getNextState();
            state.setNextState(GameState.State.SELECT);
            setState(next, (next == GameState.State.SELECT));
        }
    }

    public void setState(GameState.State state)
    {
        setState(state, true);
    }

    public void setState(GameState.State state, boolean normal)
    {
        this.state.leave();

        switch(state) {
            case SELECT:
                this.state = selectState;
                break;
            case MOVE:
                this.state = pathState;
                break;
            case ROTATE:
                this.state = rotateState;
                break;
            case ANIMATION:
                this.state = animationState;
                break;
            default:
                break;
        }

        this.state.enter(normal);
    }

    public void abort()
    {
        state.abort();
    }

    public void done()
    {
        state.done();
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
