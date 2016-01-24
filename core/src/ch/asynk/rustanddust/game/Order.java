package ch.asynk.rustanddust.game;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.util.Collection;

public class Order implements Disposable, Pool.Poolable, Json.Serializable, Comparable<Unit>
{
    public enum OrderType
    {
        NONE,
        MOVE,
        ENGAGE,
        PROMOTE,
        END_OF_TURN;
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

    public OrderType type;
    public Unit unit;
    public Unit.UnitId unitId;
    public Unit.UnitType unitType;
    public Hex unitHex;
    public Move move;
    public Engagement engagement;

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
        return String.format("%s : %s", type, unit.id);
    }

    public void setMove(Unit unit, Move move)
    {
        this.type = OrderType.MOVE;
        this.move = move;
        setUnit(unit);
    }

    public void setPromote(Unit unit)
    {
        this.type = OrderType.PROMOTE;
        setUnit(unit);
    }

    public void setEngage(Unit unit, Unit target)
    {
        this.type = OrderType.ENGAGE;
        this.engagement = Engagement.get(unit, target);
        setUnit(unit);
    }

    private void setUnit(Unit unit)
    {
        this.unit = unit;
        this.unitId = unit.id;
        this.unitType = unit.type;
        this.unitHex = unit.getHex();
    }

    @Override
    public void write(Json json)
    {
        json.writeValue("type", type);
        json.writeObjectStart("unit");
        json.writeValue("id", unitId);
        json.writeValue("type", unitType);
        json.writeValue("hq", unit.hq);
        json.writeValue("ace", unit.ace);
        writeHex(json, "tile", unitHex);
        json.writeObjectEnd();
        if (move != null) writeMove(json, "move", move);
        if (engagement != null) writeEngagement(json, "engagement", engagement);
    }

    private void writeMove(Json json, String key, Move m)
    {
        json.writeObjectStart(key);
        json.writeValue("type", move.type);
        writeHex(json, "from", (Hex) move.from);
        writeHex(json, "to", (Hex) move.to);
        json.writeValue("orientation", move.orientation.r());
        writeTiles(json, "path", move.tiles);
        json.writeObjectEnd();
    }

    private void writeEngagement(Json json, String key, Engagement e)
    {
        json.writeObjectStart(key);
        writeUnit(json, "attacker", e.attacker);
        writeUnit(json, "defender", e.defender);
        writeUnits(json, "assists", e.assists);
        json.writeObjectStart("dice");
        json.writeValue("d1", e.d1);
        json.writeValue("d2", e.d2);
        json.writeValue("d3", e.d3);
        json.writeValue("d4", e.d4);
        json.writeObjectEnd();
        json.writeObjectStart("results");
        json.writeValue("success", e.success);
        json.writeValue("attackSum", e.attackSum);
        json.writeValue("defenseSum", e.defenseSum);
        json.writeObjectEnd();
        json.writeObjectEnd();
    }

    private void writeUnit(Json json, String key, Unit u)
    {
        if (key != null) json.writeObjectStart(key);
        else json.writeObjectStart();
        json.writeValue("id", u.id);
        json.writeValue("ace", u.ace);
        json.writeValue("army", u.getArmy());
        writeHex(json, "tile", u.getHex());
        json.writeObjectEnd();
    }

    private void writeUnits(Json json, String key, Collection<Unit> units)
    {
        json.writeArrayStart(key);
        for (Unit u : units)
            writeUnit(json, null, u);
        json.writeArrayEnd();
    }

    private void writeHex(Json json, String key, Hex t)
    {
        if (t == null) return;
        if (key != null) json.writeObjectStart(key);
        else json.writeObjectStart();
        json.writeValue("col", t.getCol());
        json.writeValue("row", t.getRow());
        json.writeObjectEnd();
    }

    private void writeTiles(Json json, String key, Collection<Tile> tiles)
    {
        json.writeArrayStart(key);
        for (Tile t : tiles)
            writeHex(json, null, (Hex) t);
        json.writeArrayEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        // FIXME Order.read(Json, JsonValue);
    }
}
