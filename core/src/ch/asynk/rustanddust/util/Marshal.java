package ch.asynk.rustanddust.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public interface Marshal
{
    public enum Mode
    {
        FULL,
        STATE,
        ORDERS
    }

    public void unload(Mode mode, Json json);

    public void load(Mode mode, JsonValue value);
}
