package ch.asynk.rustanddust.game.battles;

import java.util.Random;
import java.util.HashMap;

import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Battle;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Unit.UnitId;
import ch.asynk.rustanddust.game.Factory;
import ch.asynk.rustanddust.game.State.StateType;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.util.ArrayListIt;

public abstract class BattleCommon implements Battle
{
    protected final static Random random = new Random(System.currentTimeMillis());

    protected Factory.MapType mapType;
    protected String name;
    protected String description;
    protected Factory factory;
    protected Map map;
    protected Player currentPlayer;
    protected Player usPlayer;
    protected Player gePlayer;
    protected ArrayListIt<Zone> entryZone = new ArrayListIt<Zone>();
    protected ArrayListIt<Zone> exitZone = new ArrayListIt<Zone>();
    protected HashMap<Unit, Zone> unitEntry = new HashMap<Unit, Zone>();
    protected HashMap<Unit, Zone> unitExit = new HashMap<Unit, Zone>();

    public abstract Player getWinner();

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
    public boolean actionDone()
    {
        boolean burn = (map.unitsActivatedSize() > 0);
        if (burn)
            currentPlayer.burnDownOneAp();
        map.actionDone();
        return burn;
    }

    protected boolean abTurnDone()
    {
        return ((currentPlayer.getTurnDone() > 0) && (currentPlayer.getTurnDone() == getOpponent().getTurnDone()));
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

    public Player getNextPlayer()
    {
        return getOpponent();
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
        entryZone.add(entry);
    }

    protected void addExitZone(Zone exit)
    {
        exitZone.add(exit);
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
