package ch.asynk.tankontank.game.battles;

import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

import ch.asynk.tankontank.engine.TileSet;
import ch.asynk.tankontank.engine.EntryPoint;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Battle;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Unit.UnitId;

public abstract class BattleCommon implements Battle
{
    protected final static Random random = new Random();

    protected String name;
    protected String description;
    protected Factory factory;
    protected Player usPlayer;
    protected Player gePlayer;
    protected ArrayList<EntryPoint> entryPoints = new ArrayList<EntryPoint>();
    protected ArrayList<EntryPoint> exitPoints = new ArrayList<EntryPoint>();
    protected HashMap<Unit, EntryPoint> pawnEntry = new HashMap<Unit, EntryPoint>();
    protected TileSet objectives;

    public BattleCommon(Factory factory)
    {
        this.factory = factory;
        this.usPlayer = factory.getPlayer(Army.US);
        this.gePlayer = factory.getPlayer(Army.GE);
    }

    public String toString()
    {
        return getName();
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public EntryPoint getEntryPoint(Unit unit)
    {
        return pawnEntry.get(unit);
    }

    public void addEntryPoint(EntryPoint entry)
    {
        entryPoints.add(entry);
    }

    public void addExitPoint(EntryPoint exit)
    {
        exitPoints.add(exit);
    }

    public void addReinforcement(Player player, EntryPoint entryPoint, UnitId unitId)
    {
        addReinforcement(player, entryPoint, unitId, false);
    }

    public void addReinforcement(Player player, EntryPoint entryPoint, UnitId unitId, boolean ace)
    {
        Unit unit = factory.getUnit(unitId);
        unit.setAce(ace);
        player.addReinforcement(unit);
        pawnEntry.put(unit, entryPoint);
    }
}
