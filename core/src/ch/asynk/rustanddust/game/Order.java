package ch.asynk.rustanddust.game;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.engine.Move;

public class Order implements Disposable, Pool.Poolable, Comparable<Unit>
{
    public static int orderId = 1;

    public static final Order END = new Order(OrderType.END);
    public static final Order REVERT = new Order(OrderType.REVERT);

    public enum OrderType
    {
        NONE,
        MOVE,
        ENGAGE,
        PROMOTE,
        END,
        REVERT,
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
        if ((type == OrderType.END) || (type == OrderType.REVERT))
            throw new RuntimeException(String.format("call dispose() on a static Order %s", type));
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
        switch (type)
        {
            case END:
                return String.format("[00] END");
            case REVERT:
                return String.format("%s[%d]", type, id);
            default:
                return String.format("[%d] %s(%d) : %s", id, type, cost, leader.code);
        }
    }

    public void setMove(Unit unit, Move move)
    {
        this.type = OrderType.MOVE;
        this.leader = unit;
        this.move = move;
    }

    public void setPromote(Unit unit)
    {
        this.type = OrderType.PROMOTE;
        this.leader = unit;
    }

    public void setEngage(Unit unit, Unit target)
    {
        this.type = OrderType.ENGAGE;
        this.leader = unit;
        this.engagement = Engagement.get(unit, target);
    }

    public void setRevert(int id)
    {
        this.type = OrderType.REVERT;
        this.id = id;
        this.cost = 0;
    }

    public void setActivables(UnitList l)
    {
        for(Unit u : l)
            activables.add(u);
    }
}
