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
    protected Army firstArmy;
    protected Army secondArmy;
    protected Factory factory;
    protected ArrayList<EntryPoint> entryPoints = new ArrayList<EntryPoint>();
    protected HashMap<Unit, EntryPoint> pawnEntry = new HashMap<Unit, EntryPoint>();
    protected TileSet objectives;

    public BattleCommon(Factory factory)
    {
        this.factory = factory;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void randomizeArmies()
    {
        if (random.nextInt(2) == 0) {
            firstArmy = Army.US;
            secondArmy = Army.GE;
        } else {
            firstArmy = Army.GE;
            secondArmy = Army.US;
        }
    }

    public Player getFirstPlayer()
    {
        return factory.getPlayer(firstArmy);
    }

    public Player getSecondPlayer()
    {
        return factory.getPlayer(secondArmy);
    }

    public void addEntryPoint(EntryPoint entry)
    {
        entryPoints.add(entry);
    }

    public void addReinforcement(Player player, EntryPoint entryPoint, UnitId unitId)
    {
        Unit unit = factory.getUnit(unitId);
        player.addReinforcement(unit);
        pawnEntry.put(unit, entryPoint);
    }

    public EntryPoint getEntryPoint(Unit unit)
    {
        return pawnEntry.get(unit);
    }
}
