package ch.asynk.rustanddust.game.battles;

import java.util.Random;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import ch.asynk.rustanddust.util.Marshal;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Battle;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.State;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Unit.UnitCode;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.Factory;
import ch.asynk.rustanddust.game.State.StateType;
import ch.asynk.rustanddust.engine.Orientation;

public abstract class BattleCommon implements Battle
{
    protected final static Random random = new Random(System.currentTimeMillis());

    protected final Factory factory;

    protected int _id;
    protected int turnCount;
    protected Factory.MapType mapType;
    protected String name;
    protected String description;
    protected Map map;
    protected Player currentPlayer;
    protected Player[] players;

    protected abstract Player getWinner();
    protected abstract void setup();
    protected abstract void deployPlayer(final Ctrl ctrl);
    protected abstract void setNextTurn();

    private int d6()
    {
        return random.nextInt(6) + 1;
    }

    protected int getActionPoints()
    {
        int aps = 2;
        if (d6() > 2) {
            aps += 1;
            if (d6() > 3)
                aps += 1;
        }
        return aps;
    }

    public BattleCommon(Factory factory)
    {
        this.factory = factory;
        this.players = new Player[2];
    }

    @Override public int getId()                    { return _id; }
    @Override public String toString()              { return getName(); }
    @Override public String getName()               { return name; }
    @Override public String getDescription()        { return description; }
    @Override public Map getMap()                   { return map; }
    @Override public Factory.MapType getMapType()   { return mapType; }

    @Override
    public Map init()
    {
        this.map = factory.getMap(getMapType());
        setup();
        this.turnCount = 0;
        this.currentPlayer = players[0];
        return this.map;
    }

    @Override
    public void desinit()
    {
        this.map = null;
        this.turnCount = 0;
        this.players[0] = null;
        this.players[1] = null;
        this.currentPlayer = null;
    }

    @Override
    public void initialDeployment(final Ctrl ctrl)
    {
        this.currentPlayer = players[0];
        deployPlayer(ctrl);

        this.currentPlayer = players[1];
        deployPlayer(ctrl);

        this.currentPlayer = players[0];
    }

    public void load(Marshal.Mode mode, JsonValue value)
    {
        if (mode == Marshal.Mode.PLAYER) {
            map.loadPlayerAP(value, 0, currentPlayer);
        } else if (mode == Marshal.Mode.PLAYERS) {
            map.loadPlayers(value, players);
            this.currentPlayer = players[0];
        } else {
            if (mode == Marshal.Mode.MAP) {
                JsonValue v = value.get("battle");
                this.turnCount = v.getInt("turnCount");
                Unit.unitId = v.getInt("unitId");
                Order.orderId = v.getInt("orderId");
            }
            map.load(mode, value);
        }
    }

    @Override
    public void unload(Marshal.Mode mode, Json json)
    {
        json.writeObjectStart();
        if (mode == Marshal.Mode.PLAYERS)
            map.unloadPlayers(json, getPlayer(), getOpponent());
        else {
            if (mode == Marshal.Mode.MAP) {
                json.writeObjectStart("battle");
                json.writeValue("turnCount", this.turnCount);
                json.writeValue("unitId", Unit.unitId);
                json.writeValue("orderId", Order.orderId);
                json.writeObjectEnd();
            }
            map.unload(mode, json);
        }
        json.writeObjectEnd();
    }

    @Override
    public boolean turnDone()
    {
        boolean ret = false;
        currentPlayer.turnEnd();
        Player winner = getWinner();
        if (winner != null) {
            currentPlayer = winner;
            ret = true;
        } else {
            currentPlayer = getOpponent();
            currentPlayer.turnStart(getActionPoints());
        }
        turnCount += 1;
        setNextTurn();
        map.clear();
        return ret;
    }

    protected boolean turnDoneForBoth()
    {
        return ((turnCount > 0) && ((turnCount % 2) == 0));
    }

    protected Player getWinner(int minTurns)
    {
        Player a = players[0];
        Player b = players[1];

        if (a.unitsLeft() == 0)
            return b;
        if (b.unitsLeft() == 0)
            return a;

        if (a.getTurn() <= minTurns)
            return null;

        if (!turnDoneForBoth())
            return null;

        a.objectivesWon = map.objectivesCount(a.army);
        b.objectivesWon = map.objectivesCount(b.army);

        if (a.objectivesWon > b.objectivesWon)
            return a;
        else if (a.objectivesWon < b.objectivesWon)
            return b;

        return null;
    }

    @Override
    public int getTurnCount()
    {
        return turnCount;
    }

    @Override
    public Player getPlayer()
    {
        return currentPlayer;
    }

    @Override
    public Player getOpponent()
    {
        return ((currentPlayer == players[0]) ? players[1] : players[0]);
    }

    @Override
    public boolean isDeploymentDone()
    {
        return currentPlayer.isDeploymentDone();
    }

    @Override
    public StateType getState()
    {
        if (!currentPlayer.isDeploymentDone())
            return StateType.DEPLOYMENT;
        return StateType.SELECT;
    }

    protected void addEntryZone(Zone entry)
    {
        map.addEntryZone(entry);
    }

    protected void addExitZone(Zone exit)
    {
        map.addExitZone(exit);
    }

    protected void addReinforcement(Player player, Zone entryZone, UnitCode unitCode)
    {
        addReinforcement(player, entryZone, unitCode, false, false);
    }

    protected void addReinforcement(Player player, Zone entryZone, Zone exitZone, UnitCode unitCode)
    {
        addReinforcement(player, entryZone, exitZone, unitCode, false, false);
    }

    protected void addReinforcement(Player player, Zone entryZone, UnitCode unitCode, boolean hq, boolean ace)
    {
        addReinforcement(player, entryZone, null, unitCode, hq, ace);
    }

    protected void addReinforcement(Player player, Zone entryZone, Zone exitZone, UnitCode unitCode, boolean hq, boolean ace)
    {
        Unit unit = factory.getUnit(unitCode, hq, ace);
        player.addReinforcement(unit);
        unit.entryZone = entryZone;
        if (exitZone != null) unit.exitZone = exitZone;
    }

    protected Unit setUnit(Ctrl ctrl, Player player, UnitCode unitCode, int col, int row, Orientation orientation, Zone exitZone)
    {
        return setUnit(ctrl, player, unitCode, col, row, orientation, false, false, exitZone);
    }

    protected Unit setUnit(Ctrl ctrl, Player player, UnitCode unitCode, int col, int row, Orientation orientation, boolean hq, boolean ace, Zone exitZone)
    {
        Unit unit = factory.getUnit(unitCode, hq, ace);
        if (exitZone != null) unit.exitZone = exitZone;
        ctrl.postInitOrder(map.getSetOrder(unit, map.getHex(col, row), orientation));
        return unit;
    }
}
