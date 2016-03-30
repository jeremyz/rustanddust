package ch.asynk.rustanddust.game;

import java.io.StringWriter;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.util.IterableQueue;
import ch.asynk.rustanddust.engine.util.IterableStack;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.util.Marshal;
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
import ch.asynk.rustanddust.game.states.StateReplay;

class Event
{
    public enum Type
    {
        STATE_CHANGE,
        TURN_END;
    }

    public Type type;
    public Object data;
}

public abstract class Ctrl implements Disposable
{
    public final RustAndDust game;
    public final Battle battle;

    private final IterableQueue<Event> events = new IterableQueue<Event>(4);
    private final IterableStack<Event> freeEvents = new IterableStack<Event>(4);
    private final StringWriter writer = new StringWriter(2048);

    public Map map;
    public Hud hud;
    public boolean blockMap;
    public boolean blockHud;
    private Hex touchedHex;
    protected boolean synched;

    private final State selectState;
    private final State pathState;
    private final State rotateState;
    private final State promoteState;
    private final State engageState;
    private final State breakState;
    private final State animationState;
    private final State reinforcementState;
    private final State deploymentState;
    private final State withdrawState;
    private final State replayState;

    private int animationCount = 0;

    private State state;
    private StateType stateType;
    private StateType stateAfterAnimation;

    public abstract void init();
    protected abstract void actionDoneCb();
    protected abstract void turnDoneCb();
    public abstract void orderProcessedCb();

    public static Ctrl getCtrl(final RustAndDust game)
    {
        Ctrl ctrl = null;
        switch(game.config.gameMode) {
            case SOLO:
                ctrl = new Solo(game);
                break;
        }
        return ctrl;
    }

    public Ctrl(final RustAndDust game)
    {
        game.ctrl = this;
        this.game = game;
        this.battle = game.config.battle;
        this.hud = new Hud(game);

        this.blockMap = false;
        this.blockHud = false;
        this.touchedHex = null;
        this.synched = false;

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
        this.replayState = new StateReplay();

        this.stateType = StateType.LOADING;

        battle.init();
        this.map = battle.getMap();
        init();
        StateCommon.set(game);
        hud.update();

        this.state = selectState;
        this.stateType = StateType.DONE;
        this.stateAfterAnimation = StateType.DONE;

        setState(battle.getState());

        switch(game.config.loadMode) {
            case REPLAY_ALL:
                // TODO REPLAY_ALL
                break;
            case REPLAY_LAST:
                if (synched) {
                    this.hud.notify(battle.toString(), 2, Position.MIDDLE_CENTER, false);
                } else {
                    map.prepareReplayLastTurn();
                    setState(StateType.REPLAY);
                }
                break;
            case LOAD:
                if (synched) {
                    this.hud.notify(battle.toString(), 2, Position.MIDDLE_CENTER, false);
                } else {
                    map.prepareReplayLastAction();
                    setState(StateType.REPLAY);
                }
                break;
        }

    }

    @Override
    public void dispose()
    {
        hud.dispose();
        map.dispose();
        battle.desinit();
        events.clear();
        freeEvents.clear();
    }

    // EVENTS

    public void post(StateType stateType)
    {
        Event evt = freeEvents.pop();
        if (evt == null)
            evt = new Event();
        evt.type = Event.Type.STATE_CHANGE;
        evt.data = stateType;
        events.enqueue(evt);
    }

    public void processEvent()
    {
        if (events.size() <= 0)
            return;

        Event evt = events.dequeue();
        switch(evt.type) {
            case STATE_CHANGE:
                setState((StateType) evt.data);
                break;
            default:
                RustAndDust.error(String.format("Unhandled Event Type : %s", evt.type));
        }
        freeEvents.push(evt);
    }

    // JSON

    protected boolean isLoading()
    {
        return (stateType == StateType.LOADING);
    }

    protected void load(Marshal.Mode mode, String payload)
    {
        JsonValue root = new JsonReader().parse(payload);
        battle.load(mode, root);
    }

    protected String unload(Marshal.Mode mode)
    {
        Json json = new Json(OutputType.json);
        writer.getBuffer().setLength(0);
        json.setWriter(writer);
        battle.unload(mode, json);
        writer.flush();
        return writer.toString();
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
        this.state.execute();
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
            nextState = complete();
        }

        if (stateType == StateType.ANIMATION) {
            this.blockMap = hud.dialogActive();
            if (nextState == StateType.REPLAY)
                completeReplayStep();
        }

        hud.playerInfo.blockEndOfTurn(nextState != StateType.SELECT);

        this.state.leaveFor(nextState);

        this.state = getNextState(nextState);

        StateType tmp = stateType;
        stateType = nextState;

        this.state.enterFrom(tmp);

        if (nextState == StateType.TURN_OVER)
            turnDone();
    }

    private StateType complete()
    {
        switch(stateType) {
            case DEPLOYMENT:
                return completeDeployment();
            case REPLAY:
                return completeReplay();
            default:
                return completeAction();
        }
    }

    private StateType completeDeployment()
    {
        if (battle.isDeploymentDone())
            hud.askEndDeployment();
        battle.actionDone();
        return StateType.DEPLOYMENT;
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

    private StateType completeReplay()
    {
        if (battle.getPlayer().apExhausted()) {
            return StateType.TURN_OVER;
        } else if (!battle.getPlayer().canDoSomething()) {
            return StateType.TURN_OVER;
        } else
            return battle.getState();
    }

    private void completeReplayStep()
    {
        StateType nextState = replayState.execute();

        if (nextState == StateType.DONE) {
            battle.getPlayer().burnDownOneAp();
            hud.update();
        }
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
            case REPLAY:
                state = replayState;
                break;
            default:
                break;
        }

        return state;
    }
}
