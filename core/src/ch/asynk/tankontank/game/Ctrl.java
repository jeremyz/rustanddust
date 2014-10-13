package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.states.StateCommon;
import ch.asynk.tankontank.game.states.StateSelect;
import ch.asynk.tankontank.game.states.StateMove;
import ch.asynk.tankontank.game.states.StateRotate;
import ch.asynk.tankontank.game.states.StateAnimation;

public class Ctrl implements Disposable
{
    private final TankOnTank game;

    private Factory factory;
    public Map map;
    public Hud hud;
    public Config cfg;
    public Player gePlayer;
    public Player usPlayer;
    public Player currentPlayer;

    private State selectState;
    private State pathState;
    private State rotateState;
    private State animationState;

    private int animationCount = 0;

    private State state;

    public Ctrl(final TankOnTank game)
    {
        this.game = game;

        this.cfg = new Config();

        this.factory = new Factory(game.manager);
        this.map = factory.getMap(this, game.manager, Factory.MapType.MAP_A);
        this.usPlayer = factory.getPlayer(Army.US);
        this.gePlayer = factory.getPlayer(Army.GE);

        this.selectState = new StateSelect(this, map);
        this.pathState = new StateMove();
        this.rotateState = new StateRotate();
        this.animationState = new StateAnimation();

        this.state = selectState;
        this.currentPlayer = factory.fakeSetup(map, gePlayer, usPlayer);

        this.hud = new Hud(this, game);

        currentPlayer.turnStart();
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
        System.err.println("    setAnimationCount(" + count + ")");
    }

    public void animationDone()
    {
        animationCount -= 1;
        if (animationCount == 0)
            state.done();
        if (animationCount < 0)
            System.err.println("    animationCount < 0");
    }

    private void nextPlayer()
    {
        currentPlayer.turnEnd();
        currentPlayer = ((currentPlayer == usPlayer) ? gePlayer : usPlayer);
        currentPlayer.turnStart();
        hud.updatePlayer();

    }

    public void setState(State.StateType state)
    {
        setState(state, true);
    }

    public void setState(State.StateType state, boolean normal)
    {
        this.state.leave(state);

        System.err.println("  switch to : " + state + " " + normal);
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
