package ch.asynk.rustanddust.engine;

import java.util.LinkedList;
import java.util.Iterator;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class OrderList extends LinkedList<Order> implements Json.Serializable
{
    public Order get(Pawn pawn, Order.OrderType type)
    {
        Iterator<Order> it = iterator();
        while (it.hasNext()) {
            Order order = it.next();
            if ((order.compareTo(pawn) == 0) && (order.isA(type)))
                return order;
        }
        return null;
    }

    public void dispose(Pawn pawn)
    {
        Iterator<Order> it = iterator();
        while (it.hasNext()) {
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
        while (it.hasNext()) {
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

    public String toJson()
    {
        Json json = new Json();
        json.setOutputType(OutputType.json);
        return json.toJson(this);
    }

    @Override
    public void write(Json json)
    {
        json.writeArrayStart("commands");
        for (Order o : this)
            json.writeValue(o);
        json.writeArrayEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        // TODO read(Json json, JsonValue jsonMap)
    }
}
