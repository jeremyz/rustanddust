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

public class BattleFrontalAssault extends BattleCommon
{
    public BattleFrontalAssault(Factory factory)
    {
        super(factory);
        name = "Frontal Assault";
    }

    @Override
    public Map getMap()
    {
        return factory.getMap(Factory.MapType.MAP_A);
    }

    @Override
    public Player getPlayer(boolean first, boolean deploymentPhase)
    {
        return factory.getPlayer((first ? Army.GE : Army.US));
    }

    @Override
    public Position getHudPosition(Player player)
    {
        return (player.is(Army.US) ? Position.TOP_RIGHT: Position.TOP_LEFT);
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
        return ((player.is(Army.GE) && (n == 4)));
    }

    @Override
    public boolean getReinforcement(Ctrl ctrl)
    {
        return false;
    }

    @Override
    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if ((ctrl.player.getTurn() < 10) || (ctrl.opponent.getTurn() < 10))
            return null;

        int count = 0;
        for (Tile tile : objectives) {
            Unit unit = ((Hex) tile).getUnit();
            if ((unit != null) && unit.is(Army.US))
                count =+ 1;
        }

        if (count > 1)
            return ctrl.getPlayer(Army.US);
        else
            return ctrl.getPlayer(Army.GE);
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        Player gePlayer = ctrl.getPlayer(Army.GE);
        Player usPlayer = ctrl.getPlayer(Army.US);

        objectives = new TileSet(map, 3);
        objectives.add(map.getHex(2, 2));
        objectives.add(map.getHex(6, 4));
        objectives.add(map.getHex(6, 1));
        objectives.enable(Hex.OBJECTIVE, true);

        EntryPoint geEntry = new EntryPoint(map, 38);
        geEntry.orientation = Orientation.NORTH_WEST;
        for (int i = 2; i < 12; i++)
            geEntry.add(map.getHex(i, 4));
        for (int i = 2; i < 11; i++)
            geEntry.add(map.getHex(i, 3));
        for (int i = 1; i < 11; i++)
            geEntry.add(map.getHex(i, 2));
        for (int i = 1; i < 10; i++)
            geEntry.add(map.getHex(i, 1));
        addEntryPoint(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);

        EntryPoint usEntry = new EntryPoint(map, 19);
        usEntry.orientation = Orientation.SOUTH_EAST;
        for (int i = 4; i < 14; i++)
            usEntry.add(map.getHex(i, 8));
        for (int i = 4; i < 13; i++)
            usEntry.add(map.getHex(i, 7));
        addEntryPoint(usEntry);

        addReinforcement(usPlayer, usEntry, UnitId.US_PRIEST);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
    }
}
