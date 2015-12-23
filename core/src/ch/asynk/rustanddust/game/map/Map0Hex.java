package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Pawn;
import ch.asynk.rustanddust.engine.Board;
import ch.asynk.rustanddust.engine.Faction;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.ObjectiveSet;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Army;

public abstract class Map0Hex extends Board implements ObjectiveSet.ObjectiveCb
{
    protected final RustAndDust game;
    protected final ObjectiveSet objectives;

    public Map0Hex(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game.factory, map, hex);

        this.game = game;
        objectives = new ObjectiveSet(this, 4);
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
        objectives.add(hex, army, persistent);
        showObjective(hex, army, !persistent);
    }

    protected void claim(Hex hex, Army army)
    {
        showObjective(hex, objectives.claim(hex, army));
    }

    protected void unclaim(Hex hex)
    {
        showObjective(hex, objectives.unclaim(hex));
    }

    public int objectivesCount(Army army)
    {
        return objectives.count(army);
    }

    public void hexSelect(Hex hex)          { selectedTile.set(hex); }
    public void hexUnselect(Hex hex)        { selectedTile.hide(); }
    public void hexMoveShow(Hex hex)        { enableOverlayOn(hex, Hex.MOVE, true); }
    public void hexMoveHide(Hex hex)        { enableOverlayOn(hex, Hex.MOVE, false); }
    public void hexDirectionsShow(Hex hex)  { enableOverlayOn(hex, Hex.DIRECTIONS, true); }
    public void hexDirectionsHide(Hex hex)  { enableOverlayOn(hex, Hex.DIRECTIONS, false); }
    public void hexExitShow(Hex hex)        { enableOverlayOn(hex, Hex.EXIT, true); }
    public void hexExitHide(Hex hex)        { enableOverlayOn(hex, Hex.EXIT, false); }

    @Override
    public boolean isObjectiveFor(Tile tile, Pawn pawn)
    {
        return objectives.isObjectiveFor(tile, pawn);
    }

    @Override
    public void showObjective(Tile tile, Faction faction)
    {
        showObjective((Hex) tile, (Army) faction);
    }

    private void showObjective(Hex hex, Army army, boolean hold)
    {
        if (hold)
            enableOverlayOn(hex, Hex.OBJECTIVE_HOLD, true);
        else
            enableOverlayOn(hex, Hex.OBJECTIVE, true);
        showObjective(hex, army);
    }

    private void showObjective(Hex hex, Army army)
    {
        if (army == null)
            army = Army.NONE;
        switch(army) {
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
