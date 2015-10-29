package ch.asynk.rustanddust.game;

import java.util.Collection;
import java.util.ArrayList;

import ch.asynk.rustanddust.engine.Pawn;

public class UnitList extends ArrayList<Unit>
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