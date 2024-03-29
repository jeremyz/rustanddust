package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Board;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.util.IterableArray;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Army;

public abstract class Map0Hex extends Board
{
    protected final RustAndDust game;
    protected final IterableArray<Hex> objectives = new IterableArray<Hex>(10);
    protected final IterableArray<Zone> entryZones = new IterableArray<Zone>(10);
    protected final IterableArray<Zone> exitZones = new IterableArray<Zone>(10);

    public Map0Hex(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game.factory, map, hex);

        this.game = game;
    }

    @Override
    public void dispose()
    {
        objectives.clear();
        super.dispose();
    }

    public Hex getHexAt(float x, float y)
    {
        return (Hex) getTileAt(x, y);
    }

    public Hex getHex(int col, int row)
    {
        return (Hex) getTile(col, row);
    }

    public void addObjective(int col, int row, Army army)
    {
        addObjective(col, row, army, true);
    }

    public void addHoldObjective(int col, int row, Army army)
    {
        addObjective(col, row, army, false);
    }

    private void addObjective(int col, int row, Army army, boolean persistent)
    {
        Hex hex = getHex(col, row);
        hex.setObjective(army, (persistent ? Tile.Objective.PERSISTENT : Tile.Objective.VERSATILE));
        showObjective(hex, army, !persistent);
        if (!objectives.contains(hex))
            objectives.add(hex);
    }

    public int objectivesCount(Army army)
    {
        int n = 0;
        for (Hex hex : objectives) {
            if (hex.belongsTo(army))
                n += 1;
        }
        return n;
    }

    public void addEntryZone(Zone zone)
    {
        entryZones.add(zone);
    }

    public void addExitZone(Zone zone)
    {
        exitZones.add(zone);
        zone.enable(Hex.EXIT, true);
    }

    public Zone getEntryZone(int i)
    {
        return entryZones.get(i);
    }

    public Zone getExitZone(int i)
    {
        return exitZones.get(i);
    }

    public void hexSelect(Hex hex)          { selectedTile.set(hex); }
    public void hexUnselect(Hex hex)        { selectedTile.hide(); }
    public void hexMoveShow(Hex hex)        { enableOverlayOn(hex, Hex.MOVE, true); }
    public void hexMoveHide(Hex hex)        { enableOverlayOn(hex, Hex.MOVE, false); }
    public void hexDirectionsShow(Hex hex)  { enableOverlayOn(hex, Hex.DIRECTIONS, true); }
    public void hexDirectionsHide(Hex hex)  { enableOverlayOn(hex, Hex.DIRECTIONS, false); }
    public void hexExitShow(Hex hex)        { enableOverlayOn(hex, Hex.EXIT, true); }
    public void hexExitHide(Hex hex)        { enableOverlayOn(hex, Hex.EXIT, false); }

    private void showObjective(Hex hex, Army army, boolean hold)
    {
        if (hold)
            enableOverlayOn(hex, Hex.OBJECTIVE_HOLD, true);
        else
            enableOverlayOn(hex, Hex.OBJECTIVE, true);
        showObjective(hex, army);
    }

    protected void showObjective(Hex hex, Army army)
    {
        if (army == null)
            army = Army.NONE;
        switch(army)
        {
            case GE:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, true);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, false);
                break;
            case US:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, false);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, true);
                break;
            case NONE:
            default:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, false);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, false);
                break;
        }
    }
}
