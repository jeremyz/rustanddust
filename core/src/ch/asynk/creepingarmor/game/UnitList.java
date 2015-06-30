package ch.asynk.creepingarmor.game;

import java.util.Collection;
import java.util.ArrayList;

import ch.asynk.creepingarmor.engine.Pawn;

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
