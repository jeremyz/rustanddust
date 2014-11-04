package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import ch.asynk.tankontank.engine.Pawn;

public abstract class Player
{
    protected Faction faction;
    protected ArrayList<Pawn> units;
    protected ArrayList<Pawn> casualties;
    protected ArrayList<Pawn> reinforcement;

    public abstract void turnEnd();
    public abstract void turnStart();

    public Player(Faction faction, int n)
    {
        this.faction = faction;
        this.units = new ArrayList<Pawn>(n);
        this.casualties = new ArrayList<Pawn>(n);
        this.reinforcement = new ArrayList<Pawn>(n);
    }

    public Faction getFaction()
    {
        return faction;
    }

    public List<Pawn> getReinforcement()
    {
        return reinforcement;
    }

    public int unitsLeft()
    {
        return (units.size() + reinforcement.size());
    }

    public String getName()
    {
        return faction.toString();
    }

    public boolean isEnemy(Pawn pawn)
    {
        return pawn.isEnemy(faction);
    }

    public boolean isEnemy(Faction other)
    {
        return faction.isEnemy(other);
    }

    public void addUnit(Pawn pawn)
    {
        units.add(pawn);
    }

    public void addReinforcement(Pawn pawn)
    {
        reinforcement.add(pawn);
    }

    public void casualty(Pawn pawn)
    {
        units.remove(pawn);
        casualties.add(pawn);
        System.err.println("    casualty : " + pawn);
    }

    public Iterator<Pawn> unitIterator()
    {
        return units.iterator();
    }
}
