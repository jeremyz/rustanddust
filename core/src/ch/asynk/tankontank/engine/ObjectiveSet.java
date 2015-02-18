package ch.asynk.tankontank.engine;

import java.util.Collection;
import java.util.HashMap;

public class ObjectiveSet extends HashMap<Tile, Objective>
{
    public interface ObjectiveCb
    {
        public void showObjective(Tile tile, Faction faction);
    }

    private final Board board;
    private final HashMap<Objective, Tile> modified;

    public ObjectiveSet(Board board, int n)
    {
        super(n);
        this.board = board;
        this.modified = new HashMap<Objective, Tile>(10);
    }

    public void add(Tile tile, Faction faction, boolean persistent)
    {
        put(tile, new Objective(faction, persistent));
    }

    public int count(Faction faction)
    {
        int n = 0;
        for (Objective objective : values()) {
            if (objective.is(faction))
                n += 1;
        }
        return n;
    }

    public Faction claim(Tile tile, Faction faction)
    {
        Objective objective = get(tile);
        if (objective == null)
            return null;

        if (objective.set(faction))
            modified.put(objective, tile);
        return objective.faction();
    }

    public Faction unclaim(Tile tile)
    {
        Objective objective = get(tile);
        if (objective == null)
            return null;

        if (objective.unset())
            modified.remove(objective);
        return objective.faction();
    }

    public void forget()
    {
        modified.clear();
    }

    public int modifiedCount()
    {
        return modified.size();
    }

    public void revert(ObjectiveCb cb)
    {
        for (Objective objective : modified.keySet()) {
            objective.revert();
            cb.showObjective(modified.get(objective), objective.faction());
        }
        modified.clear();
    }
}
