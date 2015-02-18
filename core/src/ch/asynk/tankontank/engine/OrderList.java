package ch.asynk.tankontank.engine;

import java.util.LinkedList;
import java.util.Iterator;

public class OrderList extends LinkedList<Order>
{
    public void dispose(Pawn pawn)
    {
        Iterator<Order> it = iterator();
        while(it.hasNext()) {
            Order order = it.next();
            if (order.compareTo(pawn) == 0) {
                it.remove();
                order.dispose();
            }
        }
    }

    public void dispose(Pawn pawn, Order.OrderType type)
    {
        Iterator<Order> it = iterator();
        while(it.hasNext()) {
            Order order = it.next();
            if ((order.compareTo(pawn) == 0) && (order.isA(type))) {
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
