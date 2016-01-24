package ch.asynk.rustanddust.game.battles;

import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit.UnitId;
import ch.asynk.rustanddust.game.Factory;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.engine.Orientation;

public class BattleTest extends BattleCommon
{
    private Zone usExit;

    public BattleTest(Factory factory)
    {
        super(factory);
        _id = 0;
        name = "* Alpha Test *";
        description = "This is a fast test purpose only battlefield.";
        mapType = Factory.MapType.MAP_00;
    }

    @Override
    public Position getHudPosition()
    {
        return (currentPlayer.is(Army.US) ? Position.TOP_RIGHT: Position.TOP_LEFT);
    }

    @Override
    public Player getWinner()
    {
        return getWinner(4);
    }

    @Override
    protected Player getFirstPlayer()
    {
        return gePlayer;
    }

    @Override
    public boolean hasReinforcement()
    {
        if (currentPlayer.is(Army.GE))
            return false;
        if (currentPlayer.getCurrentTurn() != 2)
            return false;

        Zone usEntry = new Zone(map, 1);
        usEntry.allowedMoves = (Orientation.SOUTH.s | Orientation.SOUTH_EAST.s | Orientation.SOUTH_WEST.s);
        usEntry.add(map.getHex(12, 6));
        addEntryZone(usEntry);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_WOLVERINE);

        return true;
    }

    @Override
    protected void setupMap()
    {
        map.addObjective(5, 2, Army.NONE);
        map.addHoldObjective(5, 3, Army.NONE);
        map.addObjective(3, 4, Army.NONE);
        map.addHoldObjective(3, 3, Army.NONE);
    }

    @Override
    protected void setupGE()
    {
        setUnit(map, gePlayer, UnitId.GE_WESPE, 6, 8, Orientation.NORTH, null);
        setUnit(map, gePlayer, UnitId.GE_TIGER, 5, 2, Orientation.NORTH, null);
        setUnit(map, gePlayer, UnitId.GE_PANZER_IV, 4, 5, Orientation.NORTH_WEST, null);
        setUnit(map, gePlayer, UnitId.GE_INFANTRY, 1, 2, Orientation.NORTH_WEST, null);
        setUnit(map, gePlayer, UnitId.GE_KINGTIGER, 1, 1, Orientation.NORTH_WEST, null);

        Zone geEntry = new Zone(map, 6);
        geEntry.orientation = Orientation.NORTH;
        geEntry.add(1, 2);
        geEntry.add(1, 1);
        geEntry.add(3, 3);
        geEntry.add(3, 4);
        addEntryZone(geEntry);
        addReinforcement(gePlayer, geEntry, UnitId.GE_AT_GUN);
    }

    @Override
    protected void setupUS()
    {
        usExit = new Zone(map, 9);
        usExit.orientation = Orientation.NORTH;
        usExit.add(11, 4);
        usExit.add(11, 5);
        usExit.add(12, 6);
        addExitZone(usExit);

        usPlayer.casualty(factory.getUnit(UnitId.US_SHERMAN, true, false));
        setUnit(map, usPlayer, UnitId.US_PRIEST, 7, 6, Orientation.SOUTH_EAST, usExit);
        setUnit(map, usPlayer, UnitId.US_SHERMAN, 8, 4, Orientation.SOUTH, false, true, usExit);
        setUnit(map, usPlayer, UnitId.US_SHERMAN, 7, 3, Orientation.SOUTH, true, false, usExit);
        setUnit(map, usPlayer, UnitId.US_WOLVERINE, 11, 7, Orientation.SOUTH_EAST, usExit);
        setUnit(map, usPlayer, UnitId.US_PERSHING, 6, 5, Orientation.SOUTH, usExit);
        setUnit(map, usPlayer, UnitId.US_INFANTRY, 5, 3, Orientation.NORTH_EAST, usExit);
        setUnit(map, usPlayer, UnitId.US_AT_GUN, 6, 1, Orientation.SOUTH, usExit);
    }
}
