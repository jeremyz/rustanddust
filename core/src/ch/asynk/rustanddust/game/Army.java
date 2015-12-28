package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.engine.Faction;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Factory;

public enum Army implements Faction
{
    NONE("None", null, -1),
    GE("German", Factory.FLAG_GE, Hex.OBJECTIVE_GE),
    US("US", Factory.FLAG_US, Hex.OBJECTIVE_US),
    USSR("Soviet", null, -1),
    EN("English", null, -1);

    public final String s;
    public final String flag;
    public final int overlay;

    Army(String s, String f, int o) {
        this.s = s;
        this.flag = f;
        this.overlay = o;
    }

    @Override
    public int overlay()
    {
        return overlay;
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
