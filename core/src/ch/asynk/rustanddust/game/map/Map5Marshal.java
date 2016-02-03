package ch.asynk.rustanddust.game.map;

import java.io.StringWriter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.SelectedTile;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Battle;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.OrderList;
import ch.asynk.rustanddust.game.Engagement;

public abstract class Map5Marshal extends Map4Orders
{
    public Map5Marshal(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);
    }

    public String unload(Player player, Player opponent)
    {
        Json json = new Json(OutputType.json);
        StringWriter buffer = new StringWriter(2048);
        json.setWriter(buffer);

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

        buffer.flush();
        return buffer.toString();
    }

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

    private void unload(Json json, String name, UnitList units, boolean pos)
    {
        json.writeArrayStart(name);
        for (Unit u : units) unload(json, u, pos);
        json.writeArrayEnd();
    }

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
                    json.writeValue("code", o.unit.code);
                    json.writeArrayStart("p");
                    json.writeValue(o.unitHex.getCol());
                    json.writeValue(o.unitHex.getRow());
                    json.writeArrayEnd();
                    break;
            }
            json.writeObjectEnd();
        }
    }

    private void unload(Json json, Move m)
    {
        json.writeValue("type", m.type);
        json.writeValue("id", ((Unit) m.pawn).id);
        json.writeValue("code", ((Unit) m.pawn).code);
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

    private void unload(Json json, Engagement e)
    {
        unload(json, "atk", e.attacker);
        unload(json, "def", e.defender);
        json.writeArrayStart("assists");
        for (Unit u : e.assists)
            unload(json, null, u);
        json.writeArrayEnd();
        json.writeArrayStart("dice");
        json.writeValue(e.d1);
        json.writeValue(e.d2);
        json.writeValue(e.d3);
        json.writeValue(e.d4);
        json.writeArrayEnd();
        json.writeArrayStart("res");
        json.writeValue(e.success);
        json.writeValue(e.attackSum);
        json.writeValue(e.defenseSum);
        json.writeArrayEnd();
    }

    private void unload(Json json, String key, Unit u)
    {
        if (key != null) json.writeObjectStart(key);
        else json.writeObjectStart();
        json.writeValue("id", u.id);
        json.writeValue("code", u.code);
        Hex h = u.getHex();
        json.writeArrayStart("p");
        json.writeValue(h.getCol());
        json.writeValue(h.getRow());
        json.writeArrayEnd();
        json.writeObjectEnd();
    }
}
