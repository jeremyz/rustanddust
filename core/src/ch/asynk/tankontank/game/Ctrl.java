package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Army;
import ch.asynk.tankontank.game.states.StateCommon;
import ch.asynk.tankontank.game.states.StateSelect;
import ch.asynk.tankontank.game.states.StateMove;
import ch.asynk.tankontank.game.states.StateRotate;
import ch.asynk.tankontank.game.states.StatePromote;
import ch.asynk.tankontank.game.states.StateAttack;
import ch.asynk.tankontank.game.states.StateAnimation;

public class Ctrl implements Disposable
{
    private final TankOnTank game;

    public Map map;
    public Hud hud;
    public Config cfg;
    private int player;
    private Player players[] = new Player[2];

    private State selectState;
    private State pathState;
    private State rotateState;
    private State promoteState;
    private State attackState;
    private State animationState;

    private int animationCount = 0;

    private State state;

    public Ctrl(final TankOnTank game)
    {
        this.game = game;
        this.cfg = game.config;
        game.ctrl = this;

        this.players[0] = game.factory.getPlayer(Army.GE);
        this.players[1] = game.factory.getPlayer(Army.US);
        this.map = game.factory.getMap(Factory.MapType.MAP_A);
        game.factory.fakeSetup(map, players[0], players[1]);
        player = (new java.util.Random()).nextInt(2);

        this.selectState = new StateSelect(this, map);
        this.pathState = new StateMove();
        this.rotateState = new StateRotate();
        this.promoteState = new StatePromote();
        this.attackState = new StateAttack();
        this.animationState = new StateAnimation();

        this.state = selectState;

        this.hud = new Hud(this, game);

        player().turnStart();
    }

    @Override
    public void dispose()
    {
        hud.dispose();
        map.dispose();
    }

    public Player player()
    {
        return this.players[player];
    }

    public Player opponent()
    {
        return this.players[((player + 1) % 2)];
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
        player().turnEnd();
        player = ((player + 1) % 2);
        player().turnStart();
        hud.notify(player().getName() + "'s turn");
    }

    private void checkTurnEnd()
    {
        if (map.activatedPawnsCount() > 0) {
            player().burnDownOneAp();
        }
        if (player().apExhausted())
            nextPlayer();
    }

    public void endPlayerTurn()
    {
        state.abort();
        nextPlayer();
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
            case PROMOTE:
                this.state = promoteState;
                break;
            case ATTACK:
                this.state = attackState;
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
