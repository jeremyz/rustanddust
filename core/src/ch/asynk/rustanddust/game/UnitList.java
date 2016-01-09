package ch.asynk.rustanddust.game;

import java.util.Collection;
import ch.asynk.rustanddust.engine.util.ArrayListIt;

import ch.asynk.rustanddust.engine.Pawn;

public class UnitList extends ArrayListIt<Unit>
{
    public UnitList(int n)
    {
        super(n);
    }

    @SuppressWarnings("unchecked")
    public Collection<Pawn> asPawns()
    {
        return (Collection) this;
    }
}
