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
import ch.asynk.rustanddust.game.states.StatePromote;
import ch.asynk.rustanddust.game.states.StateEngage;
import ch.asynk.rustanddust.game.states.StateAnimation;
import ch.asynk.rustanddust.game.states.StateDeployment;
import ch.asynk.rustanddust.game.states.StateReinforcement;
import ch.asynk.rustanddust.game.states.StateReplay;

public abstract class Ctrl implements Disposable
{
    enum Mode
    {
        LOADING,
        REPLAY,
        PLAY,
    }

    private static final boolean debugCtrl = false;

    public enum MsgType
    {
        OK,
        CANCEL,
        PROMOTE,
        ANIMATIONS_DONE,
        UNIT_DOCK_SELECT,
        UNIT_DOCK_TOGGLE,
        UNIT_DEPLOYED,
        UNIT_UNDEPLOYED,
    }

    public enum EventType
    {
        ORDER,
        ORDER_DONE,
        STATE_CHANGE,
        ANIMATION,
        REPLAY_DONE,
        TURN_DONE,
        ACTION_ABORTED,
        EXIT_BATTLE,
    }

    class Event
    {
        public EventType type;
        public Object data;
        @Override
        public String toString() { return String.format("Event : %s - %s", type, (data == null) ? "" : data); }
    }

    public final RustAndDust game;
    public final Battle battle;

    private final StringWriter writer = new StringWriter(2048);
    private final IterableQueue<Event> events = new IterableQueue<Event>(4);
    private final IterableStack<Event> freeEvents = new IterableStack<Event>(4);

    public Map map;
    public Hud hud;
    private float blockEvents;
    public boolean blockMap;
    public boolean blockHud;
    private Hex touchedHex;
    protected int gameId;
    protected boolean synched;
    private int depth;

    private Order lastOrder;

    private final State selectState;
    private final State moveState;
    private final State promoteState;
    private final State engageState;
    private final State animationState;
    private final State deploymentState;
    private final State reinforcementState;
    private final State replayState;

    private Mode mode;
    private State state;
    private StateType stateType;
    private StateType stateAfterAnimation;

    public abstract void init();

    public static Ctrl getCtrl(final RustAndDust game)
    {
        Ctrl ctrl = null;
        switch(game.config.gameMode)
        {
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

        this.blockEvents = 0.5f;
        this.blockMap = true;
        this.blockHud = false;
        this.touchedHex = null;
        this.gameId = -1;
        this.synched = false;
        this.depth = 0;

        this.lastOrder = null;

        this.selectState = new StateSelect();
        this.moveState = new StateMove();
        this.promoteState = new StatePromote();
        this.engageState = new StateEngage();
        this.animationState = new StateAnimation();
        this.deploymentState = new StateDeployment();
        this.reinforcementState = new StateReinforcement();
        this.replayState = new StateReplay();

        this.mode = Mode.LOADING;

        this.map = battle.init(this);
        init();
        StateCommon.set(game);
        hud.update();

        this.stateType = StateType.WAIT_EVENT;
        this.stateAfterAnimation = battle.getState();

        setState(StateType.ANIMATION);

        switch(game.config.loadMode)
        {
            case NEW:
                this.hud.notify(battle.toString(), 2, Position.MIDDLE_CENTER, false);
                break;
            case RESUME:
                if (!synched) {
                    map.prepareReplayLastAction();
                    this.stateAfterAnimation = StateType.REPLAY;
                }
                break;
            case REPLAY_CURRENT:
                map.prepareReplayCurrentTurn();
                this.stateAfterAnimation = StateType.REPLAY;
                break;
            case REPLAY_ALL:
                // TODO REPLAY ALL
                this.stateAfterAnimation = StateType.REPLAY;
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

    // JSON

    protected void load(Marshal.Mode mode, String payload)
    {
        if (payload == null) return;
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

    // DB
    private void storeState()
    {
        game.db.storeGameState(gameId, battle.getTurnCount(), battle.getPlayer().id, unload(Marshal.Mode.PLAYERS), unload(Marshal.Mode.MAP));
    }

    private void storeOrders()
    {
        game.db.storeGameOrders(gameId, battle.getTurnCount(), battle.getPlayer().id, unload(Marshal.Mode.ORDERS));
    }

    private void clearOrders()
    {
        game.db.clearGameOrders(gameId);
    }

    private void storeTurn()
    {
        game.db.storeCurrentTurn(gameId);
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
        if (!blockHud && hud.hit(hudX, hudY, inAnimation()))
            return;

        touchedHex = (blockMap ? null : map.getHexAt(mapX, mapY));
    }

    public void touchUp(float hudX, float hudY, float mapX, float mapY)
    {
        if (!blockMap && (touchedHex != null) && (touchedHex == map.getHexAt(mapX, mapY)))
            state.touch(touchedHex);
    }

    // MESSAGES

    public void sendMsg(MsgType msgType)
    {
        sendMsg(msgType, null);
    }

    public void sendMsg(MsgType msgType, Object data)
    {
        RustAndDust.debug(String.format("Msg : %s %s", msgType, data));
        switch(msgType)
        {
            case ANIMATIONS_DONE:   animationsDone(); break;
            case UNIT_DOCK_TOGGLE:  unitDockToggle(); break;
            case UNIT_DEPLOYED:     deploymentState.processMsg(msgType, data); break;
            default:
                if (!this.state.processMsg(msgType, data))
                    RustAndDust.error(String.format("%s does not handle msg : %s %s", this.state, msgType, data));
                break;
        }
    }

    // EVENTS

    public void postTurnDone() { postEvent(EventType.TURN_DONE); }
    public void postActionAborted() { postEvent(EventType.ACTION_ABORTED); }
    public void postReplayDone(StateType stateType) { postEvent(EventType.REPLAY_DONE, stateType); }

    public void post(StateType stateType)
    {
        postEvent(EventType.STATE_CHANGE, stateType);
    }

    public void postOrder(Order order)
    {
        postOrder(order, null);
    }

    public void postInitOrder(Order order)
    {
        // postEvent(EventType.ORDER, order);
        // postEvent(EventType.ORDER_DONE, stateType);
        executeOrder(order);
        orderDone(null);
    }

    public void postOrder(Order order, StateType stateType)
    {
        postEvent(EventType.ORDER, order);
        switch(order.type)
        {
            case END:
            case REVERT:
                break;
            default:
                postEvent(EventType.ANIMATION, StateType.WAIT_EVENT);
        }
        postEvent(EventType.ORDER_DONE, stateType);
    }

    public void postEvent(EventType type)
    {
        postEvent(type, null);
    }

    public void postEvent(EventType type, Object data)
    {
        Event evt = freeEvents.pop();
        if (evt == null)
            evt = new Event();
        evt.type = type;
        evt.data = data;
        events.enqueue(evt);
    }

    public void processEvent(float delta)
    {
        if (blockEvents > 0f) {
            blockEvents -= delta;
            if (blockEvents > 0f)
                return;
        }
        if ((events.size() <= 0) || inAnimation())
            return;

        Event evt = events.dequeue();
        RustAndDust.debug(evt.toString());

        switch(evt.type)
        {
            case ORDER:             executeOrder((Order) evt.data); break;
            case ORDER_DONE:        orderDone((StateType) evt.data); break;
            case STATE_CHANGE:      setState((StateType) evt.data); break;
            case REPLAY_DONE:       replayDone((StateType) evt.data); break;
            case TURN_DONE:         turnDone(); break;
            case ACTION_ABORTED:    abortAction(); break;
            case EXIT_BATTLE:       exitBattle(); break;
            case ANIMATION:
                stateAfterAnimation = (StateType) evt.data;
                setState(StateType.ANIMATION);
                break;
            default:
                RustAndDust.error(String.format("Unhandled Event Type : %s %s", evt.type, evt.data));
        }
        freeEvents.push(evt);
    }

    private boolean inAnimation()
    {
        return (this.stateType == StateType.ANIMATION);
    }

    private void animationsDone()
    {
        if (debugCtrl) RustAndDust.debug("    ANIMATIONS DONE");

        if (hud.dialogActive())
            hud.notifyAnimationsDone();

        if (mode == Mode.LOADING) {
            this.mode = ((stateAfterAnimation == StateType.REPLAY) ? Mode.REPLAY : Mode.PLAY);
            if (game.config.loadMode == Config.LoadMode.NEW) {
                storeState();
                storeTurn();
            }
            if (mode == Mode.PLAY)
                map.clear(true);
        }
        this.blockMap = false;
        StateType tmp = stateAfterAnimation;
        stateAfterAnimation = StateType.WAIT_EVENT;
        setState(tmp);
    }

    private void replayDone(StateType nextState)
    {
        if (debugCtrl) RustAndDust.debug("    REPLAY DONE");
        hud.notify("Replay Done", Position.MIDDLE_CENTER);
        this.mode = Mode.PLAY;
        if (nextState != null) {
            setState(nextState);
        } else {
            if (!synched) {
                storeState();
                synched = true;
            }
            if (battle.getPlayer().apExhausted())
                postTurnDone();
            else if (!battle.getPlayer().canDoSomething())
                postTurnDone();
            else
                setState(battle.getState());
        }
    }

    private void executeOrder(Order order)
    {
        if (debugCtrl) RustAndDust.debug("    EXECUTE ORDER");
        lastOrder = order;
        map.execute(order);
        if ((order.type == Order.OrderType.ENGAGE) && !order.replay) {
            game.ctrl.hud.engagementSummary(order.engagement);
        }
        if (this.mode == Mode.PLAY)
            storeOrders();
        hud.update();
    }

    private void orderDone(StateType nextState)
    {
        if (debugCtrl) RustAndDust.debug("    ORDER DONE -> " + nextState);
        Order order = this.lastOrder;
        this.lastOrder = null;
        if (nextState == null)
            nextState = battle.getState();

        completeOrder(order);

        if (mode == Mode.LOADING)
            return;

        if (mode == Mode.REPLAY) {
            if (order.cost > 0)
                battle.getPlayer().burnDownOneAp();
            hud.update();
            blockEvents = 0.2f;
            post(nextState);
            return;
        }

        if (order.cost == 0) {
            post(nextState);
            return;
        }

        battle.getPlayer().burnDownOneAp();
        hud.notify("1 Action Point burnt");
        hud.update();

        if (battle.getPlayer().apExhausted()) {
            hud.notify("No more Action Points");
            postTurnDone();
        } else if (!battle.getPlayer().canDoSomething()) {
            hud.notify("No available Actions");
            postTurnDone();
        } else {
            post(nextState);
        }

        storeState();
    }

    private void completeOrder(Order order)
    {
        switch(order.type)
        {
            case MOVE:      completeMoveOrder(order, (Unit) order.move.pawn); break;
            case ENGAGE:    completeEngagementOrder(order, order.engagement.defender); break;
            case PROMOTE:   battle.getPlayer().promote(order.leader); break;
            case REVERT:    break;
            case END:       break;
            default:        break;
        }
    }

    private void completeEngagementOrder(Order order, Unit unit)
    {
        if (order.engagement.success) {
            battle.getPlayer().engagementWon += 1;
            battle.getOpponent().casualty(unit);
        } else {
            battle.getPlayer().engagementLost += 1;
        }
    }

    private void completeMoveOrder(Order order, Unit unit)
    {
        switch(order.move.type)
        {
            case EXIT:      battle.getPlayer().unitWithdraw(unit); break;
            case SET:       battle.getPlayer().unitEntry(unit); break;
            case ENTER:     battle.getPlayer().unitEntry(unit); break;
            case REGULAR:   break;
            default:        break;
        }
    }

    private void turnDone()
    {
        if (debugCtrl) RustAndDust.debug("    TURN DONE");

        setState(StateType.WAIT_EVENT);

        if (battle.turnDone())
            hud.victory(battle.getPlayer(), battle.getOpponent());
        else {
            hud.update();
            if (battle.getPlayer().hasReinforcement())
                hud.notify("You have reinforcement", 2, Position.MIDDLE_CENTER, true);
            if (!battle.getPlayer().canDoSomething()) {
                hud.notify("No available Actions");
                postTurnDone();
            } else {
                post(battle.getState());
            }
        }

        storeState();
        storeTurn();
        map.clear(true);
        clearOrders();
    }

    private void abortAction()
    {
        if (debugCtrl) RustAndDust.debug("    ABORT ACTION");
        post(battle.getState());
    }

    private void exitBattle()
    {
        if (debugCtrl) RustAndDust.debug("    EXIT BATTLE");
        game.switchToMenu();
    }

    private void unitDockToggle()
    {
        if (this.stateType == StateType.SELECT)
            post(StateType.REINFORCEMENT);
        else if (this.stateType == StateType.REINFORCEMENT) {
            sendMsg(MsgType.OK);
            post(StateType.SELECT);
        }
    }

    //

    private void setState(StateType nextState)
    {
        if (stateType == nextState)
            RustAndDust.error("***!!!*** STATE LOOP ********************************************************************** " + stateType);

        if (nextState == StateType.WAIT_EVENT) {
            stateType = nextState;
            if (debugCtrl) RustAndDust.debug("    WAIT_EVENT");
            return;
        }

        depth += 1;
        if (depth > 1)
            RustAndDust.error(String.format("***!!!*** STATE DEPTH : %d", depth));

        if (nextState == StateType.DEPLOYMENT) {
            if (battle.isDeploymentDone())
                hud.askEndDeployment();
        }

        hud.playerInfo.blockEndOfTurn(nextState != StateType.SELECT);

        this.state = getNextState(nextState);
        StateType tmp = stateType;
        stateType = nextState;
        this.state.enterFrom(tmp);

        depth -= 1;
    }

    private State getNextState(StateType nextState)
    {
        RustAndDust.debug("  State Change", String.format("%s -> %s", stateType, nextState));

        switch(nextState)
        {
            case SELECT:        return selectState;
            case MOVE:          return moveState;
            case PROMOTE:       return promoteState;
            case ENGAGE:        return engageState;
            case ANIMATION:     return animationState;
            case DEPLOYMENT:    return deploymentState;
            case REINFORCEMENT: return reinforcementState;
            case REPLAY:        return replayState;
            default:
                RustAndDust.error(String.format("Unhandled State : %s", nextState));
        }

        return this.state;
    }
}
