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

public class BattleCounterAttack extends BattleCommon
{
    public BattleCounterAttack(Factory factory)
    {
        super(factory);
        name = "Counterattack";
    }

    @Override
    public Map getMap()
    {
        return factory.getMap(Factory.MapType.MAP_B);
    }

    @Override
    public Position getHudPosition(Player player)
    {
        return (player.is(Army.US) ? Position.TOP_RIGHT: Position.TOP_LEFT);
    }

    @Override
    public Player getPlayer(boolean first, boolean deploymentPhase)
    {
        if (deploymentPhase)
            return (first ? gePlayer : usPlayer);
        else
            return (first ? gePlayer : usPlayer);
    }

    @Override
    public StateType getState(Player player)
    {
        if (player.getTurn() == 0)
            return StateType.DEPLOYMENT;
        return StateType.SELECT;
    }

    @Override
    public boolean deploymentDone(Player player)
    {
        int n = player.reinforcement();
        if (n == 0) {
            player.deploymentDone();
            return true;
        }
        return false;
    }

    @Override
    public boolean getReinforcement(Ctrl ctrl, Map map)
    {
        if (ctrl.player.is(Army.GE))
            return false;
        if (ctrl.player.getTurn() != 5)
            return false;

        EntryPoint usEntry = new EntryPoint(map, 9);
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

        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_PRIEST);

        return true;
    }

    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if (gePlayer.escaped() >= 3)
            return gePlayer;

        if ((ctrl.player.getTurn() < 9) || (ctrl.opponent.getTurn() < 9))
            return null;

        return usPlayer;
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        Player gePlayer = ctrl.getPlayer(Army.GE);
        Player usPlayer = ctrl.getPlayer(Army.US);

        EntryPoint geEntry = new EntryPoint(map, 18);
        geEntry.orientation = Orientation.NORTH;
        for (int i = 0; i < 2; i++) {
            geEntry.add(map.getHex((1 + i), 0));
            geEntry.add(map.getHex((1 + i), 1));
            geEntry.add(map.getHex((2 + i), 2));
            geEntry.add(map.getHex((2 + i), 3));
            geEntry.add(map.getHex((3 + i), 4));
            geEntry.add(map.getHex((3 + i), 5));
            geEntry.add(map.getHex((4 + i), 6));
            geEntry.add(map.getHex((4 + i), 7));
            geEntry.add(map.getHex((5 + i), 8));
        }
        addEntryPoint(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_WESPE);

        EntryPoint geExit = new EntryPoint(map, 9);
        geExit.allowedMoves = (Orientation.NORTH.s | Orientation.NORTH_EAST.s | Orientation.NORTH_WEST.s);
        geExit.add(map.getHex(9, 0));
        geExit.add(map.getHex(9, 1));
        geExit.add(map.getHex(10, 2));
        geExit.add(map.getHex(10, 3));
        geExit.add(map.getHex(11, 4));
        geExit.add(map.getHex(11, 5));
        geExit.add(map.getHex(12, 6));
        geExit.add(map.getHex(12, 7));
        geExit.add(map.getHex(13, 8));
        addExitPoint(geExit);

        EntryPoint usEntry = new EntryPoint(map, 36);
        usEntry.orientation = Orientation.SOUTH;
        for (int i = 0; i < 4; i++) {
            usEntry.add(map.getHex((6 + i), 0));
            usEntry.add(map.getHex((6 + i), 1));
            usEntry.add(map.getHex((7 + i), 2));
            usEntry.add(map.getHex((7 + i), 3));
            usEntry.add(map.getHex((8 + i), 4));
            usEntry.add(map.getHex((8 + i), 5));
            usEntry.add(map.getHex((9 + i), 6));
            usEntry.add(map.getHex((9 + i), 7));
            usEntry.add(map.getHex((10 + i), 8));
        }
        addEntryPoint(usEntry);

        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);

    }
}
