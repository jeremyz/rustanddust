package ch.asynk.tankontank.game;

import java.util.Collection;
import java.util.HashMap;

import ch.asynk.tankontank.engine.gfx.Moveable;
import ch.asynk.tankontank.engine.gfx.animations.MoveToAnimation.MoveToAnimationCb;

public class ObjectiveSet extends HashMap<Hex, Objective> implements MoveToAnimationCb
{
    private final Map map;
    private final HashMap<Objective, Hex> modified;

    public ObjectiveSet(Map map, int n)
    {
        super(n);
        this.map = map;
        this.modified = new HashMap<Objective, Hex>(10);
    }

    public void add(Hex hex, Army army, boolean persistent)
    {
        put(hex, new Objective(army, persistent));
        map.showObjective(hex, army);
    }

    public int count(Army army)
    {
        int n = 0;
        for (Objective objective : values()) {
            if (objective.is(army))
                n += 1;
        }
        return n;
    }

    @Override
    public void moveToAnimationEnter(Moveable moveable, float x, float y, float r)
    {
        claim(map.getHexAt(x, y), (Army) moveable.getFaction());
    }

    @Override
    public void moveToAnimationLeave(Moveable moveable, float x, float y, float r)
    {
        unclaim(map.getHexAt(x, y));
    }

    @Override
    public void moveToAnimationDone(Moveable moveable, float x, float y, float r)
    {
    }

    public Army claim(Hex hex, Army army)
    {
        Objective objective = get(hex);
        if (objective == null)
            return Army.NONE;
        if (objective.set(army)) {
            modified.put(objective, hex);
            map.showObjective(hex, army);
        }
        return army;
    }

    public void unclaim(Hex hex)
    {
        Objective objective = get(hex);
        if (objective == null)
            return;
        if (objective.unset()) {
            modified.remove(objective);
            map.showObjective(hex, objective.army());
        }
    }

    public void forget()
    {
        modified.clear();
    }

    public void revert()
    {
        for (Objective objective : modified.keySet()) {
            objective.revert();
            map.showObjective(modified.get(objective), objective.army());
        }
        modified.clear();
    }
}
