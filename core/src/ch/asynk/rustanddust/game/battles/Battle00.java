package ch.asynk.rustanddust.game.battles;

import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit.UnitId;
import ch.asynk.rustanddust.game.Factory;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.engine.Orientation;

public class Battle00 extends BattleCommon
{
    public Battle00(Factory factory)
    {
        super(factory);
        _id = 1;
        name = "Les Fleurs du mal";
        description = "This is a test battle."
            + "\nDon't expect it to be neither interesting,\nnor well ballanced."
            + "\n\nThe victory conditions are:"
            + "\n * at any point, destroy all enemy units."
            + "\n * after 8 turns,\n   hold more objectives than your opponent."
            + "";
        mapType = Factory.MapType.MAP_00;
    }

    @Override
    public Position getHudPosition()
    {
        return (currentPlayer.is(Army.US) ? Position.TOP_RIGHT: Position.TOP_LEFT);
    }

    @Override
    public void start()
    {
        map.actionDone();
        map.turnDone();
        usPlayer.turnEnd();
        gePlayer.turnEnd();
        currentPlayer = usPlayer;
    }

    @Override
    public Player getWinner()
    {
        if (!abTurnDone())
            return null;

        if (gePlayer.unitsLeft() == 0)
            return usPlayer;
        if (usPlayer.unitsLeft() == 0)
            return gePlayer;

        if (gePlayer.getTurnDone() <= 8)
            return null;

        usPlayer.objectivesWon = map.objectivesCount(Army.US);
        gePlayer.objectivesWon = map.objectivesCount(Army.GE);

        if (usPlayer.objectivesWon >= gePlayer.objectivesWon) {
            return usPlayer;
        } else {
            return gePlayer;
        }
    }

    @Override
    public boolean hasReinforcement()
    {
        return false;
    }

    @Override
    public Map setup()
    {
        super.setup();

        map.addHoldObjective(5, 2, Army.NONE);
        map.addObjective(11, 7, Army.US);

        currentPlayer = usPlayer;
        setUnit(map, usPlayer, UnitId.US_AT_GUN, 11, 7, Orientation.SOUTH, null);

        Zone usEntry = new Zone(map, 10);
        usEntry.orientation = Orientation.SOUTH;
        usEntry.add(map.getHex(8, 0));
        usEntry.add(map.getHex(9, 0));
        usEntry.add(map.getHex(8, 1));
        usEntry.add(map.getHex(9, 1));
        usEntry.add(map.getHex(9, 2));
        usEntry.add(map.getHex(10, 2));
        usEntry.add(map.getHex(9, 3));
        usEntry.add(map.getHex(10, 3));
        usEntry.add(map.getHex(10, 4));
        usEntry.add(map.getHex(11, 4));
        addEntryZone(usEntry);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN, true, false);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_SHERMAN);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE, true, false);
        addReinforcement(usPlayer, usEntry, UnitId.US_WOLVERINE);
        addReinforcement(usPlayer, usEntry, UnitId.US_PERSHING);
        addReinforcement(usPlayer, usEntry, UnitId.US_PRIEST);

        Zone geEntry = new Zone(map, 8);
        geEntry.orientation = Orientation.NORTH;
        geEntry.add(map.getHex(4, 8));
        geEntry.add(map.getHex(5, 8));
        geEntry.add(map.getHex(4, 7));
        geEntry.add(map.getHex(5, 7));
        geEntry.add(map.getHex(3, 6));
        geEntry.add(map.getHex(4, 6));
        geEntry.add(map.getHex(3, 5));
        geEntry.add(map.getHex(4, 5));
        addEntryZone(geEntry);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV, true, false);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV);
        addReinforcement(gePlayer, geEntry, UnitId.GE_PANZER_IV, true, false);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_TIGER);
        addReinforcement(gePlayer, geEntry, UnitId.GE_WESPE);

        return this.map;
    }
}
