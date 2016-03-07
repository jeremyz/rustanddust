package ch.asynk.rustanddust.game.battles;

import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit.UnitCode;
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
    public Player getWinner()
    {
        return getWinner(8);
    }

    @Override
    public boolean hasReinforcement()
    {
        return false;
    }

    // SETUP

    @Override
    protected void setup()
    {
        players[0] = factory.getPlayer(Army.US);
        players[1] = factory.getPlayer(Army.GE);

        map.addHoldObjective(5, 2, Army.NONE);
        map.addObjective(11, 7, Army.US);
    }

    @Override
    protected void deployPlayer()
    {
        if (currentPlayer.army == Army.US)
            setupUS(currentPlayer);
        else
            setupGE(currentPlayer);
    }

    private void setupUS(final Player p)
    {
        setUnit(map, p, UnitCode.US_AT_GUN, 11, 7, Orientation.SOUTH, null);

        Zone usEntry = new Zone(map, 10);
        usEntry.orientation = Orientation.SOUTH;
        usEntry.add(8, 0);
        usEntry.add(9, 0);
        usEntry.add(8, 1);
        usEntry.add(9, 1);
        usEntry.add(9, 2);
        usEntry.add(10, 2);
        usEntry.add(9, 3);
        usEntry.add(10, 3);
        usEntry.add(10, 4);
        usEntry.add(11, 4);
        addEntryZone(usEntry);
        addReinforcement(p, usEntry, UnitCode.US_SHERMAN, true, false);
        addReinforcement(p, usEntry, UnitCode.US_SHERMAN);
        addReinforcement(p, usEntry, UnitCode.US_SHERMAN);
        addReinforcement(p, usEntry, UnitCode.US_SHERMAN);
        addReinforcement(p, usEntry, UnitCode.US_WOLVERINE, true, false);
        addReinforcement(p, usEntry, UnitCode.US_WOLVERINE);
        addReinforcement(p, usEntry, UnitCode.US_PERSHING);
        addReinforcement(p, usEntry, UnitCode.US_PRIEST);
    }

    private void setupGE(final Player p)
    {
        Zone geEntry = new Zone(map, 8);
        geEntry.orientation = Orientation.NORTH;
        geEntry.add(4, 8);
        geEntry.add(5, 8);
        geEntry.add(4, 7);
        geEntry.add(5, 7);
        geEntry.add(3, 6);
        geEntry.add(4, 6);
        geEntry.add(3, 5);
        geEntry.add(4, 5);
        addEntryZone(geEntry);
        addReinforcement(p, geEntry, UnitCode.GE_PANZER_IV, true, false);
        addReinforcement(p, geEntry, UnitCode.GE_PANZER_IV);
        addReinforcement(p, geEntry, UnitCode.GE_PANZER_IV);
        addReinforcement(p, geEntry, UnitCode.GE_PANZER_IV, true, false);
        addReinforcement(p, geEntry, UnitCode.GE_TIGER);
        addReinforcement(p, geEntry, UnitCode.GE_TIGER);
        addReinforcement(p, geEntry, UnitCode.GE_WESPE);
    }
}
