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
import ch.asynk.tankontank.game.hud.Position;
import ch.asynk.tankontank.engine.Orientation;

public class BattleTest extends BattleCommon
{
    public BattleTest(Factory factory)
    {
        super(factory);
        name = "Engagement Test";
    }

    @Override
    public Map getMap()
    {
        return factory.getMap(Factory.MapType.MAP_B);
    }

    @Override
    public Player getPlayer()
    {
        return usPlayer;
    }

    @Override
    public Position getHudPosition(Player player)
    {
        return (player.is(Army.US) ? Position.TOP_RIGHT: Position.TOP_LEFT);
    }

    @Override
    public boolean deploymentDone(Player player)
    {
        return true;
    }

    @Override
    public Player checkVictory(Ctrl ctrl)
    {
        return null;
    }

    private void setUnit(Map map, Player player, UnitId unitId, int col, int row, Orientation orientation)
    {
        Unit u = factory.getUnit(unitId);
        player.addUnit(u);
        map.setPawnOnto(u, map.getHex(col, row), orientation);
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        setUnit(map, gePlayer, UnitId.GE_TIGER, 6, 4, Orientation.NORTH);

        setUnit(map, usPlayer, UnitId.US_PRIEST, 10, 8, Orientation.SOUTH_EAST);
        setUnit(map, usPlayer, UnitId.US_SHERMAN, 7, 3, Orientation.SOUTH);
        setUnit(map, usPlayer, UnitId.US_SHERMAN_HQ, 8, 4, Orientation.SOUTH);
        setUnit(map, usPlayer, UnitId.US_WOLVERINE, 9, 7, Orientation.SOUTH_EAST);
        setUnit(map, usPlayer, UnitId.US_SHERMAN, 6, 6, Orientation.NORTH_EAST);
        setUnit(map, usPlayer, UnitId.US_INFANTRY, 5, 3, Orientation.NORTH_WEST);
    }
}
