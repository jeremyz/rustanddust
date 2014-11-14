package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.hud.Position;
import ch.asynk.tankontank.game.states.StateCommon;
import ch.asynk.tankontank.game.states.StateSelect;
import ch.asynk.tankontank.game.states.StateMove;
import ch.asynk.tankontank.game.states.StateRotate;
import ch.asynk.tankontank.game.states.StatePromote;
import ch.asynk.tankontank.game.states.StateAttack;
import ch.asynk.tankontank.game.states.StateBreak;
import ch.asynk.tankontank.game.states.StateAnimation;
import ch.asynk.tankontank.game.states.StateReinforcement;

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
    public boolean blockHud;

    private State selectState;
    private State pathState;
    private State rotateState;
    private State promoteState;
    private State attackState;
    private State breakState;
    private State animationState;
    private State reinforcementState;

    private int animationCount = 0;

    private State state;
    private State.StateType stateType;
    private State.StateType stateAfterAnimation;

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
        this.reinforcementState = new StateReinforcement();

        this.state = selectState;
        this.stateType = State.StateType.DONE;

        this.hud = new Hud(this, game);
        this.blockMap = false;
        this.blockHud = false;

        hud.notify(battle.toString(), 2, Position.MIDDLE_CENTER, false);
        startPlayerTurn();
        setState(battle.getState(player));
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
            setState(stateAfterAnimation);
        if (animationCount < 0)
            TankOnTank.debug("    animationCount < 0");
    }

    private void startPlayerTurn()
    {
        player.turnStart();
        hud.playerInfo.update(player, battle.getHudPosition(player));
        // setState(battle.getState(player));
        hud.notify(player.getName() + "'s turn", 2, Position.MIDDLE_CENTER, true);
    }

    private void endPlayerTurn()
    {
        TankOnTank.debug("Ctrl", "next Player");
        player.turnEnd();
        Player winner = battle.checkVictory(this);
        if (winner != null)
            hud.victory(winner, ((winner == player) ? opponent : player));
    }

    private void swicthPlayer()
    {
        endPlayerTurn();
        Player tmp = player;
        player = opponent;
        opponent = tmp;
        startPlayerTurn();
    }

    private State.StateType actionAborted()
    {
        hud.notify("Action canceled");
        State.StateType nextState = this.state.abort();

        if (nextState == State.StateType.ABORT)
            nextState = battle.getState(player);

        return nextState;
    }

    private State.StateType actionDone()
    {
        State.StateType nextState = this.state.done();

        if (map.activatedPawns.size() > 0) {
            player.burnDownOneAp();
            hud.playerInfo.update(player, battle.getHudPosition(player));
        }

        if (player.apExhausted())
            swicthPlayer();

        if (nextState == State.StateType.DONE)
            nextState = battle.getState(player);

        return nextState;
    }

    public void stateTouchUp()
    {
        this.state.touchUp();
    }

    public void toggleState(State.StateType stateA, State.StateType stateB)
    {
        if (this.stateType == stateA) {
            setState(stateB);
        } else if (this.stateType == stateB) {
            setState(stateA);
        } else {
            TankOnTank.debug("Ctrl", "wrong call to toggleState()");
        }
    }

    public void setState(State.StateType nextState)
    {
        setState(nextState, battle.getState(player));
    }

    public void setState(State.StateType nextState, State.StateType whenDone)
    {
        if (nextState == State.StateType.ABORT)
            nextState = actionAborted();
        else if (nextState == State.StateType.DONE)
            nextState = actionDone();
        else if (nextState == State.StateType.ANIMATION)
            stateAfterAnimation = whenDone;

        this.state.leave(nextState);

        TankOnTank.debug("  switch to : " + nextState);

        switch(nextState) {
            case SELECT:
                this.state = selectState;
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
            case REINFORCEMENT:
                this.state = reinforcementState;
                break;
            default:
                break;
        }

        this.state.enter(stateType);

        stateType = nextState;
    }

    public void touchDown(float hx, float hy, float mx, float my)
    {
        if (!blockHud && hud.touchDown(hx, hy))
            return;

        if (!blockMap && state.downInMap(mx, my))
            state.touchDown();
    }

    public void touchUp(float hx, float hy, float mx, float my)
    {
        if (!blockHud && hud.touchUp(hx, hy))
            return;

        if (!blockMap && state.upInMap(mx, my))
            state.touchUp();
    }

    public void endGame()
    {
        game.setScreen(new OptionsScreen(game));
    }

    public void abortPlayerTurn()
    {
        state.abort();
        swicthPlayer();
    }
}
