package ch.asynk.rustanddust.game;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.math.Vector3;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.State.StateType;
import ch.asynk.rustanddust.game.states.StateCommon;
import ch.asynk.rustanddust.game.states.StateSelect;
import ch.asynk.rustanddust.game.states.StateMove;
import ch.asynk.rustanddust.game.states.StateRotate;
import ch.asynk.rustanddust.game.states.StatePromote;
import ch.asynk.rustanddust.game.states.StateEngage;
import ch.asynk.rustanddust.game.states.StateBreak;
import ch.asynk.rustanddust.game.states.StateAnimation;
import ch.asynk.rustanddust.game.states.StateReinforcement;
import ch.asynk.rustanddust.game.states.StateDeployment;
import ch.asynk.rustanddust.game.states.StateWithdraw;

public class Ctrl implements Disposable
{
    private final RustAndDust game;
    public final Battle battle;

    public Map map;
    public Hud hud;
    public Config cfg;
    public boolean blockMap;
    public boolean blockHud;

    public Vector3 mapTouch = new Vector3();
    public Vector3 hudTouch = new Vector3();

    private State selectState;
    private State pathState;
    private State rotateState;
    private State promoteState;
    private State engageState;
    private State breakState;
    private State animationState;
    private State reinforcementState;
    private State deploymentState;
    private State withdrawState;

    private int animationCount = 0;

    private State state;
    private StateType stateType;
    private StateType stateAfterAnimation;

    public Ctrl(final RustAndDust game, final Battle battle)
    {
        this.game = game;
        this.battle = battle;
        this.cfg = game.config;
        game.ctrl = this;

        this.map = battle.setup();

        this.selectState = new StateSelect();
        this.pathState = new StateMove();
        this.rotateState = new StateRotate();
        this.promoteState = new StatePromote();
        this.engageState = new StateEngage();
        this.breakState = new StateBreak();
        this.animationState = new StateAnimation();
        this.reinforcementState = new StateReinforcement();
        this.deploymentState = new StateDeployment();
        this.withdrawState = new StateWithdraw();

        this.state = selectState;
        this.stateType = StateType.DONE;
        StateCommon.set(game);

        this.hud = new Hud(game);
        this.blockMap = false;
        this.blockHud = false;

        hud.notify(battle.toString(), 2, Position.MIDDLE_CENTER, false);
        startPlayerTurn();
    }

    @Override
    public void dispose()
    {
        hud.dispose();
        map.dispose();
    }

    public boolean isInAction()
    {
        return (state != selectState);
    }

    public void animationsOver()
    {
        if (hud.dialogActive())
            return;
        if (stateType == StateType.ANIMATION)
            leaveAnimationState();
    }

    private void leaveAnimationState()
    {

        StateType tmp = stateAfterAnimation;
        stateAfterAnimation = StateType.DONE;
        setState(tmp);
    }

    private void startPlayerTurn()
    {
        battle.getPlayer().turnStart();
        // hud.notify(battle.getPlayer().getName() + "'s turn", 2, Position.MIDDLE_CENTER, true);
        if (battle.hasReinforcement())
            hud.notify("You have reinforcement", 2, Position.MIDDLE_CENTER, true);
        hud.update();
        setState(battle.getState());
    }

    private void endPlayerTurn()
    {
        battle.getPlayer().turnEnd();
        Player winner = battle.getVictor();
        if (winner != null)
            hud.victory(winner, ((winner == battle.getPlayer()) ? battle.getOpponent() : battle.getPlayer()));
    }

    private StateType actionAborted()
    {
        hud.notify("Action canceled");
        StateType nextState = this.state.abort();

        if (nextState == StateType.ABORT)
            nextState = battle.getState();

        return nextState;
    }

    private void turnDone()
    {
        map.turnDone();
        endPlayerTurn();
        battle.changePlayer();
        startPlayerTurn();
    }

    private StateType actionDone()
    {
        StateType nextState = this.state.execute();

        if (nextState == StateType.DONE) {
            map.actionDone();
            if (map.unitsActivatedSize() > 0) {
                RustAndDust.debug("Ctrl", "burn down 1AP");
                hud.notify("1 Action Point burnt", 0.6f, Position.BOTTOM_CENTER, false);
                battle.getPlayer().burnDownOneAp();
                hud.update();
            }
            if (battle.getPlayer().apExhausted())
                hud.notifyNoMoreAP();
        }

        if (nextState == StateType.DONE)
            nextState = battle.getState();

        return nextState;
    }

    private StateType deploymentDone()
    {
        map.actionDone();
        return this.state.execute();
    }

    public void setState(StateType nextState)
    {
        if (nextState == StateType.ABORT)
            nextState = actionAborted();
        else if (nextState == StateType.DONE) {
            if (stateType == StateType.DEPLOYMENT)
                nextState = deploymentDone();
            else
                nextState = actionDone();
        }

        if (stateType == StateType.ANIMATION) {
            this.blockMap = hud.dialogActive();
        }
        hud.playerInfo.blockEndOfTurn(nextState != StateType.SELECT);

        this.state.leave(nextState);

        RustAndDust.debug("Ctrl", String.format("  %s -> %s : %s", stateType, nextState, battle.getPlayer()));

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
            case ENGAGE:
                this.state = engageState;
                break;
            case BREAK:
                this.state = breakState;
                break;
            case WITHDRAW:
                this.state = withdrawState;
                break;
            case ANIMATION:
                this.blockMap = true;
                this.state = animationState;
                break;
            case REINFORCEMENT:
                this.state = reinforcementState;
                break;
            case DEPLOYMENT:
                this.state = deploymentState;
                break;
            default:
                break;
        }

        StateType tmp = stateType;
        stateType = nextState;

        this.state.enter(tmp);

    }

    public boolean drag(int dx, int dy)
    {
        if (!blockHud && hud.drag(hudTouch.x, hudTouch.y, dx, dy))
            return true;
        return false;
    }

    public void touchDown()
    {
        if (!blockHud && hud.touchDown(hudTouch.x, hudTouch.y))
            return;

        if (!blockMap && state.downInMap(mapTouch.x, mapTouch.y))
            state.touchDown();
    }

    public void touchUp()
    {
        if (!blockHud && hud.touchUp(hudTouch.x, hudTouch.y))
            return;

        if (!blockMap && state.upInMap(mapTouch.x, mapTouch.y))
            state.touchUp();
    }

    public void stateTouchUp()
    {
        state.downInMap(-1, -1);
        state.upInMap(-1, -1);
        state.touchUp();
    }

    public boolean isInAnimation()
    {
        return (this.stateType == StateType.ANIMATION);
    }

    public void setAfterAnimationState(StateType after)
    {
        stateAfterAnimation = after;
    }

    public boolean checkDeploymentDone()
    {
        boolean done = battle.deploymentDone();
        if (done)
            hud.askEndDeployment();
        return done;
    }

    public void reinforcementHit()
    {
        if (this.stateType == StateType.SELECT)
            setState(StateType.REINFORCEMENT);
        else if (this.stateType == StateType.REINFORCEMENT)
            setState(StateType.SELECT);
    }

    // Hud callbacks
    public void engagementPanelClosed()
    {
        if (animationCount == 0)
            leaveAnimationState();
    }

    public void endDeployment()
    {
        setState(StateType.DONE);
        turnDone();
    }

    public void endGame()
    {
        game.switchToMenu();
    }

    public void endPlayerTurn(boolean abort)
    {
        if (abort)
            state.abort();
        turnDone();
    }

    public void exitBoard(boolean doit)
    {
        if (doit)
            setState(StateType.DONE);
        else
            setState(StateType.ABORT);
    }
}
