package ch.asynk.tankontank.game;

import ch.asynk.tankontank.engine.Faction;

public enum Army implements Faction
{
    GE("German"),
    US("US"),
    USSR("Soviet"),
    EN("English");

    private String s;

    Army(String s) {
        this.s = s;
    }

    @Override
    public boolean isEnemy(Faction other)
    {
        return (this != other);
    }

    @Override
    public String toString()
    {
        return s;
    }
}
