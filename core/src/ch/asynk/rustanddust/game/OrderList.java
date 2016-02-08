package ch.asynk.rustanddust.game;

import java.util.Iterator;

import ch.asynk.rustanddust.engine.util.IterableArray;

public class OrderList extends IterableArray<Order>
{
    private Player player;

    public OrderList(int capacity)
    {
        super(capacity);
    }

    public Order get(Unit unit, Order.OrderType type)
    {
        for (Order o : this) {
            if ((o.compareTo(unit) == 0) && (o.isA(type)))
                return o;
        }
        return null;
    }

    public void dispose(Unit unit)
    {
        Iterator<Order> it = iterator();
        while (it.hasNext()) {
            Order order = it.next();
            if (order.compareTo(unit) == 0) {
                it.remove();
                order.dispose();
            }
        }
    }

    public void dispose(Unit unit, Order.OrderType type)
    {
        Iterator<Order> it = iterator();
        while (it.hasNext()) {
            Order order = it.next();
            if ((order.compareTo(unit) == 0) && (order.isA(type))) {
                it.remove();
                order.dispose();
            }
        }
    }

    public void dispose()
    {
        for (Order o : this)
            o.dispose();
        clear();
    }
}
