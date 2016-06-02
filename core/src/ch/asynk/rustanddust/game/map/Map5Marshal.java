package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Path;
import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.SelectedTile;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.util.Marshal;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.OrderList;
import ch.asynk.rustanddust.game.Engagement;

public abstract class Map5Marshal extends Map4Orders implements Marshal
{
    public Map5Marshal(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);
    }

    @Override
    public void unload(Marshal.Mode mode, Json json)
    {
        switch(mode)
        {
            case MAP: unloadMap(json); break;
            case ORDERS: unloadOrders(json, orders); break;
        }
    }

    public void unloadPlayers(Json json, Player a, Player b)
    {
        json.writeArrayStart("players");
        unloadPlayer(json, a);
        unloadPlayer(json, b);
        json.writeArrayEnd();
    }

    private void unloadPlayer(Json json, Player player)
    {
        json.writeObjectStart();
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
        unloadUnits(json, "Us", player.units, true);
        unloadUnits(json, "Cs", player.casualties, false);
        unloadUnits(json, "Rs", player.reinforcement, false);
        unloadUnits(json, "Ws", player.withdrawed, false);
        json.writeObjectEnd();
    }

    private void unloadUnits(Json json, String name, UnitList units, boolean pos)
    {
        json.writeArrayStart(name);
        for (Unit u : units) unloadUnit(json, u, pos);
        json.writeArrayEnd();
    }

    private void unloadUnit(Json json, Unit unit, boolean pos)
    {
        json.writeObjectStart();
        json.writeValue("id", unit.id());
        json.writeValue("code", unit.code);
        json.writeArrayStart("v");
        json.writeValue(unit.hq);
        json.writeValue(unit.ace);
        json.writeValue(unit.hasMoved);
        json.writeValue(unit.hasFired);
        json.writeValue(entryZones.indexOf(unit.entryZone));
        json.writeValue(exitZones.indexOf(unit.exitZone));
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

    private void unloadMap(Json json)
    {
        json.writeObjectStart("map");
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
        json.writeArrayStart("e");
        for (Zone z: entryZones) {
            json.writeObjectStart();
            json.writeValue("a", z.allowedMoves);
            json.writeValue("o", z.orientation.r());
            json.writeArrayStart("t");
            for (Tile t : z) {
                json.writeArrayStart();
                json.writeValue(t.getCol());
                json.writeValue(t.getRow());
                json.writeArrayEnd();
            }
            json.writeArrayEnd();
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
        json.writeArrayStart("x");
        for (Zone z: exitZones) {
            json.writeObjectStart();
            json.writeValue("a", z.allowedMoves);
            json.writeValue("o", z.orientation.r());
            json.writeArrayStart("t");
            for (Tile t : z) {
                json.writeArrayStart();
                json.writeValue(t.getCol());
                json.writeValue(t.getRow());
                json.writeArrayEnd();
            }
            json.writeArrayEnd();
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
        json.writeObjectEnd();
    }

    private void unloadOrders(Json json, OrderList orders)
    {
        json.writeArrayStart("orders");
        for (Order o : orders) {
            json.writeObjectStart();
            json.writeValue("id", o.id);
            json.writeValue("type", o.type);
            json.writeValue("cost", o.cost);
            switch(o.type)
            {
                case MOVE:      unloadMoveOrder(json, o.leader, o.move); break;
                case ENGAGE:    unloadEngageOrder(json, o.engagement); break;
                case PROMOTE:   unloadPromoteOrder(json, o.leader); break;
            }
            if (o.activables.size() > 0) {
                json.writeArrayStart("a");
                for(Unit u : o.activables)
                    json.writeValue(u.id());
                json.writeArrayEnd();
            }
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    private void unloadMoveOrder(Json json, Unit leader, Move m)
    {
        json.writeValue("mType", m.type);
        json.writeValue("mCost", m.cost);
        json.writeValue("l", leader.id());
        json.writeValue("u", ((Unit) m.pawn).id());
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

    private void unloadEngageOrder(Json json, Engagement e)
    {
        json.writeArrayStart("u");
        json.writeValue(e.attacker.id());
        json.writeValue(e.defender.id());
        json.writeArrayEnd();
        json.writeArrayStart("us");
        for (Unit u : e.assists)
            json.writeValue(u.id());
        json.writeArrayEnd();
        json.writeArrayStart("d");
        json.writeValue(e.d1);
        json.writeValue(e.d2);
        json.writeValue(e.d3);
        json.writeValue(e.d4);
        json.writeArrayEnd();
        json.writeArrayStart("v");
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

    private void  unloadPromoteOrder(Json json, Unit u)
    {
        json.writeValue("u", u.id());
    }

    private void unloadUnit(Json json, String key, Unit u)
    {
        unloadUnit(json, key, u, true);
    }

    private void unloadUnit(Json json, String key, Unit u, boolean pos)
    {
        if (key != null) json.writeObjectStart(key);
        else json.writeObjectStart();
        json.writeValue("id", u.id());
        Hex h = u.getHex();
        if (pos && (h != null)) {
            json.writeArrayStart("p");
            json.writeValue(h.getCol());
            json.writeValue(h.getRow());
            json.writeArrayEnd();
        }
        json.writeObjectEnd();
    }

    @Override
    public void load(Marshal.Mode mode, JsonValue v)
    {
        switch(mode) {
            case MAP: loadMap(v.get("map")); break;
            case ORDERS: loadOrders(v.get("orders")); break;
        }
    }

    public void loadPlayers(JsonValue v, Player[] players)
    {
        Unit.blockId = true;
        players[0] = loadPlayer(v.get("players").get(0));
        players[1] = loadPlayer(v.get("players").get(1));
        Unit.blockId = false;
    }

    public void loadPlayerAP(JsonValue v, int idx, Player player)
    {
        JsonValue a = v.get("players").get(idx).get("v");
        player.setTurn(a.getInt(0), a.getInt(1), a.getInt(2));
    }

    private Player loadPlayer(JsonValue v)
    {
        Player p = new Player(Army.valueOf(v.getString("a")));
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
        int i = a.getInt(4);
        if (i != -1) u.entryZone = entryZones.get(i);
        i = a.getInt(5);
        if (i != -1) u.exitZone = exitZones.get(i);
        u.id(unitId);
        if (pos) {
            a = v.get("p");
            Hex h = getHex(a.getInt(0), a.getInt(1));
            setOnBoard(u, h, Orientation.fromRotation(a.getInt(2)));
        }
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
        a = v.get("e");
        for (int i = 0; i < a.size; i++)
            addEntryZone(loadZone(a.get(i)));
        a = v.get("x");
        for (int i = 0; i < a.size; i++)
            addExitZone(loadZone(a.get(i)));
    }

    private Zone loadZone(JsonValue v)
    {
        JsonValue t = v.get("t");
        Zone z = new Zone((Map) this, t.size);
        z.allowedMoves = v.getInt("a");
        z.orientation = Orientation.fromRotation(v.getInt("o"));
        for (int j = 0; j < t.size; j++) {
            JsonValue h = t.get(j);
            z.add(getHex(h.getInt(0), h.getInt(1)));
        }
        return z;
    }

    private void loadOrders(JsonValue v)
    {
        for (int i = 0; i < v.size; i++) {
            JsonValue o = v.get(i);
            Order order = null;
            switch(Order.OrderType.valueOf(o.getString("type")))
            {
                case MOVE:      order = loadMoveOrder(o); break;
                case ENGAGE:    order = loadEngageOrder(o); break;
                case PROMOTE:   order = loadPromoteOrder(o); break;
            }
            order.id = o.getInt("id");
            order.cost = o.getInt("cost");
            JsonValue a = o.get("a");
            if (a != null) {
                for (int j = 0; j < a.size; j++) {
                    order.activables.add(findById(a.getInt(j)));
                }
            }
            orders.add(order);
        }
    }

    private Order loadMoveOrder(JsonValue v)
    {
        Unit leader = findById(v.getInt("l"));
        Unit unit = findById(v.getInt("u"));
        if (unit == null) return null;
        Hex from = loadHex(v, "from");
        Hex to = loadHex(v, "to");
        Orientation orientation = Orientation.fromRotation(v.get("to").getInt(2));

        Path path = null;
        JsonValue p = v.get("path");
        if (p != null) {
            path = Path.get(p.size);
            for (int i = 0; i < p.size; i++) {
                JsonValue h = p.get(i);
                path.tiles.add(getHex(h.getInt(0), h.getInt(1)));
            }
        }

        Move m = null;
        switch(Move.MoveType.valueOf(v.getString("mType")))
        {
            case REGULAR:   m = Move.get(unit, from, to, orientation, path); break;
            case ENTER:     m = Move.getEnter(unit, from, to, orientation, path); break;
            case SET:       m = Move.getSet(unit, to, orientation); break;
            case EXIT:
                m = Move.get(unit, from, to, orientation, path);
                m.type = Move.MoveType.EXIT;
                break;
        }
        m.cost = v.getInt("mCost");

        Order o = Order.get();
        o.setMove(leader, m);
        return o;
    }

    private Order loadEngageOrder(JsonValue v)
    {
        Order o = Order.get();
        JsonValue a = v.get("u");
        o.setEngage(findById(a.getInt(0)), findById(a.getInt(1)));

        a = v.get("us");
        for (int i = 0; i < a.size; i++)
            o.engagement.assists.add(findById(a.getInt(i)));

        a = v.get("d");
        o.engagement.d1 = a.getInt(0);
        o.engagement.d2 = a.getInt(1);
        o.engagement.d3 = a.getInt(2);
        o.engagement.d4 = a.getInt(3);

        a = v.get("v");
        o.engagement.success = a.getBoolean(0);
        o.engagement.attackSum = a.getInt(1);
        o.engagement.defenseSum = a.getInt(2);
        o.engagement.unitCount = a.getInt(3);
        o.engagement.flankBonus = a.getInt(4);
        o.engagement.unitDefense = a.getInt(5);
        o.engagement.terrainDefense = a.getInt(6);
        o.engagement.weatherDefense = a.getInt(7);

        return o;
    }

    private Order loadPromoteOrder(JsonValue v)
    {
        Unit unit = findById(v.getInt("u"));
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
        Unit u = Unit.findById(id);
        if (u != null)
            return u;
        RustAndDust.error(String.format("findById: unable to find unit %d", id));
        return null;
    }
}
