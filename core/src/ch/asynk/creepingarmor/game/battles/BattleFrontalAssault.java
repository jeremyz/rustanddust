package ch.asynk.creepingarmor.game.battles;

import ch.asynk.creepingarmor.game.Army;
import ch.asynk.creepingarmor.game.Player;
import ch.asynk.creepingarmor.game.Ctrl;
import ch.asynk.creepingarmor.game.Map;
import ch.asynk.creepingarmor.game.Hex;
import ch.asynk.creepingarmor.game.HexSet;
import ch.asynk.creepingarmor.game.Zone;
import ch.asynk.creepingarmor.game.Unit;
import ch.asynk.creepingarmor.game.Unit.UnitId;
import ch.asynk.creepingarmor.ui.Position;
import ch.asynk.creepingarmor.engine.Orientation;

public class BattleFrontalAssault extends BattleCommon
{
    public BattleFrontalAssault(Factory factory)
    {
        super(factory);
        name = "Frontal Assault";
        mapType = Factory.MapType.MAP_A;
    }


    @Override
    public Player getPlayer()
    {
        if (!gePlayer.isDeploymentDone()) {
            int n = gePlayer.reinforcement();
            if (n > 4)
                return gePlayer;
            else {
                if (usPlayer.isDeploymentDone())
                    return gePlayer;
                else
                    return usPlayer;
            }
        }
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
    public boolean deploymentDone(Player player)
    {
        if (player.isDeploymentDone())
            return true;
        return ((player.is(Army.GE) && (gePlayer.reinforcement.size() == 4)));
    }

    @Override
    public Player checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return ctrl.player;

        if ((ctrl.player.getTurnDone() < 10) || (ctrl.opponent.getTurnDone() < 10))
            return null;

        if (ctrl.map.objectives.count(Army.US) >= 2)
            return usPlayer;
        else
            return gePlayer;
    }

    @Override
    public void setup(Ctrl ctrl, Map map)
    {
        // G9, E6, H4
        map.addObjective(2, 2, Army.NONE);
        map.addObjective(6, 4, Army.NONE);
        map.addObjective(6, 1, Army.NONE);

        // hex rows E-H
        Zone geEntry = new Zone(map, 38);
        geEntry.orientation = Orientation.NORTH_WEST;
        for (int i = 2; i < 12; i++)
            geEntry.add(map.getHex(i, 4));
        for (int i = 2; i < 11; i++)
            geEntry.add(map.getHex(i, 3));
        for (int i = 1; i < 11; i++)
            geEntry.add(map.getHex(i, 2));
        for (int i = 1; i < 10; i++)
            geEntry.add(map.getHex(i, 1));
        addEntryZone(geEntry);

        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV_HQ);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);

        // hex rows A-B
        Zone usEntry = new Zone(map, 19);
        usEntry.orientation = Orientation.SOUTH_EAST;
        for (int i = 4; i < 14; i++)
            usEntry.add(map.getHex(i, 8));
        for (int i = 4; i < 13; i++)
            usEntry.add(map.getHex(i, 7));
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
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_PRIEST);
    }
}
