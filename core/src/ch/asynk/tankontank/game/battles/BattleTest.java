package ch.asynk.tankontank.game.battles;

import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.HexSet;
import ch.asynk.tankontank.game.Zone;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Unit.UnitId;
import ch.asynk.tankontank.ui.Position;
import ch.asynk.tankontank.engine.Orientation;

public class BattleTest extends BattleCommon
{
    private Zone usExit;

    public BattleTest(Factory factory)
    {
        super(factory);
        name = "*** Test ***";
        mapType = Factory.MapType.MAP_B;
    }

    @Override
    public Player getPlayer()
    {
        if (!gePlayer.isDeploymentDone())
            return gePlayer;

        if (gePlayer.getTurnDone() == usPlayer.getTurnDone())
            return usPlayer;
        return gePlayer;
    }

    @Override
    public Position getHudPosition(Player player)
    {
        return (player.is(Army.US) ? Position.TOP_RIGHT: Position.TOP_LEFT);
    }

    @Override
    public Player checkVictory(Ctrl ctrl)
    {
        if (usPlayer.getTurnDone() > 2)
                return usPlayer;
        return null;
    }

    @Override
    public boolean getReinforcement(Ctrl ctrl, Map map)
    {
        if (ctrl.player.is(Army.GE))
            return false;
        if (ctrl.player.getCurrentTurn() != 2)
            return false;

        Zone usEntry = new Zone(map, 1);
        usEntry.allowedMoves = (Orientation.SOUTH.s | Orientation.SOUTH_EAST.s | Orientation.SOUTH_WEST.s);
        usEntry.add(map.getHex(12, 6));
        addEntryZone(usEntry);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_WOLVERINE);

        return true;
    }

    private Unit setUnit(Map map, Player player, UnitId unitId, int col, int row, Orientation orientation, Zone exitZone)
    {
        return setUnit(map, player, unitId, col, row, orientation, false, exitZone);
    }

    private Unit setUnit(Map map, Player player, UnitId unitId, int col, int row, Orientation orientation, boolean ace, Zone exitZone)
    {
        Unit u = factory.getUnit(unitId);
        u.setAce(ace);
        if (exitZone != null)
            unitExit.put(u, exitZone);
        map.setOnBoard(u, map.getHex(col, row), orientation);
        return u;
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        map.addObjective(6, 4, Army.NONE);
        map.addHoldObjective(5, 3, Army.NONE);
        map.addObjective(3, 4, Army.NONE);
        map.addHoldObjective(3, 3, Army.NONE);

        ctrl.player = gePlayer;
        setUnit(map, gePlayer, UnitId.GE_WESPE, 5, 8, Orientation.NORTH, null);
        setUnit(map, gePlayer, UnitId.GE_TIGER, 6, 4, Orientation.NORTH, null);
        setUnit(map, gePlayer, UnitId.GE_PANZER_IV, 4, 5, Orientation.NORTH_WEST, null);
        setUnit(map, gePlayer, UnitId.GE_INFANTRY, 1, 2, Orientation.NORTH_WEST, null);
        setUnit(map, gePlayer, UnitId.GE_KINGTIGER, 1, 1, Orientation.NORTH_WEST, null);
        Zone geEntry = new Zone(map, 6);
        geEntry.orientation = Orientation.NORTH;
        geEntry.add(map.getHex(1, 2));
        geEntry.add(map.getHex(1, 1));
        geEntry.add(map.getHex(3, 3));
        geEntry.add(map.getHex(3, 4));
        geEntry.add(map.getHex(4, 0));
        geEntry.add(map.getHex(5, 0));
        addEntryZone(geEntry);
        addReinforcement(gePlayer, geEntry, UnitId.GE_AT_GUN);

        usExit = new Zone(map, 9);
        usExit.orientation = Orientation.NORTH;
        usExit.add(map.getHex(11, 4));
        usExit.add(map.getHex(11, 5));
        usExit.add(map.getHex(12, 6));
        addExitZone(usExit);

        ctrl.player = usPlayer;
        usPlayer.casualty(factory.getUnit(UnitId.US_SHERMAN_HQ));
        setUnit(map, usPlayer, UnitId.US_PRIEST, 10, 8, Orientation.SOUTH_EAST, usExit);
        setUnit(map, usPlayer, UnitId.US_SHERMAN, 7, 3, Orientation.SOUTH, true, usExit);
        setUnit(map, usPlayer, UnitId.US_SHERMAN_HQ, 8, 4, Orientation.SOUTH, usExit);
        setUnit(map, usPlayer, UnitId.US_WOLVERINE, 9, 7, Orientation.SOUTH_EAST, usExit);
        setUnit(map, usPlayer, UnitId.US_PERSHING, 6, 6, Orientation.NORTH_EAST, usExit);
        setUnit(map, usPlayer, UnitId.US_INFANTRY, 5, 3, Orientation.NORTH_WEST, usExit);
        setUnit(map, usPlayer, UnitId.US_AT_GUN, 10, 3, Orientation.SOUTH, usExit);
        usPlayer.turnEnd();
        map.init();
        map.turnDone();
    }
}
