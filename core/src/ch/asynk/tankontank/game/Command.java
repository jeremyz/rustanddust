package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import ch.asynk.tankontank.engine.Order;
import ch.asynk.tankontank.engine.Move;
import ch.asynk.tankontank.engine.Pawn;

public  class Command extends Order
{
    public enum CommandType implements Order.OrderType
    {
        NONE,
        MOVE,
        ENGAGE,
        PROMOTE,
        END_OF_TURN;
    }

    private static final Pool<Command> commandPool = new Pool<Command>()
    {
        @Override
        protected Command newObject() {
            return new Command();
        }
    };

    public static void clearPool()
    {
        commandPool.clear();
    }

    public static Command get(Player player)
    {
        Command c = commandPool.obtain();
        c.player = player;
        return c;
    }

    public CommandType type;
    public Player player;
    public Unit unit;
    public Move move;
    public Engagement engagement;

    private Command()
    {
        reset();
    }

    @Override
    public void dispose()
    {
        commandPool.free(this);
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
        if (this.engagement != null) {
            this.engagement.dispose();
            this.engagement = null;
        }
    }

    @Override
    public int compareTo(Pawn pawn)
    {
        if (pawn == unit)
            return 0;
        return 1;
    }

    @Override
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
        this.type = CommandType.MOVE;
        this.unit = unit;
        this.move = move;
    }

    public void setPromote(Unit unit)
    {
        this.type = CommandType.PROMOTE;
        this.unit = unit;
    }

    public void setEngage(Unit unit, Unit target)
    {
        this.type = CommandType.ENGAGE;
        this.unit = unit;
        this.engagement = Engagement.get(unit, target);
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
