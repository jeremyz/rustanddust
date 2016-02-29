package ch.asynk.rustanddust.game.battles;

import java.util.Random;

import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Battle;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.State;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Unit.UnitCode;
import ch.asynk.rustanddust.game.Factory;
import ch.asynk.rustanddust.game.State.StateType;
import ch.asynk.rustanddust.engine.Orientation;

public abstract class BattleCommon implements Battle
{
    protected final static Random random = new Random(System.currentTimeMillis());

    protected final Factory factory;

    protected int _id;
    protected Factory.MapType mapType;
    protected String name;
    protected String description;
    protected Map map;
    protected Player currentPlayer;
    protected Player[] players;

    protected abstract Player getWinner();
    protected abstract void setupMap();
    protected abstract void setupPlayer();
    protected abstract void setPlayers(int idA, int idB);

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

    @Override
    public String unload(boolean full)
    {
        return map.unload(full, getPlayer(), getOpponent());
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
    public Map getMap()
    {
        return map;
    }

    @Override
    public Factory.MapType getMapType()
    {
        return mapType;
    }

    @Override
    public void init(Ctrl ctrl, int idA, int idB)
    {
        ctrl.map = this.map = factory.getMap(getMapType());
        setPlayers(idA, idB);

        setupMap();

        this.currentPlayer = players[0];
        setupPlayer();
        currentPlayer.turnEnd();

        this.currentPlayer = players[1];
        setupPlayer();
        currentPlayer.turnEnd();

        this.currentPlayer = players[0];
        map.turnDone();
    }

    @Override
    public void init(Ctrl ctrl, String payload)
    {
        ctrl.map = this.map = factory.getMap(getMapType());
        setupMap();
        map.load(payload, players);
        currentPlayer = players[0];
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
        map.turnDone();
        return ret;
    }

    protected boolean turnDoneForBoth()
    {
        return ((currentPlayer.getTurn() > 0) && (currentPlayer.getTurn() == getOpponent().getTurn()));
    }

    protected Player getWinner(int minTurns)
    {
        if (!turnDoneForBoth())
            return null;

        Player a = players[0];
        Player b = players[1];

        if (a.unitsLeft() == 0)
            return b;
        if (b.unitsLeft() == 0)
            return a;

        if (a.getTurn() <= minTurns)
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
    public boolean hasReinforcement()
    {
        return false;
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
        exit.enable(Hex.EXIT, true);
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

    protected Unit setUnit(Map map, Player player, UnitCode unitCode, int col, int row, Orientation orientation, Zone exitZone)
    {
        return setUnit(map, player, unitCode, col, row, orientation, false, false, exitZone);
    }

    protected Unit setUnit(Map map, Player player, UnitCode unitCode, int col, int row, Orientation orientation, boolean hq, boolean ace, Zone exitZone)
    {
        Unit unit = factory.getUnit(unitCode, hq, ace);
        if (exitZone != null) unit.exitZone = exitZone;
        map.setOnBoard(unit, map.getHex(col, row), orientation);
        return unit;
    }
}
