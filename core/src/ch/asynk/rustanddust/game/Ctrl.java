package ch.asynk.rustanddust.game;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.ctrl.Solo;
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

public abstract class Ctrl implements Disposable
{
    public final RustAndDust game;
    public final Battle battle;

    public Map map;
    public Hud hud;
    public boolean blockMap;
    public boolean blockHud;
    private Hex touchedHex;

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

    public abstract void init();
    protected abstract void processTurn();

    public static Ctrl getCtrl(final RustAndDust game)
    {
        Ctrl ctrl = null;
        switch(game.config.gameMode) {
            case SOLO:
                ctrl = new Solo(game, game.config.battle);
                break;
        }
        return ctrl;
    }

    public Ctrl(final RustAndDust game, final Battle battle)
    {
        game.ctrl = this;
        this.game = game;
        this.battle = battle;
        this.map = game.factory.getMap(battle.getMapType());
        this.hud = new Hud(game);

        this.blockMap = false;
        this.blockHud = false;
        this.touchedHex = null;

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

        setState(battle.setup(this));

        this.hud.notify(battle.toString(), 2, Position.MIDDLE_CENTER, false);
    }

    @Override
    public void dispose()
    {
        hud.dispose();
        map.dispose();
    }

    // INPUTS

    public boolean drag(float x, float y, int dx, int dy)
    {
        if (!blockHud && hud.drag(x, y, dx, dy))
            return true;
        return false;
    }

    public void touchDown(float hudX, float hudY, float mapX, float mapY)
    {
        boolean inAnimation = (this.stateType == StateType.ANIMATION);

        if (!blockHud && hud.hit(hudX, hudY, inAnimation))
            return;

        touchedHex = (blockMap ? null : map.getHexAt(mapX, mapY));
    }

    public void touchUp(float hudX, float hudY, float mapX, float mapY)
    {
        if (!blockMap && (touchedHex != null) && (touchedHex == map.getHexAt(mapX, mapY)))
            state.touch(touchedHex);
    }

    // Map callbacks

    public void animationsOver()
    {
        if (hud.dialogActive())
            hud.notifyAnimationsEnd();
        if (stateType == StateType.ANIMATION) {
            StateType tmp = stateAfterAnimation;
            stateAfterAnimation = StateType.DONE;
            setState(tmp);
        }
    }

    // State callbacks

    public void setAfterAnimationState(StateType after)
    {
        stateAfterAnimation = after;
    }

    public boolean checkDeploymentDone()
    {
        boolean done = battle.isDeploymentDone();
        if (done)
            hud.askEndDeployment();
        return done;
    }

    // Hud callbacks

    public void endGame()
    {
        game.switchToMenu();
    }

    public void showEntryZone()
    {
        if ((stateType == StateType.DEPLOYMENT) || (stateType == StateType.REINFORCEMENT))
            state.touch(null);
    }

    public void endDeployment()
    {
        setState(StateType.DONE);
        turnDone();
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

    public void reinforcementHit()
    {
        if (this.stateType == StateType.SELECT)
            setState(StateType.REINFORCEMENT);
        else if (this.stateType == StateType.REINFORCEMENT)
            setState(StateType.SELECT);
    }

    //

    private void turnDone()
    {
        if (battle.turnDone())
            hud.victory(battle.getPlayer(), battle.getOpponent());
        else {
            if (battle.hasReinforcement())
                hud.notify("You have reinforcement", 2, Position.MIDDLE_CENTER, true);
            hud.update();
            if (!battle.getPlayer().canDoSomething()) {
                hud.notify("No available Actions");
                setState(StateType.TURN_OVER);
            } else
                setState(battle.getState());
        }
    }

    //

    public void setState(StateType nextState)
    {
        if (nextState == StateType.ABORT)
            nextState = abortAction();
        else if (nextState == StateType.DONE) {
            if (stateType == StateType.DEPLOYMENT)
                nextState = completeDeployment();
            else
                nextState = completeAction();
        }

        if (stateType == StateType.ANIMATION)
            this.blockMap = hud.dialogActive();

        hud.playerInfo.blockEndOfTurn(nextState != StateType.SELECT);

        this.state.leave(nextState);

        this.state = getNextState(nextState);

        StateType tmp = stateType;
        stateType = nextState;

        this.state.enter(tmp);

        if (nextState == StateType.TURN_OVER)
            turnDone();
    }

    private StateType completeDeployment()
    {
        battle.actionDone();
        return this.state.execute();
    }

    private StateType abortAction()
    {
        hud.notify("Action canceled");
        StateType nextState = this.state.abort();

        if (nextState == StateType.ABORT)
            nextState = battle.getState();

        return nextState;
    }

    private StateType completeAction()
    {
        StateType nextState = this.state.execute();

        if (nextState == StateType.DONE) {
            if (battle.actionDone()) {
                hud.notify("1 Action Point burnt");
                hud.update();
            }
            if (battle.getPlayer().apExhausted()) {
                hud.notify("No more Action Points");
                nextState = StateType.TURN_OVER;
            } else if (!battle.getPlayer().canDoSomething()) {
                hud.notify("No available Actions");
                nextState = StateType.TURN_OVER;
            } else
                nextState = battle.getState();
        }

        return nextState;
    }

    private State getNextState(StateType nextState)
    {
        RustAndDust.debug("Ctrl", String.format("  %s -> %s : %s", stateType, nextState, battle.getPlayer()));

        State state = this.state;

        switch(nextState) {
            case SELECT:
                state = selectState;
                break;
            case MOVE:
                state = pathState;
                break;
            case ROTATE:
                state = rotateState;
                break;
            case PROMOTE:
                state = promoteState;
                break;
            case ENGAGE:
                state = engageState;
                break;
            case BREAK:
                state = breakState;
                break;
            case WITHDRAW:
                state = withdrawState;
                break;
            case ANIMATION:
                state = animationState;
                this.blockMap = true;
                break;
            case REINFORCEMENT:
                state = reinforcementState;
                break;
            case DEPLOYMENT:
                state = deploymentState;
                break;
            default:
                break;
        }

        return state;
    }
}
