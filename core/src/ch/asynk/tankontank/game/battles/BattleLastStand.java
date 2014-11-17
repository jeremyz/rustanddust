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

public class BattleLastStand extends BattleCommon
{
    public BattleLastStand(Factory factory)
    {
        super(factory);
        name = "Last Stand";
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
            return (first ? usPlayer : gePlayer);
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
        return false;
    }

    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if ((ctrl.player.getTurn() < 8) || (ctrl.opponent.getTurn() < 8))
            return null;

        int gePoints = usPlayer.casualties();
        int usPoints = gePlayer.casualties();
        for (Unit unit : gePlayer.casualties) {
            if (unit.isAce())
                usPoints += 1;
        }
        for (Tile tile : objectives) {
            Unit unit = ((Hex) tile).getUnit();
            if ((unit != null) && unit.is(Army.US))
                usPoints += 1;
        }

        if (usPoints > gePoints)
            return usPlayer;
        else
            return gePlayer;
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        Player gePlayer = ctrl.getPlayer(Army.GE);
        Player usPlayer = ctrl.getPlayer(Army.US);

        objectives = new TileSet(map, 4);
        objectives.add(map.getHex(7, 8));
        objectives.add(map.getHex(6, 4));
        objectives.add(map.getHex(5, 3));
        objectives.add(map.getHex(1, 2));
        objectives.enable(Hex.OBJECTIVE, true);

        EntryPoint geEntry = new EntryPoint(map, 7);
        geEntry.orientation = Orientation.NORTH;
        geEntry.add(map.getHex(5, 5));
        geEntry.add(map.getHex(4, 4));
        geEntry.add(map.getHex(4, 3));
        geEntry.add(map.getHex(5, 3));
        geEntry.add(map.getHex(6, 4));
        geEntry.add(map.getHex(6, 5));
        geEntry.add(map.getHex(5, 4));
        addEntryPoint(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER, true);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);

        geEntry = new EntryPoint(map, 32);
        geEntry.orientation = Orientation.NORTH;
        for (int i = 0; i < 4; i++) {
            geEntry.add(map.getHex(i, 0));
            geEntry.add(map.getHex((i + 1), 2));
            geEntry.add(map.getHex((i + 2), 4));
            geEntry.add(map.getHex((i + 3), 6));
            geEntry.add(map.getHex((i + 4), 8));
        }
        for (int i = 0; i < 3; i++) {
            geEntry.add(map.getHex((i + 1), 1));
            geEntry.add(map.getHex((i + 2), 3));
            geEntry.add(map.getHex((i + 3), 5));
            geEntry.add(map.getHex((i + 4), 7));
        }
        addEntryPoint(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_WESPE);

        EntryPoint usEntry = new EntryPoint(map, 11);
        usEntry.orientation = Orientation.SOUTH;
        usEntry.add(map.getHex(9, 0));
        usEntry.add(map.getHex(9, 1));
        usEntry.add(map.getHex(10, 2));
        usEntry.add(map.getHex(10, 3));
        usEntry.add(map.getHex(11, 4));
        usEntry.add(map.getHex(11, 5));
        usEntry.add(map.getHex(12, 6));
        usEntry.add(map.getHex(12, 7));
        usEntry.add(map.getHex(13, 8));
        usEntry.add(map.getHex(10, 4));
        usEntry.add(map.getHex(11, 6));
        addEntryPoint(usEntry);

        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
    }
}
