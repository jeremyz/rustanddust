package ch.asynk.tankontank.game;

import java.util.Collection;
import java.util.LinkedHashSet;

import ch.asynk.tankontank.engine.Pawn;

public class UnitSet extends LinkedHashSet<Unit>
{
    private final Map map;

    public UnitSet(Map map, int n)
    {
        super(n);
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    public Collection<Pawn> asPawns()
    {
        return (Collection) this;
    }

    public Unit first()
    {
        if (isEmpty()) return null;
        return iterator().next();
    }

    public void enable(int i, boolean enable)
    {
        for (Unit unit : this)
            unit.enableOverlay(i, enable);
    }
}
