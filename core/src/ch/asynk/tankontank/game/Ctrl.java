package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.states.StateCommon;
import ch.asynk.tankontank.game.states.StateSelect;
import ch.asynk.tankontank.game.states.StateMove;
import ch.asynk.tankontank.game.states.StateRotate;
import ch.asynk.tankontank.game.states.StatePromote;
import ch.asynk.tankontank.game.states.StateAttack;
import ch.asynk.tankontank.game.states.StateBreak;
import ch.asynk.tankontank.game.states.StateAnimation;
import ch.asynk.tankontank.game.states.StateEntry;

import ch.asynk.tankontank.screens.OptionsScreen;

public class Ctrl implements Disposable
{
    private final TankOnTank game;
    public final Battle battle;

    public Map map;
    public Hud hud;
    public Config cfg;
    public Player player;
    public Player opponent;
    public boolean blockMap;

    private State selectState;
    private State pathState;
    private State rotateState;
    private State promoteState;
    private State attackState;
    private State breakState;
    private State animationState;
    private State entryState;

    private int animationCount = 0;

    private State state;
    private State.StateType stateType;

    public Ctrl(final TankOnTank game, final Battle battle)
    {
        this.game = game;
        this.battle = battle;
        this.cfg = game.config;
        game.ctrl = this;

        this.player = battle.getFirstPlayer();
        this.opponent = battle.getSecondPlayer();
        this.map = battle.getMap();
        battle.setup(map, player, opponent);

        this.selectState = new StateSelect(this, map);
        this.pathState = new StateMove();
        this.rotateState = new StateRotate();
        this.promoteState = new StatePromote();
        this.attackState = new StateAttack();
        this.breakState = new StateBreak();
        this.animationState = new StateAnimation();
        this.entryState = new StateEntry();

        this.state = selectState;
        this.stateType = State.StateType.SELECT;

        this.hud = new Hud(this, game);
        this.blockMap = false;

        player.turnStart();
        hud.playerInfo.update(player, battle.getHudPosition(player));
    }

    @Override
    public void dispose()
    {
        hud.dispose();
        map.dispose();
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
        if (animationCount == 0)
            state.done();
        if (animationCount < 0)
            TankOnTank.debug("    animationCount < 0");
    }

    private void nextPlayer()
    {
        player.turnEnd();
        Player winner = battle.checkVictory(this);
        if (winner != null) {
            hud.victory(winner, ((winner == player) ? opponent : player));
        }
        Player tmp = player;
        player = opponent;
        opponent = tmp;
        player.turnStart();
        hud.playerInfo.update(player, battle.getHudPosition(player));
        hud.notify(player.getName() + "'s turn");
    }

    private void checkTurnEnd()
    {
        if (map.activatedPawns.size() > 0) {
            player.burnDownOneAp();
            hud.playerInfo.update(player, battle.getHudPosition(player));
        }
        if (player.apExhausted())
            nextPlayer();
    }

    public void endPlayerTurn()
    {
        state.abort();
        nextPlayer();
    }

    public void setState(State.StateType state)
    {
        if (state == State.StateType.ABORT) {
            hud.notify("Action canceled");
            this.state.abort();
        }
        else if (state == State.StateType.DONE)
            this.state.done();
        else
            setState(state, true);
    }

    public void setState(State.StateType state, boolean normal)
    {
        hud.changeState(stateType, state);
        this.state.leave(state);

        TankOnTank.debug("  switch to : " + state + " " + normal);
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
            case BREAK:
                this.state = breakState;
                break;
            case ANIMATION:
                this.state = animationState;
                break;
            case ENTRY:
                this.state = entryState;
                break;
            default:
                break;
        }
        stateType = state;

        this.state.enter(normal);
    }

    public void touchDown(float x, float y)
    {
        if (!blockMap && state.downInMap(x, y))
            state.touchDown();
    }

    public void touchUp(float x, float y)
    {
        if (!blockMap && state.upInMap(x, y))
            state.touchUp();
    }

    public void endGame()
    {
        game.setScreen(new OptionsScreen(game));
    }
}
