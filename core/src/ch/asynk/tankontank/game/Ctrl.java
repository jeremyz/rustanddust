package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.states.GameStateCommon;
import ch.asynk.tankontank.game.states.GameStateSelect;
import ch.asynk.tankontank.game.states.GameStateMove;
import ch.asynk.tankontank.game.states.GameStateRotate;
import ch.asynk.tankontank.game.states.GameStateAnimation;

public class Ctrl implements Disposable
{

    public class Config
    {
        public boolean showMoves;
        public boolean showTargets;
        public boolean showMoveAssists;
        public boolean canCancel;
        public boolean mustValidate;
        public boolean showEnemyPossibilities;

        public Config()
        {
            this.showMoves = true;
            this.showTargets = true;
            this.showMoveAssists = true;
            this.canCancel = true;
            this.mustValidate = false;
            this.showEnemyPossibilities = false;
        }
    }

    private final TankOnTank game;

    private Factory factory;
    public Map map;
    public Hud hud;
    public Config cfg;
    public Player gePlayer;
    public Player usPlayer;
    public Player currentPlayer;

    private GameState selectState;
    private GameState pathState;
    private GameState rotateState;
    private GameState animationState;

    private int animationCount = 0;

    private GameState state;

    public Ctrl(final TankOnTank game)
    {
        this.game = game;

        this.cfg = new Config();

        this.factory = new Factory(game.manager);
        this.map = factory.getMap(this, game.manager, Factory.MapType.MAP_A);
        this.usPlayer = factory.getPlayer(Army.US);
        this.gePlayer = factory.getPlayer(Army.GE);

        this.selectState = new GameStateSelect(this, map);
        this.pathState = new GameStateMove();
        this.rotateState = new GameStateRotate();
        this.animationState = new GameStateAnimation();

        this.state = selectState;
        this.currentPlayer = factory.fakeSetup(map, gePlayer, usPlayer);

        this.hud = new Hud(this, game);
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
        System.err.println(" setAnimationCount(" + count + ")");
    }

    public void animationDone()
    {
        animationCount -= 1;
        if (animationCount == 0)
            state.done();
        if (animationCount < 0)
            System.err.println("animationCount < 0");
    }

    private void nextPlayer()
    {
        currentPlayer.turnEnd();
        currentPlayer = ((currentPlayer == usPlayer) ? gePlayer : usPlayer);
        currentPlayer.turnStart();
        hud.updatePlayer();

    }

    public void setState(GameState.State state)
    {
        setState(state, true);
    }

    public void setState(GameState.State state, boolean normal)
    {
        this.state.leave(state);

        System.err.println("Switch to : " + state + " " + normal);
        switch(state) {
            case SELECT:
                this.state = selectState;
                checkTurnEnd();
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

    private void checkTurnEnd()
    {
        System.err.println(" current player : " + currentPlayer.toString());
        if (map.activatedPawnsCount() > 0) {
            currentPlayer.burnDownOneAp();
        }
        if (currentPlayer.apExhausted())
            nextPlayer();
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
