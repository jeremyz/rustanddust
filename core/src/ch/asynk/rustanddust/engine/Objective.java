package ch.asynk.rustanddust.engine;

public class Objective
{
    protected Faction curFaction;
    protected Faction prevFaction;
    private boolean persistent;

    public Objective(Faction faction, boolean persistent)
    {
        this.curFaction = faction;
        this.prevFaction = faction;
        this.persistent = persistent;
    }

    public boolean is(Faction faction)
    {
        return (curFaction == faction);
    }

    public Faction faction()
    {
        return curFaction;
    }

    public boolean set(Faction faction)
    {
        if (faction == curFaction)
            return false;

        prevFaction = curFaction;
        curFaction = faction;
        return true;
    }

    public boolean unset()
    {
        if (persistent)
            return false;
        revert();
        return true;
    }

    public Faction revert()
    {
        curFaction = prevFaction;
        return curFaction;
    }
}
