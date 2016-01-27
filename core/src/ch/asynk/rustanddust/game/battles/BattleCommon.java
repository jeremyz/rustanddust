package ch.asynk.rustanddust.game.battles;

import java.util.Random;
import java.util.HashMap;

import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Battle;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.State;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Unit.UnitId;
import ch.asynk.rustanddust.game.Factory;
import ch.asynk.rustanddust.game.State.StateType;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.util.IterableArray;

public abstract class BattleCommon implements Battle
{
    protected final static Random random = new Random(System.currentTimeMillis());

    protected int _id;
    protected Factory.MapType mapType;
    protected String name;
    protected String description;
    protected Factory factory;
    protected Map map;
    protected Player currentPlayer;
    protected Player usPlayer;
    protected Player gePlayer;
    protected IterableArray<Zone> entryZones = new IterableArray<Zone>(10);
    protected IterableArray<Zone> exitZones = new IterableArray<Zone>(10);
    protected HashMap<Unit, Zone> unitEntry = new HashMap<Unit, Zone>();
    protected HashMap<Unit, Zone> unitExit = new HashMap<Unit, Zone>();

    protected abstract Player getWinner();
    protected abstract void setupMap();
    protected abstract void setupUS();
    protected abstract void setupGE();
    protected abstract Player getFirstPlayer();

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
    }

    @Override
    public int getId()
    {
        return _id;
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
    public void init()
    {
        this.map = ctrl.map;
        this.usPlayer = factory.getPlayer(Army.US);
        this.gePlayer = factory.getPlayer(Army.GE);

        setupMap();

        this.currentPlayer = this.usPlayer;
        setupUS();
        map.actionDone();
        currentPlayer.turnEnd();
        map.turnDone();

        this.currentPlayer = this.gePlayer;
        setupGE();
        map.actionDone();
        currentPlayer.turnEnd();
        map.turnDone();

        this.currentPlayer = getFirstPlayer();
    }

    @Override
    public boolean actionDone()
    {
        boolean burn = (map.unitsActivatedSize() > 0);
        if (burn)
            currentPlayer.burnDownOneAp();
        map.actionDone();
        return burn;
    }

    protected boolean turnDoneForBoth()
    {
        return ((currentPlayer.getTurn() > 0) && (currentPlayer.getTurn() == getOpponent().getTurn()));
    }

    protected Player getWinner(int minTurns)
    {
        if (!turnDoneForBoth())
            return null;

        if (gePlayer.unitsLeft() == 0)
            return usPlayer;
        if (usPlayer.unitsLeft() == 0)
            return gePlayer;

        if (gePlayer.getTurn() <= minTurns)
            return null;

        usPlayer.objectivesWon = map.objectivesCount(Army.US);
        gePlayer.objectivesWon = map.objectivesCount(Army.GE);

        if (usPlayer.objectivesWon > gePlayer.objectivesWon)
            return usPlayer;
        else if (usPlayer.objectivesWon < gePlayer.objectivesWon)
            return gePlayer;

        return null;
    }

    @Override
    public boolean turnDone()
    {
        map.turnDone();
        currentPlayer.turnEnd();
        Player winner = getWinner();
        if (winner != null) {
            currentPlayer = winner;
            return true;
        } else {
            currentPlayer = getNextPlayer();
            currentPlayer.turnStart(getActionPoints());
            return false;
        }
    }

    protected Player getNextPlayer()
    {
        return getOpponent();
    }

    public void setPlayerIds(int a, int b)
    {
        usPlayer._id = a;
        gePlayer._id = b;
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

    protected void addEntryZone(Zone entry)
    {
        entryZones.add(entry);
    }

    protected void addExitZone(Zone exit)
    {
        exitZones.add(exit);
        exit.enable(Hex.EXIT, true);
    }

    protected void addReinforcement(Player player, Zone entryZone, UnitId unitId)
    {
        addReinforcement(player, entryZone, unitId, false, false);
    }

    protected void addReinforcement(Player player, Zone entryZone, Zone exitZone, UnitId unitId)
    {
        addReinforcement(player, entryZone, exitZone, unitId, false, false);
    }

    protected void addReinforcement(Player player, Zone entryZone, UnitId unitId, boolean hq, boolean ace)
    {
        addReinforcement(player, entryZone, null, unitId, hq, ace);
    }

    protected void addReinforcement(Player player, Zone entryZone, Zone exitZone, UnitId unitId, boolean hq, boolean ace)
    {
        Unit unit = factory.getUnit(unitId, hq, ace);
        player.addReinforcement(unit);
        unitEntry.put(unit, entryZone);
        if (exitZone != null)
            unitExit.put(unit, exitZone);
    }

    protected Unit setUnit(Map map, Player player, UnitId unitId, int col, int row, Orientation orientation, Zone exitZone)
    {
        return setUnit(map, player, unitId, col, row, orientation, false, false, exitZone);
    }

    protected Unit setUnit(Map map, Player player, UnitId unitId, int col, int row, Orientation orientation, boolean hq, boolean ace, Zone exitZone)
    {
        Unit u = factory.getUnit(unitId, hq, ace);
        if (exitZone != null)
            unitExit.put(u, exitZone);
        map.setOnBoard(u, map.getHex(col, row), orientation);
        return u;
    }
}
