package ch.asynk.rustanddust.game;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.engine.Move;

public class Order implements Disposable, Pool.Poolable, Comparable<Unit>
{
    public enum OrderType
    {
        NONE,
        MOVE,
        ENGAGE,
        PROMOTE;
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
        Order c = orderPool.obtain();
        return c;
    }

    public int id;
    public int cost;
    public Unit unit;
    public OrderType type;
    public Move move;
    public Engagement engagement;
    public UnitList activable = new UnitList(4);

    private Order()
    {
        reset();
    }

    @Override
    public void dispose()
    {
        orderPool.free(this);
    }

    @Override
    public void reset()
    {
        this.type = OrderType.NONE;
        this.unit = null;
        this.activable.clear();
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
        if (unit == other)
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
        return String.format("[%d] %s : %s", id, type, unit.code);
    }

    public void setMove(Unit unit, Move move)
    {
        this.type = OrderType.MOVE;
        this.move = move;
        this.unit = unit;
    }

    public void setPromote(Unit unit)
    {
        this.type = OrderType.PROMOTE;
        this.unit = unit;
    }

    public void setEngage(Unit unit, Unit target)
    {
        this.type = OrderType.ENGAGE;
        this.engagement = Engagement.get(unit, target);
        this.unit = unit;
    }

    public void setActivable(UnitList l)
    {
        for(Unit u : l)
            activable.add(u);
    }
}
