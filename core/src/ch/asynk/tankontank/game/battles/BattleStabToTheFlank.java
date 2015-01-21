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

public class BattleStabToTheFlank extends BattleCommon
{
    public BattleStabToTheFlank(Factory factory)
    {
        super(factory);
        name = "Stab To The Flank";
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
        if (!usPlayer.isDeploymentDone() || usPlayer.getCurrentTurn() == 1)
            return usPlayer;
        if (usPlayer.getTurnDone() > gePlayer.getTurnDone())
            return gePlayer;
        return usPlayer;
    }

    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if ((ctrl.player.getTurnDone() < 9) || (ctrl.opponent.getTurnDone() < 9))
            return null;

        int gePoints = usPlayer.casualties();
        int usPoints = gePlayer.casualties();
        usPoints += ctrl.map.objectives.count(Army.US);

        int withdrawed = usPlayer.withdrawed();
        if (withdrawed == 0)
            gePoints += 1;
        else
            usPoints += withdrawed;

        if (usPoints > gePoints)
            return usPlayer;
        else
            return gePlayer;
    }

    @Override
    public boolean getReinforcement(Ctrl ctrl, Map map)
    {
        if (ctrl.player.is(Army.US))
            return false;
        if (ctrl.player.getCurrentTurn() != 3)
            return false;

        // hex rows I
        Zone geEntry = new Zone(map, 9);
        geEntry.allowedMoves = (Orientation.SOUTH_WEST.s | Orientation.NORTH_WEST.s);
        for (int i = 0; i < 10; i++)
            geEntry.add(map.getHex(i, 0));
        addEntryZone(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER, true);

        return true;
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        // F6, E6
        map.addHoldObjective(5, 3, Army.NONE);
        map.addObjective(6, 4, Army.NONE);

        // hex rows D-I
        Zone geEntry = new Zone(map, 57);
        geEntry.orientation = Orientation.NORTH;
        for (int i = 3; i < 12; i++)
            geEntry.add(map.getHex(i, 5));
        for (int i = 2; i < 12; i++)
            geEntry.add(map.getHex(i, 4));
        for (int i = 2; i < 11; i++)
            geEntry.add(map.getHex(i, 3));
        for (int i = 1; i < 11; i++)
            geEntry.add(map.getHex(i, 2));
        for (int i = 1; i < 10; i++)
            geEntry.add(map.getHex(i, 1));
        for (int i = 0; i < 10; i++)
            geEntry.add(map.getHex(i, 0));

        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_AT_GUN);
        addReinforcement(gePlayer, geEntry, UnitId.GE_INFANTRY);

        // hex row I
        Zone usExit = new Zone(map, 10);
        usExit.orientation = Orientation.NORTH_EAST;
        for (int i = 0; i < 10; i++)
            geEntry.add(map.getHex(i, 0));
        addExitZone(usExit);

        // hex rows A-B
        Zone usEntry = new Zone(map, 19);
        usEntry.orientation = Orientation.SOUTH_EAST;
        for (int i = 4; i < 13; i++) {
            usEntry.add(map.getHex(i, 8));
            usEntry.add(map.getHex(i, 7));
        }
        usEntry.add(map.getHex(13, 8));
        addEntryZone(usEntry);

        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_SHERMAN_HQ);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_INFANTRY);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_INFANTRY);
        addReinforcement(usPlayer, usEntry, usExit, UnitId.US_PRIEST);
    }
}
