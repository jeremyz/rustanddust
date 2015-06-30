package ch.asynk.creepingarmor.game.battles;

import ch.asynk.creepingarmor.game.Zone;
import ch.asynk.creepingarmor.game.Army;
import ch.asynk.creepingarmor.game.Player;
import ch.asynk.creepingarmor.game.Ctrl;
import ch.asynk.creepingarmor.game.Map;
import ch.asynk.creepingarmor.game.Hex;
import ch.asynk.creepingarmor.game.HexSet;
import ch.asynk.creepingarmor.game.Unit;
import ch.asynk.creepingarmor.game.Unit.UnitId;
import ch.asynk.creepingarmor.ui.Position;
import ch.asynk.creepingarmor.engine.Orientation;
import ch.asynk.creepingarmor.engine.Meteorology;

public class BattleNightAction extends BattleCommon
{
    public BattleNightAction(Factory factory)
    {
        super(factory);
        name = "Night Action";
        mapType = Factory.MapType.MAP_B;
    }

    @Override
    public Position getHudPosition(Player player)
    {
        return (player.is(Army.US) ? Position.TOP_RIGHT: Position.TOP_LEFT);
    }

    @Override
    public Player getPlayer()
    {
        if (!gePlayer.isDeploymentDone() || gePlayer.getCurrentTurn() == 1)
            return gePlayer;
        if (gePlayer.getTurnDone() > usPlayer.getTurnDone())
            return usPlayer;
        return gePlayer;
    }

    private boolean isClear(Map map, int col, int row)
    {
        Hex hex = map.getHex(col, row);
        Unit unit = hex.getUnit();
        if ((unit != null) && unit.is(Army.GE)) {
            map.selectHex(hex);
            return false;
        }
            map.showMove(hex);
        return true;
    }

    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if ((ctrl.player.getTurnDone() < 9) || (ctrl.opponent.getTurnDone() < 9))
            return null;

        Map map = ctrl.map;
        boolean clear = true;
        clear &= isClear(map, 4, 8);
        clear &= isClear(map, 5, 8);
        clear &= isClear(map, 6, 8);
        clear &= isClear(map, 7, 8);
        clear &= isClear(map, 8, 8);
        clear &= isClear(map, 8, 7);
        clear &= isClear(map, 8, 6);
        boolean upLeft = clear;
        clear = true;
        clear &= isClear(map, 8, 6);
        clear &= isClear(map, 9, 6);
        clear &= isClear(map, 10, 6);
        clear &= isClear(map, 11, 6);
        clear &= isClear(map, 12, 6);
        boolean upRight = clear;
        clear = true;
        clear &= isClear(map, 1, 2);
        clear &= isClear(map, 2, 3);
        clear &= isClear(map, 3, 3);
        clear &= isClear(map, 4, 3);
        clear &= isClear(map, 5, 3);
        clear &= isClear(map, 6, 4);
        clear &= isClear(map, 7, 4);
        clear &= isClear(map, 8, 4);
        boolean bottomLeft = clear;
        clear &= isClear(map, 8, 4);
        clear &= isClear(map, 9, 4);
        clear &= isClear(map, 10, 4);
        clear &= isClear(map, 11, 4);
        clear = true;
        boolean bottomRight = clear;
        // clear &= isClear(map, 8, 6);
        // clear &= isClear(map, 8, 5);
        // clear &= isClear(map, 8, 4);
        // clear = true;
        // boolean link = clear;

        if ((!upLeft || !upRight) && (!bottomLeft || !bottomRight))
            return gePlayer;
        return usPlayer;
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        map.meteorology.day = Meteorology.Day.NIGHT;

        // hex row I
        Zone geEntry = new Zone(map, 10);
        geEntry.orientation = Orientation.NORTH_EAST;
        for (int i = 0; i < 10; i++)
            geEntry.add(map.getHex(i, 0));
        addEntryZone(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_INFANTRY);
        addReinforcement(gePlayer, geEntry, UnitId.GE_INFANTRY);

        // hex rows A-B
        Zone usEntry = new Zone(map, 19);
        usEntry.orientation = Orientation.SOUTH;
        for (int i = 0; i < 10; i++) {
            usEntry.add(map.getHex((4 + i), 8));
            usEntry.add(map.getHex((3 + i), 6));
            usEntry.add(map.getHex((2 + i), 4));
            usEntry.add(map.getHex((1 + i), 2));
        }
        for (int i = 0; i < 9; i++) {
            usEntry.add(map.getHex((4 + i), 7));
            usEntry.add(map.getHex((3 + i), 5));
            usEntry.add(map.getHex((2 + i), 3));
        }
        addEntryZone(usEntry);

        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_AT_GUN);
        addReinforcement(usPlayer, usEntry, UnitId.US_INFANTRY);
        addReinforcement(usPlayer, usEntry, UnitId.US_INFANTRY);
    }
}
