package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.OrderList;
import ch.asynk.rustanddust.game.Engagement;

public abstract class Map4Orders extends Map3Animations
{
    protected final OrderList orders;
    protected final OrderList replayOrders;

    protected int actionId;
    protected abstract int engagementCost(Engagement e);
    protected abstract void resolveEngagement(Engagement e);

    public Map4Orders(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);

        this.actionId = 0;
        this.orders = new OrderList(10);
        this.replayOrders = new OrderList(10);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        orders.dispose();
        Order.clearPool();
        Engagement.clearPool();
    }

    protected void incActionId() { actionId += 1; }
    protected int ordersSize() { return orders.size(); }
    protected void ordersClear() { orders.dispose(); }

    // STATES ENTRY ->

    public void showOnBoard(final Unit unit, Hex to, Orientation o)
    {
        setPawnOnto(unit, to, o);
    }

    public boolean setOnBoard(final Unit unit, Hex to, Orientation entry)
    {
        orders.dispose(unit);
        return process(getMoveOrder(unit, Move.getSet(unit, to, entry)));
    }

    public boolean enterBoard(final Unit unit, Hex to, int allowedMoves)
    {
        Orientation entry = findBestEntry(unit, to, allowedMoves);
        if (entry == Orientation.KEEP)
            return false;

        return process(getMoveOrder(unit, Move.getEnter(unit, to, entry)));
    }

    public boolean exitBoard(final Unit unit)
    {
        return process(getMoveOrder(unit, paths.getExitMove()));
    }

    public boolean moveUnit(final Unit unit)
    {
        return process(getMoveOrder(unit, paths.getMove()));
    }

    public void revertMoves()
    {
        for (Unit unit: activatedUnits) {
            RustAndDust.debug("    revertMove() " + unit);
            revertLastPawnMove(unit, ((Order) orders.get(unit, Order.OrderType.MOVE)).move);
            orders.dispose(unit, Order.OrderType.MOVE);
        }
        activatedUnits.clear();
    }

    public void revertEnter(final Unit unit)
    {
        RustAndDust.debug("    revertEnter() "+ unit);

        revertclaim(unit, unit.getHex());
        removePawn(unit);
        game.ctrl.battle.getPlayer().revertUnitEntry(unit);
        orders.dispose(unit);
        unit.reset();
    }

    public boolean engageUnit(final Unit unit, final Unit target)
    {
        attack(unit, target, true);

        Order order = Order.get();
        order.setEngage(unit, target);
        process(order);
        return order.engagement.success;
    }

    public boolean promoteUnit(final Unit unit)
    {
        Order order = Order.get();
        order.setPromote(unit);
        return process(order);
    }

    // STATES ENTRY <-

    // REPLAY ->

    public void prepareReplayLastAction()
    {
        int s = orders.size();
        int a = orders.get(s - 1).actionId;
        while (s > 0) {
            s -= 1;
            Order o = orders.get(s);
            if (o.actionId != a)
                break;
            replayOrders.add(o);
        }
    }

    public void prepareReplayLastTurn()
    {
        int s = orders.size();
        while (s > 0) {
            s -= 1;
            replayOrders.add(orders.get(s));
        }
    }

    public Order stepReplay()
    {
        int s = replayOrders.size();
        if (s <= 0)
            return null;
        return replayOrders.remove(s - 1);
    }

    public boolean replay(Order order)
    {
        return process(order, true);
    }

    // REPLAY <-

    private Order getMoveOrder(Unit unit, Move move)
    {
        Order order = Order.get();
        order.setMove(unit, move);
        return order;
    }

    private boolean process(Order order)
    {
        return process(order, false);
    }

    private boolean process(Order order, boolean replay)
    {
        RustAndDust.debug("Order", order.toString());

        boolean r = false;

        switch(order.type) {
            case MOVE:
                r = doMove(order.unit, order.move, replay);
                break;
            case PROMOTE:
                r = doPromote(order.unit, replay);
                break;
            case ENGAGE:
                r = doEngagement(order.engagement, replay);
                break;
            default:
                RustAndDust.error(String.format("Unhandled Order Type %s", order.type));
                break;
        }

        if (r && !replay) {
            order.cost = ((activatedUnits.size() > 0) ? ((activableUnits.size() > 0) ? 0 : 1) : 0);
            order.actionId = actionId;
            order.setActivable(activableUnits);
            orders.add(order);
            game.ctrl.orderProcessedCb();
        }

        if (replay) {
            activableUnits.clear();
            for (Unit u : order.activable)
                activableUnits.add(u);
            actionId = order.actionId;
        }

        return r;
    }

    private boolean doMove(Unit unit, Move move, boolean replay)
    {
        RustAndDust.debug("  Move", String.format("%s %s", move.type, move.toString()));

        switch(move.type) {
            case REGULAR:
                initMove(unit);
                movePawn(unit, move, this);
                break;
            case EXIT:
                initMove(unit);
                movePawn(unit, move, this);
                game.ctrl.battle.getPlayer().unitWithdraw(unit);
                break;
            case SET:
                setPawnOnto(unit, move);
                game.ctrl.battle.getPlayer().unitEntry(unit);
                claim(unit, move.to);
                addBounceAnimation(unit, 0.3f);
                break;
            case ENTER:
                enterPawn(unit, move);
                game.ctrl.battle.getPlayer().unitEntry(unit);
                claim(unit, move.to);
                addBounceAnimation(unit, 0.3f);
                break;
            default:
                RustAndDust.error(String.format("Unhandled Move Type %s", move.type));
                return false;
        }

        return true;
    }

    private void initMove(Unit unit)
    {
        activableUnits.remove(unit);
        activatedUnits.add(unit);
        playMoveSound(unit);
    }

    private boolean doPromote(final Unit unit, boolean replay)
    {
        activableUnits.remove(unit);
        activatedUnits.add(unit);
        addPromoteAnimation(unit, game.ctrl.battle.getPlayer(), new Runnable() {
            @Override
            public void run() {
                game.ctrl.battle.getPlayer().promote(unit);
            }
        });
        return true;
    }

    private boolean doEngagement(Engagement e, boolean replay)
    {
        if (replay) {
            activatedUnits.clear();
            for (Unit u : e.assists) {
                u.engage();
                activatedUnits.add(u);
            }
            e.attacker.engage();
            activatedUnits.add(e.attacker);
        } else {
            resolveEngagement(e);
            activableUnits.clear();
            for (Unit u : activatedUnits) {
                u.engage();
                if (u.canBreak())
                    activableUnits.add(u);
            }
        }

        if (e.success) {
            unclaim(e.defender, e.defender.getHex());
            removePawn(e.defender);
            addDestroyAnimation(e.defender);
        }

        if (!replay)
            game.ctrl.hud.engagementSummary(e);
        addEngagementAnimation(e.defender);

        if (engagementCost(e) == 0)
            activatedUnits.clear();

        return true;
    }

}
