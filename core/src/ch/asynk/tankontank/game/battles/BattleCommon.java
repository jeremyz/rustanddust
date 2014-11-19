package ch.asynk.tankontank.game.battles;

import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

import ch.asynk.tankontank.engine.TileSet;
import ch.asynk.tankontank.engine.Zone;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Battle;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Unit.UnitId;
import ch.asynk.tankontank.game.State.StateType;

public abstract class BattleCommon implements Battle
{
    protected final static Random random = new Random();

    protected String name;
    protected String description;
    protected Factory factory;
    protected Player usPlayer;
    protected Player gePlayer;
    protected ArrayList<Zone> entryZone = new ArrayList<Zone>();
    protected ArrayList<Zone> exitZone = new ArrayList<Zone>();
    protected HashMap<Unit, Zone> unitEntry = new HashMap<Unit, Zone>();
    protected HashMap<Unit, Zone> unitExit = new HashMap<Unit, Zone>();
    protected TileSet objectives;

    public BattleCommon(Factory factory)
    {
        this.factory = factory;
        this.usPlayer = factory.getPlayer(Army.US);
        this.gePlayer = factory.getPlayer(Army.GE);
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
    public Player opponent(Player player)
    {
        if (player == usPlayer)
            return gePlayer;
        return usPlayer;
    }

    @Override
    public boolean deploymentDone(Player player)
    {
        return player.isDeploymentDone();
    }

    @Override
    public StateType getState(Player player)
    {
        if (!player.isDeploymentDone())
            return StateType.DEPLOYMENT;
        return StateType.SELECT;
    }

    @Override
    public boolean getReinforcement(Ctrl ctrl, Map map)
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
