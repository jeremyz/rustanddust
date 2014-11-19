package ch.asynk.tankontank.game.battles;

import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.State.StateType;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Unit.UnitId;
import ch.asynk.tankontank.game.hud.Position;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.TileSet;
import ch.asynk.tankontank.engine.Zone;
import ch.asynk.tankontank.engine.Orientation;

public class BattleHeadToHead extends BattleCommon
{
    private Army firstArmy;

    public BattleHeadToHead(Factory factory)
    {
        super(factory);
        name = "Head To Head";
        firstArmy = ((random.nextInt(2) == 0) ? Army.US : Army.GE);
    }

    @Override
    public Map getMap()
    {
        return factory.getMap(Factory.MapType.MAP_A);
    }

    @Override
    public Player getPlayer()
    {
        if (gePlayer.getTurnDone() == usPlayer.getTurnDone())
            return ((firstArmy == Army.US) ? usPlayer : gePlayer);
        else
            return ((firstArmy == Army.US) ? gePlayer : usPlayer);
    }

    @Override
    public Position getHudPosition(Player player)
    {
        return (player.is(Army.US) ? Position.TOP_RIGHT: Position.TOP_LEFT);
    }

    @Override
    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if ((ctrl.player.getTurnDone() < 10) || (ctrl.opponent.getTurnDone() < 10))
            return null;

        int player = 0;
        int opponent = 0;

        for (Tile tile : objectives) {
            Unit unit = ((Hex) tile).getUnit();
            if (unit != null) {
                if (ctrl.player.isEnemy(unit))
                    opponent += 1;
                else
                    player += 1;
            }
        }

        if (player > 1)
            return ctrl.player;
        else if (opponent > 1)
            return ctrl.opponent;
        return null;
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        // end deployment
        usPlayer.turnEnd();
        gePlayer.turnEnd();

        // B6, E6, H4
        objectives = new TileSet(map, 3);
        objectives.add(map.getHex(7, 7));
        objectives.add(map.getHex(6, 4));
        objectives.add(map.getHex(6, 1));
        objectives.enable(Hex.OBJECTIVE, true);

        // southern hex row
        Zone geEntry = new Zone(map, 9);
        geEntry.allowedMoves = (Orientation.NORTH.s | Orientation.NORTH_EAST.s | Orientation.NORTH_WEST.s);
        geEntry.add(map.getHex(0, 0));
        geEntry.add(map.getHex(1, 1));
        geEntry.add(map.getHex(1, 2));
        geEntry.add(map.getHex(2, 3));
        geEntry.add(map.getHex(2, 4));
        geEntry.add(map.getHex(3, 5));
        geEntry.add(map.getHex(3, 6));
        geEntry.add(map.getHex(4, 7));
        geEntry.add(map.getHex(4, 8));
        addEntryZone(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);

        // northern hex row
        Zone usEntry = new Zone(map, 9);
        usEntry.allowedMoves = (Orientation.SOUTH.s | Orientation.SOUTH_EAST.s | Orientation.SOUTH_WEST.s);
        usEntry.add(map.getHex(9, 0));
        usEntry.add(map.getHex(9, 1));
        usEntry.add(map.getHex(10, 2));
        usEntry.add(map.getHex(10, 3));
        usEntry.add(map.getHex(11, 4));
        usEntry.add(map.getHex(11, 5));
        usEntry.add(map.getHex(12, 6));
        usEntry.add(map.getHex(12, 7));
        usEntry.add(map.getHex(13, 8));
        addEntryZone(usEntry);

        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_PRIEST);
    }
}
