package ch.asynk.creepingarmor.game;

import ch.asynk.creepingarmor.engine.Faction;

public enum Army implements Faction
{
    NONE("None"),
    GE("German"),
    US("US"),
    USSR("Soviet"),
    EN("English");

    private String s;

    Army(String s) {
        this.s = s;
    }

    @Override
    public String toString()
    {
        return s;
    }

    @Override
    public boolean isEnemy(Faction other)
    {
        return (this != other);
    }
}