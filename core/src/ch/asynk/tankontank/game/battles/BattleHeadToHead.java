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
import ch.asynk.tankontank.engine.EntryPoint;
import ch.asynk.tankontank.engine.Orientation;

public class BattleHeadToHead extends BattleCommon
{
    public BattleHeadToHead(Factory factory)
    {
        super(factory);
        randomizeArmies();
    }

    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if ((ctrl.player.getTurn() < 10) || (ctrl.opponent.getTurn() < 10))
            return null;

        Unit unit;
        int player = 0;
        int opponent = 0;

        for (Tile tile : objectives) {
            unit = ((Hex) tile).getUnit();
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
    public Position getHudPosition(Player player)
    {
        return (player.isEnemy(Army.GE) ? Position.TOP_RIGHT: Position.TOP_LEFT);
    }

    @Override
    public Map getMap()
    {
        return factory.getMap(Factory.MapType.MAP_A);
    }

    @Override
    public StateType getState(Player player)
    {
        return StateType.SELECT;
    }

    @Override
    public void setup(Map map, Player a, Player b)
    {
        Player gePlayer;
        Player usPlayer;

        if (a.isEnemy(Army.GE)) {
            usPlayer = a;
            gePlayer = b;
        } else {
            usPlayer = b;
            gePlayer = a;
        }

        objectives = new TileSet(map, 3);
        objectives.add(map.getHex(7, 7));
        objectives.add(map.getHex(6, 4));
        objectives.add(map.getHex(6, 1));
        objectives.enable(Hex.OBJECTIVE, true);

        EntryPoint geEntry = new EntryPoint(map, 10);
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
        addEntryPoint(geEntry);

        EntryPoint usEntry = new EntryPoint(map, 10);
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
        addEntryPoint(usEntry);

        Orientation o = Orientation.NORTH;
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);

        o = Orientation.SOUTH;
        addReinforcement(usPlayer, usEntry, UnitId.US_PRIEST);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
    }
}
