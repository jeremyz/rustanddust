package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import ch.asynk.tankontank.engine.Order;
import ch.asynk.tankontank.engine.Move;

public class Command extends Order
{
    public enum CommandType
    {
        NONE,
        MOVE,
        ENGAGE,
        PROMOTE,
        END_OF_TURN;
    }

    private static final Pool<Command> orderPool = new Pool<Command>()
    {
        @Override
        protected Command newObject() {
            return new Command();
        }
    };

    public static Command get(Player player)
    {
        Command c = orderPool.obtain();
        c.player = player;
        return c;
    }

    public CommandType type;
    public Player player;
    public Unit unit;
    public Move move;

    private Command()
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
        this.type = CommandType.NONE;
        this.player = null;
        this.unit = null;
        if (this.move != null) {
            this.move.dispose();
            this.move = null;
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s %s : ", type, unit.id);
    }

    public void setMove(Unit unit, Move move)
    {
        this.type = CommandType.MOVE;
        this.unit = unit;
        this.move = move;
    }

    @Override
    public void write(Json json)
    {
        // FIXME Command.write(Json);
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        // FIXME Command.read(Json, JsonValue);
    }
}
