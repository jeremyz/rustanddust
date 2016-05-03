package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Ctrl.MsgType;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.OrderList;
import ch.asynk.rustanddust.game.Engagement;

public abstract class Map4Orders extends Map3Animations
{
    protected final OrderList orders;
    protected final OrderList replayOrders;

    protected abstract void resolveEngagement(Engagement e);

    public Map4Orders(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);

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

    protected int ordersSize() { return orders.size(); }
    protected void ordersClear() { orders.dispose(); }

    // STATES ENTRY ->

    public void setOnBoard(final Unit unit, Move move)
    {
        setPawnOnto(unit, move);
        addBounceAnimation(unit);
    }

    public void setOnBoard(final Unit unit, Hex to, Orientation o)
    {
        setPawnOnto(unit, to, o);
        addBounceAnimation(unit);
    }

    public boolean enterBoard(final Unit unit, Hex to, Zone entryZone)
    {
        Orientation entry = findBestEntry(unit, to, entryZone.allowedMoves);
        if (entry == Orientation.KEEP)
            return false;
        unit.spendMovementPoints(to.costFrom(unit, entry));
        setOnBoard(unit, to, entry.opposite());
        return true;
    }

    public void revertEnter(final Unit unit)
    {
        removePawn(unit);
        unit.reset();
    }

    public Order getSetOrder(final Unit unit, final Hex to, final Orientation o)
    {
        Order order = getMoveOrder(unit, Move.getSet(unit, to, o), false);
        order.cost = 0;

        return order;
    }

    public Order getRevertSetOrder(Unit unit)
    {
        Order o = orders.get(unit, Order.OrderType.MOVE);
        if (o == null) {
            RustAndDust.error(String.format("can't revert order : %s %s", Order.OrderType.MOVE, unit));
            return null;
        }
        Order order = Order.REVERT;
        order.setRevert(o.id);

        return order;
    }

    public Order getExitOrder(final Unit unit, boolean hqMode)
    {
        Order order = getMoveOrder(unit, paths.getExitMove(), hqMode);

        return order;
    }

    public Order getEnterOrder(final Unit unit, boolean hqMode)
    {
        Order order = getMoveOrder(unit, paths.getEnterMove(unit, getAdjTileAt(unit.getTile(), unit.getOrientation().opposite())), hqMode);

        return order;
    }

    public Order getMoveOrder(final Unit unit, boolean hqMode)
    {
        Order order = getMoveOrder(unit, paths.getMove(), hqMode);

        return order;
    }

    // FIXME revertMoves(...)
    // public void revertMoves()
    // {
    //     for (Unit unit: activatedUnits) {
    //         RustAndDust.debug("    revertMove() " + unit);
    //         revertLastPawnMove(unit, ((Order) orders.get(unit, Order.OrderType.MOVE)).move);
    //         orders.dispose(unit, Order.OrderType.MOVE);
    //     }
    //     activatedUnits.clear();
    // }

    public Order getEngageOrder(final Unit unit, final Unit target)
    {
        attack(unit, target, true);
        Order order = Order.get();
        order.setEngage(unit, target);

        Engagement e = order.engagement;
        resolveEngagement(e);
        order.setActivables(activableUnits);
        if ((e.cost > 0) && order.activables.size() > 0)
            order.cost = 0;

        return order;
    }

    public Order getPromoteOrder(final Unit unit)
    {
        Order order = Order.get();
        order.setPromote(unit);

        return order;
    }

    private Order getMoveOrder(Unit leader, Move move, boolean hqMode)
    {
        Order order = Order.get();
        order.setMove(leader, move);
        activableUnits.remove((Unit) move.pawn);
        activatedUnits.add((Unit) move.pawn);
        order.setActivables(activableUnits);
        if (hqMode && order.activables.size() > 0)
            order.cost = 0;

        return order;
    }

    // STATES ENTRY <-

    // REPLAY ->

    public void prepareReplayLastAction()
    {
        int s = orders.size();
        if (s == 0) return;

        boolean more = true;
        // int a = orders.get(s - 1).id;
        while (more) {
            s -= 1;
            Order o = orders.get(s);
            o.replay = true;
            replayOrders.add(o);
            if (s == 0) {
                more = false;
            } else {
                Order prev = orders.get(s - 1);
                // first order cost=1 part of a group action
                if ((o.cost > 0) && (s > 0) && replayOrders.size() == 1)
                    more = ((prev.cost == 0) && (prev.type == o.type));
                else // part of a group action
                    more = ((o.cost == 0) && (prev.cost == 0) && (prev.type == o.type));
            }
        }
    }

    public void prepareReplayCurrentTurn()
    {
        int s = orders.size();
        while (s > 0) {
            s -= 1;
            Order o = orders.get(s);
            o.replay = true;
            replayOrders.add(o);
        }
    }

    public Order stepReplay()
    {
        int s = replayOrders.size();
        if (s <= 0)
            return null;
        return replayOrders.remove(s - 1);
    }

    // REPLAY <-

    // EXECUTE ->

    public void execute(final Order order)
    {
        RustAndDust.debug("  Order", order.toString());

        switch(order.type)
        {
            case MOVE:      executeMove(order); break;
            case ENGAGE:    executeEngage(order); break;
            case PROMOTE:   executePromote(order); break;
            case END:       executeEnd(order); return;
            case REVERT:    executeRevert(order); return;
            default:
                RustAndDust.error(String.format("Unhandled Order Type %s", order.type));
                break;
        }

        if (order.replay) {
            activableUnits.clear();
            for (Unit u : order.activables)
                activableUnits.add(u);
        } else
            orders.add(order);
    }

    private void executePromote(final Order order)
    {
        addPromoteAnimation(order.leader, game.ctrl.battle.getPlayer());
    }

    private void executeEngage(final Order order)
    {
        Engagement e = order.engagement;

        e.attacker.engage();
        for (Unit u : e.assists)
            u.engage();

        if (order.replay) {
            activatedUnits.clear();
            activatedUnits.add(e.attacker);
            addBounceAnimation(e.attacker);
            for (Unit u : e.assists) {
                activatedUnits.add(u);
                addBounceAnimation(u);
            }
        }

        if (e.success) {
            unclaim(e.defender, e.defender.getHex());
            removePawn(e.defender);
            addDestroyAnimation(e.defender);
        }
        addEngagementAnimation(e.defender);
    }

    private void executeMove(final Order order)
    {
        final Unit leader = order.leader;
        final Unit unit = (Unit) order.move.pawn;
        final Move move = order.move;

        switch(move.type)
        {
            case REGULAR:
                playMoveSound(unit);
                moveUnit(unit, move, this, order.replay);
                break;
            case SET:
                claim(unit, move.to);
                if (!order.replay)
                    orders.dispose(unit, Order.OrderType.MOVE);
                setOnBoard(unit, move);
                break;
            case ENTER:
                claim(unit, move.from);
                if (order.replay)
                    unit.setOnTile(move.from, Orientation.NORTH.r());
                playMoveSound(unit);
                moveUnit(unit, move, this, order.replay);
                break;
            case EXIT:
                playMoveSound(unit);
                moveUnit(unit, move, this, order.replay);
                break;
            default:
                RustAndDust.error(String.format("Unhandled Move Type %s", order.move.type));
        }
    }

    private void executeRevert(Order order)
    {
        int id = order.id;
        order = orders.getId(id);
        orders.remove(order);

        if (order.type != Order.OrderType.MOVE) {
            RustAndDust.error(String.format("Unhandled Revert for Order Type %s", order.type));
            return;
        }

        Unit unit = (Unit) order.move.pawn;
        switch(order.move.type)
        {
            case SET:
                removePawn(unit);
                unclaim(unit, order.move.to);
                game.ctrl.sendMsg(MsgType.UNIT_UNDEPLOYED, unit);
                break;
            case REGULAR:
            case EXIT:
            case ENTER:
            default:
                RustAndDust.error(String.format("Unhandled Move Type %s", order.move.type));
        }
        order.dispose();
    }

    private void executeEnd(Order order)
    {
        orders.get(orders.size() - 1).cost = 1;
    }
}
