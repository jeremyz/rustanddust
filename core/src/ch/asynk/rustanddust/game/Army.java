package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.engine.Faction;
import ch.asynk.rustanddust.game.battles.Factory;

public enum Army implements Faction
{
    NONE("None", null),
    GE("German", Factory.FLAG_GE),
    US("US", Factory.FLAG_US),
    USSR("Soviet", null),
    EN("English", null);

    private String s;
    private String f;

    Army(String s, String f) {
        this.s = s;
        this.f = f;
    }

    public String flag()
    {
        return f;
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
