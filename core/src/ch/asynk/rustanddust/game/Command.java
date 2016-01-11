package ch.asynk.rustanddust.game;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import ch.asynk.rustanddust.engine.Order;
import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.Pawn;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.util.Collection;

public class Command extends Order
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
        c.ap = player.getAp();
        c.turn = player.getCurrentTurn();
        return c;
    }

    public CommandType type;
    public Player player;
    public int ap;
    public int turn;
    public Unit unit;
    public Unit.UnitId unitId;
    public Unit.UnitType unitType;
    public Tile unitTile;
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
        this.move = move;
        setUnit(unit);
    }

    public void setPromote(Unit unit)
    {
        this.type = CommandType.PROMOTE;
        setUnit(unit);
    }

    public void setEngage(Unit unit, Unit target)
    {
        this.type = CommandType.ENGAGE;
        this.engagement = Engagement.get(unit, target);
        setUnit(unit);
    }

    private void setUnit(Unit unit)
    {
        this.unit = unit;
        this.unitId = unit.id;
        this.unitType = unit.type;
        this.unitTile = unit.getTile();
    }

    @Override
    public void write(Json json)
    {
        json.writeValue("type", type);
        json.writeObjectStart("player");
        json.writeValue("army", player.getName());
        json.writeValue("turn", turn);
        json.writeValue("aps", ap);
        json.writeObjectEnd();
        json.writeObjectStart("unit");
        json.writeValue("id", unitId);
        json.writeValue("type", unitType);
        json.writeValue("ace", unit.ace);
        writeTile(json, "tile", unitTile);
        json.writeObjectEnd();
        if (move != null) writeMove(json, "move", move);
        if (engagement != null) writeEngagement(json, "engagement", engagement);
    }

    private void writeMove(Json json, String key, Move m)
    {
        json.writeObjectStart(key);
        json.writeValue("type", move.type);
        writeTile(json, "from", move.from);
        writeTile(json, "to", move.to);
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
        writeTile(json, "tile", u.getTile());
        json.writeObjectEnd();
    }

    private void writeUnits(Json json, String key, Collection<Unit> units)
    {
        json.writeArrayStart(key);
        for (Unit u : units)
            writeUnit(json, null, u);
        json.writeArrayEnd();
    }

    private void writeTile(Json json, String key, Tile t)
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
            writeTile(json, null, t);
        json.writeArrayEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        // FIXME Command.read(Json, JsonValue);
    }
}
