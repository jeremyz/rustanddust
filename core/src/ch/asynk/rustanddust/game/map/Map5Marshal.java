package ch.asynk.rustanddust.game.map;

import java.io.StringWriter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Path;
import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.SelectedTile;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Battle;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.OrderList;
import ch.asynk.rustanddust.game.Engagement;

public abstract class Map5Marshal extends Map4Orders
{
    private static StringWriter writer = new StringWriter(2048);
    private static UnitList units = new UnitList(30);

    public Map5Marshal(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);
    }

    public String unload(Player player, Player opponent)
    {
        Json json = new Json(OutputType.json);
        writer.getBuffer().setLength(0);
        json.setWriter(writer);

        json.writeObjectStart();
        json.writeArrayStart("players");
        unload(json, player);
        unload(json, opponent);
        json.writeArrayEnd();
        json.writeObjectStart("map");
        unload(json);
        json.writeObjectEnd();
        json.writeArrayStart("orders");
        unload(json, orders);
        json.writeArrayEnd();
        json.writeObjectEnd();

        writer.flush();
        return writer.toString();
    }

    // player
    private void unload(Json json, Player player)
    {
        json.writeObjectStart();
        json.writeValue("id", player.getId());
        json.writeValue("a", player.army);
        json.writeArrayStart("v");
        json.writeValue(player.getTurn());
        json.writeValue(player.getAp());
        json.writeValue(player.getApSpent());
        json.writeValue(player.actionCount);
        json.writeValue(player.objectivesWon);
        json.writeValue(player.engagementWon);
        json.writeValue(player.engagementLost);
        json.writeArrayEnd();
        unload(json, "Us", player.units, true);
        unload(json, "Cs", player.casualties, false);
        unload(json, "Rs", player.reinforcement, false);
        unload(json, "Ws", player.withdrawed, false);
        json.writeObjectEnd();
    }

    // units
    private void unload(Json json, String name, UnitList units, boolean pos)
    {
        json.writeArrayStart(name);
        for (Unit u : units) unload(json, u, pos);
        json.writeArrayEnd();
    }

    // unit
    private void unload(Json json, Unit unit, boolean pos)
    {
        json.writeObjectStart();
        json.writeValue("id", unit.id);
        json.writeValue("code", unit.code);
        json.writeArrayStart("v");
        json.writeValue(unit.hq);
        json.writeValue(unit.ace);
        json.writeValue(unit.hasMoved);
        json.writeValue(unit.hasFired);
        json.writeArrayEnd();
        if (pos) {
            Hex h = unit.getHex();
            json.writeArrayStart("p");
            json.writeValue(h.getCol());
            json.writeValue(h.getRow());
            json.writeValue(unit.getRotation());
            json.writeArrayEnd();
        }
        json.writeObjectEnd();
    }

    // map
    private void unload(Json json)
    {
        json.writeArrayStart("o");
        for (Hex h : objectives) {
            json.writeObjectStart();
            json.writeValue("army", h.belongsTo());
            json.writeArrayStart("p");
            json.writeValue(h.getCol());
            json.writeValue(h.getRow());
            json.writeArrayEnd();
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    // orders
    private void unload(Json json, OrderList orders)
    {
        for (Order o : orders) {
            json.writeObjectStart();
            json.writeValue("type", o.type);
            switch(o.type) {
                case MOVE:
                    unload(json, o.move);
                    break;
                case ENGAGE:
                    unload(json, o.engagement);
                    break;
                case PROMOTE:
                    json.writeValue("id", o.unit.id);
                    break;
            }
            json.writeObjectEnd();
        }
    }

    // move
    private void unload(Json json, Move m)
    {
        json.writeValue("type", m.type);
        json.writeValue("id", ((Unit) m.pawn).id);
        if (m.from != null) {
            json.writeArrayStart("from");
            json.writeValue(m.from.getCol());
            json.writeValue(m.from.getRow());
            json.writeArrayEnd();
        }
        if (m.to != null) {
            json.writeArrayStart("to");
            json.writeValue(m.to.getCol());
            json.writeValue(m.to.getRow());
            json.writeValue(m.orientation.r());
            json.writeArrayEnd();
        }
        if (m.tiles.size() > 0) {
            json.writeArrayStart("path");
            for (Tile t : m.tiles) {
                json.writeArrayStart();
                json.writeValue(t.getCol());
                json.writeValue(t.getRow());
                json.writeArrayEnd();
            }
            json.writeArrayEnd();
        }
    }

    // engagement
    private void unload(Json json, Engagement e)
    {
        json.writeArrayStart("units");
        json.writeValue(e.attacker.id);
        json.writeValue(e.defender.id);
        json.writeArrayEnd();
        json.writeArrayStart("assists");
        for (Unit u : e.assists)
            json.writeValue(u.id);
        json.writeArrayEnd();
        json.writeArrayStart("dice");
        json.writeValue(e.d1);
        json.writeValue(e.d2);
        json.writeValue(e.d3);
        json.writeValue(e.d4);
        json.writeArrayEnd();
        json.writeArrayStart("vals");
        json.writeValue(e.success);
        json.writeValue(e.attackSum);
        json.writeValue(e.defenseSum);
        json.writeValue(e.unitCount);
        json.writeValue(e.flankBonus);
        json.writeValue(e.unitDefense);
        json.writeValue(e.terrainDefense);
        json.writeValue(e.weatherDefense);
        json.writeArrayEnd();
    }

    private void unload(Json json, String key, Unit u)
    {
        unload(json, key, u, true);
    }

    // unit
    private void unload(Json json, String key, Unit u, boolean pos)
    {
        if (key != null) json.writeObjectStart(key);
        else json.writeObjectStart();
        json.writeValue("id", u.id);
        Hex h = u.getHex();
        if (pos && (h != null)) {
            json.writeArrayStart("p");
            json.writeValue(h.getCol());
            json.writeValue(h.getRow());
            json.writeArrayEnd();
        }
        json.writeObjectEnd();
    }

    // LOAD
    public void load(String payload, Player[] players)
    {
        units.clear();
        JsonValue root = new JsonReader().parse(payload);
        players[0] = loadPlayer(root.get("players").get(0));
        players[1] = loadPlayer(root.get("players").get(1));
        loadMap(root.get("map"));
        loadOrders(root.get("orders"));
        units.clear();
    }

    private Player loadPlayer(JsonValue v)
    {
        Player p = new Player(v.getInt("id"), Army.valueOf(v.getString("a")));
        JsonValue a = v.get("v");
        p.setTurn(a.getInt(0), a.getInt(1), a.getInt(2));
        p.actionCount = a.getInt(3);
        p.objectivesWon = a.getInt(4);
        p.engagementWon = a.getInt(5);
        p.engagementLost = a.getInt(6);

        a = v.get("Us");
        for (int i = 0; i < a.size; i++)
            p.units.add(loadUnit(a.get(i), true));
        a = v.get("Cs");
        for (int i = 0; i < a.size; i++)
            p.casualties.add(loadUnit(a.get(i), false));
        a = v.get("Rs");
        for (int i = 0; i < a.size; i++)
            p.reinforcement.add(loadUnit(a.get(i), false));
        a = v.get("Ws");
        for (int i = 0; i < a.size; i++)
            p.withdrawed.add(loadUnit(a.get(i), false));

        return p;
    }

    private Unit loadUnit(JsonValue v, boolean pos)
    {
        int unitId = v.getInt("id");
        Unit.UnitCode code = Unit.UnitCode.valueOf(v.getString("code"));
        JsonValue a = v.get("v");
        Unit u = game.factory.getUnit(code, a.getBoolean(0), a.getBoolean(1));
        if (a.getBoolean(2)) u.setMoved();
        if (a.getBoolean(3)) u.setFired();
        u.id = unitId;
        if (pos) {
            a = v.get("p");
            Hex h = getHex(a.getInt(0), a.getInt(1));
            u.setRotation(a.getInt(2));
            showOnBoard(u, h, Orientation.fromRotation(a.getInt(2)));
        }
        units.add(u);
        return u;
    }

    private void loadMap(JsonValue v)
    {
        JsonValue a = v.get("o");
        for (int i = 0; i < a.size; i++) {
            JsonValue o = a.get(i);
            Hex h = getHex(o.get("p").getInt(0), o.get("p").getInt(1));
            Army army = Army.valueOf(o.getString("army"));
            h.claim(army);
            showObjective(h, army);
        }
    }

    private void loadOrders(JsonValue v)
    {
        for (int i = 0; i < v.size; i++) {
            JsonValue o = v.get(i);
            Order order = null;
            switch(Order.OrderType.valueOf(o.getString("type"))) {
                case MOVE:
                    order = loadMoveOrder(o);
                    break;
                case ENGAGE:
                    order = loadEngageOrder(o);
                    break;
                case PROMOTE:
                    order = loadPromoteOrder(o);
                    break;
            }
            orders.add(order);
        }
    }

    private Order loadMoveOrder(JsonValue v)
    {
        Unit unit = findById(v.getInt("id"));
        Hex from = loadHex(v, "from");
        Hex to = loadHex(v, "to");
        Orientation orientation = Orientation.fromRotation(v.get("to").getInt(2));
        if (unit == null) return null;

        Path path = null;
        JsonValue p = v.get("path");
        if (p != null) {
            path = Path.get(p.size);
            for (int i = 0; i < p.size; i++) {
                JsonValue h = p.get(i);
                path.tiles.add(getHex(h.getInt(0), h.getInt(1)));
            }
        }

        Move move = Move.get(unit, from, to, orientation, path);
        Order o = Order.get();
        o.setMove(unit, move);
        o.unitHex = from;
        return o;
    }

    private Order loadEngageOrder(JsonValue v)
    {
        Order o = Order.get();
        JsonValue a = v.get("units");
        o.setEngage(findById(a.getInt(0)), findById(a.getInt(1)));

        a = v.get("dice");
        o.engagement.d1 = a.getInt(0);
        o.engagement.d2 = a.getInt(1);
        o.engagement.d3 = a.getInt(2);
        o.engagement.d4 = a.getInt(3);

        a = v.get("vals");
        o.engagement.success = a.getBoolean(0);
        o.engagement.attackSum = a.getInt(1);
        o.engagement.defenseSum = a.getInt(2);
        o.engagement.unitCount = a.getInt(3);
        o.engagement.flankBonus = a.getInt(4);
        o.engagement.unitDefense = a.getInt(5);
        o.engagement.terrainDefense = a.getInt(6);
        o.engagement.weatherDefense = a.getInt(7);

        a = v.get("assists");
        for (int i = 0; i < a.size; i++)
            o.engagement.assists.add(findById(a.getInt(i)));

        return o;
    }

    private Order loadPromoteOrder(JsonValue v)
    {
        Unit unit = findById(v.getInt("id"));
        if (unit == null) return null;

        Order o = Order.get();
        o.setPromote(unit);
        return o;
    }

    private Hex loadHex(JsonValue v, String key)
    {
        JsonValue p = v.get(key);
        if (p == null) return null;
        return getHex(p.getInt(0), p.getInt(1));
    }

    private static Unit findById(int id)
    {
        for (Unit u : units) {
            if (u.id == id)
                return u;
        }
        RustAndDust.error(String.format("loadPromoteOrder: unable to find unit %d", id));
        return null;
    }
}
