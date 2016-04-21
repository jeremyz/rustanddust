package ch.asynk.rustanddust.game;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.engine.Move;

public class Order implements Disposable, Pool.Poolable, Comparable<Unit>
{
    public static int orderId = 1;

    public static final Order END = new Order(OrderType.END);

    public enum OrderType
    {
        NONE,
        MOVE,
        ENGAGE,
        PROMOTE,
        END,
    }

    private static final Pool<Order> orderPool = new Pool<Order>()
    {
        @Override
        protected Order newObject() {
            return new Order();
        }
    };

    public static void clearPool()
    {
        orderPool.clear();
    }

    public static Order get()
    {
        Order o = orderPool.obtain();
        o.id = orderId;
        orderId += 1;
        return o;
    }

    public int id;
    public int cost;
    public boolean replay;
    public Unit leader;
    public OrderType type;
    public Move move;
    public Engagement engagement;
    public UnitList activables = new UnitList(4);

    private Order()
    {
        reset();
    }

    private Order(OrderType type)
    {
        this();
        this.type = type;
    }

    @Override
    public void dispose()
    {
        orderPool.free(this);
    }

    @Override
    public void reset()
    {
        this.id = -1;
        this.cost = 1;
        this.replay = false;
        this.leader = null;
        this.type = OrderType.NONE;
        this.activables.clear();
        if (this.move != null) {
            this.move.dispose();
            this.move = null;
        }
        if (this.engagement != null) {
            this.engagement.dispose();
            this.engagement = null;
        }
    }

    @Override
    public int compareTo(Unit other)
    {
        if (leader == other)
            return 0;
        return 1;
    }

    public boolean isA(OrderType type)
    {
        return (type == this.type);
    }

    @Override
    public String toString()
    {
        if (type == OrderType.END)
            return String.format("[00] END");
        else
            return String.format("[%d] %s(%d) : %s", id, type, cost, leader.code);
    }

    public void setMove(Unit unit, Move move)
    {
        this.leader = unit;
        this.type = OrderType.MOVE;
        this.move = move;
    }

    public void setPromote(Unit unit)
    {
        this.leader = unit;
        this.type = OrderType.PROMOTE;
    }

    public void setEngage(Unit unit, Unit target)
    {
        this.leader = unit;
        this.type = OrderType.ENGAGE;
        this.engagement = Engagement.get(unit, target);
    }

    public void setActivables(UnitList l)
    {
        for(Unit u : l)
            activables.add(u);
    }
}
