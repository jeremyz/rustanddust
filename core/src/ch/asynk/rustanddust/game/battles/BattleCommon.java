package ch.asynk.rustanddust.game.battles;

import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Battle;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Unit.UnitId;
import ch.asynk.rustanddust.game.State.StateType;

public abstract class BattleCommon implements Battle
{
    protected final static Random random = new Random();

    protected Factory.MapType mapType;
    protected String name;
    protected String description;
    protected Factory factory;
    protected Map map;
    protected Player currentPlayer;
    protected Player usPlayer;
    protected Player gePlayer;
    protected ArrayList<Zone> entryZone = new ArrayList<Zone>();
    protected ArrayList<Zone> exitZone = new ArrayList<Zone>();
    protected HashMap<Unit, Zone> unitEntry = new HashMap<Unit, Zone>();
    protected HashMap<Unit, Zone> unitExit = new HashMap<Unit, Zone>();

    public BattleCommon(Factory factory)
    {
        this.factory = factory;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public Factory.MapType getMapType()
    {
        return mapType;
    }

    @Override
    public Map setup()
    {
        this.map = factory.getMap(mapType);
        this.usPlayer = factory.getPlayer(Army.US);
        this.gePlayer = factory.getPlayer(Army.GE);

        return this.map;
    }

    @Override
    public boolean turnDone()
    {
        map.turnDone();
        currentPlayer.turnEnd();
        Player winner = getVictor();
        if (winner != null) {
            currentPlayer = winner;
            return true;
        } else {
            currentPlayer = getOpponent();
            currentPlayer.turnStart();
            return false;
        }
    }

    @Override
    public Player getPlayer()
    {
        return currentPlayer;
    }

    @Override
    public Player getOpponent()
    {
        return ((currentPlayer == usPlayer) ? gePlayer : usPlayer);
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

    @Override
    public boolean hasReinforcement()
    {
        return false;
    }

    @Override
    public Zone getEntryZone(Unit unit)
    {
        return unitEntry.get(unit);
    }

    @Override
    public Zone getExitZone(Unit unit)
    {
        return unitExit.get(unit);
    }

    public void addEntryZone(Zone entry)
    {
        entryZone.add(entry);
    }

    public void addExitZone(Zone exit)
    {
        exitZone.add(exit);
        exit.enable(Hex.EXIT, true);
    }

    public void addReinforcement(Player player, Zone entryZone, UnitId unitId)
    {
        addReinforcement(player, entryZone, unitId, false);
    }

    public void addReinforcement(Player player, Zone entryZone, Zone exitZone, UnitId unitId)
    {
        addReinforcement(player, entryZone, exitZone, unitId, false);
    }

    public void addReinforcement(Player player, Zone entryZone, UnitId unitId, boolean ace)
    {
        addReinforcement(player, entryZone, null, unitId, ace);
    }

    public void addReinforcement(Player player, Zone entryZone, Zone exitZone, UnitId unitId, boolean ace)
    {
        Unit unit = factory.getUnit(unitId);
        unit.setAce(ace);
        player.addReinforcement(unit);
        unitEntry.put(unit, entryZone);
        if (exitZone != null)
            unitExit.put(unit, exitZone);
    }
}
