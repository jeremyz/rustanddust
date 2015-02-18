package ch.asynk.tankontank.engine;

import java.lang.Comparable;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Json;

public abstract class Order implements Disposable, Pool.Poolable, Json.Serializable, Comparable<Pawn>
{
    public interface OrderType
    {
    }

    public abstract boolean isA(OrderType type);
}
