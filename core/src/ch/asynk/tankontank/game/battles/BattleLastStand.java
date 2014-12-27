package ch.asynk.tankontank.game.battles;

import ch.asynk.tankontank.game.Zone;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.HexSet;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Unit.UnitId;
import ch.asynk.tankontank.ui.Position;
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
    public Player getPlayer()
    {
        if (!gePlayer.isDeploymentDone())
            return gePlayer;
        if (!usPlayer.isDeploymentDone())
            return usPlayer;
        if (gePlayer.getTurnDone() == usPlayer.getTurnDone())
            return usPlayer;
        return gePlayer;
    }

    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if ((ctrl.player.getTurnDone() < 8) || (ctrl.opponent.getTurnDone() < 8))
            return null;

        int gePoints = usPlayer.casualties();
        int usPoints = gePlayer.casualties();
        usPoints += ctrl.map.objectives.count(Army.US);
        for (Unit unit : gePlayer.casualties) {
            if (unit.isAce())
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
        // A7, E6, F6, G10
        map.addObjective(7, 8, Army.NONE);
        map.addObjective(6, 4, Army.NONE);
        map.addObjective(5, 3, Army.NONE);
        map.addObjective(1, 2, Army.NONE);

        // 1 hex of E7
        Zone geEntry = new Zone(map, 7);
        geEntry.orientation = Orientation.NORTH;
        geEntry.add(map.getHex(5, 5));
        geEntry.add(map.getHex(4, 4));
        geEntry.add(map.getHex(4, 3));
        geEntry.add(map.getHex(5, 3));
        geEntry.add(map.getHex(6, 4));
        geEntry.add(map.getHex(6, 5));
        geEntry.add(map.getHex(5, 4));
        addEntryZone(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER, true);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);

        // hex rows 7-10
        geEntry = new Zone(map, 32);
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
        addEntryZone(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_WESPE);

        // hex rows hex row 1 + E2 + C2
        Zone usEntry = new Zone(map, 11);
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
        addEntryZone(usEntry);

        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
    }
}
