package ch.asynk.tankontank.engine;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Json;

public abstract class Order implements Disposable, Pool.Poolable, Json.Serializable
{
}