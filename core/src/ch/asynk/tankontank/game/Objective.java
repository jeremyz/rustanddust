package ch.asynk.tankontank.game;

public class Objective
{
    protected Army curArmy;
    protected Army prevArmy;
    private boolean persistent;

    public Objective(Army army, boolean persistent)
    {
        this.curArmy = army;
        this.prevArmy = army;
        this.persistent = persistent;
    }

    public boolean is(Army army)
    {
        return (curArmy == army);
    }

    public Army army()
    {
        return curArmy;
    }

    public boolean set(Army army)
    {
        if (army == curArmy)
            return false;
        prevArmy = curArmy;
        curArmy = army;
        return true;
    }

    public boolean unset()
    {
        if (persistent)
            return false;
        revert();
        return true;
    }

    public Army revert()
    {
        curArmy = prevArmy;
        return curArmy;
    }
}
