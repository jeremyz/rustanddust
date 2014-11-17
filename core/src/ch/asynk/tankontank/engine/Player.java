package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import ch.asynk.tankontank.engine.Pawn;

public abstract class Player
{
    public Faction faction;
    public ArrayList<Pawn> units;
    public ArrayList<Pawn> casualties;
    public ArrayList<Pawn> reinforcement;

    public abstract void turnEnd();
    public abstract void turnStart();

    public Player(Faction faction, int n)
    {
        this.faction = faction;
        this.units = new ArrayList<Pawn>(n);
        this.casualties = new ArrayList<Pawn>(n);
        this.reinforcement = new ArrayList<Pawn>(n);
    }

    public String getName()
    {
        return faction.toString();
    }

    public boolean is(Faction faction)
    {
        return (this.faction == faction);
    }

    public boolean isEnemy(Pawn pawn)
    {
        return pawn.isEnemy(faction);
    }

    public boolean isEnemy(Faction other)
    {
        return faction.isEnemy(other);
    }

    public int unitsLeft()
    {
        return (units.size() + reinforcement.size());
    }

    public int reinforcement()
    {
        return reinforcement.size();
    }

    public int casualties()
    {
        return casualties.size();
    }

    public void addUnit(Pawn pawn)
    {
        units.add(pawn);
    }

    public void addReinforcement(Pawn pawn)
    {
        reinforcement.add(pawn);
    }

    public void unitEntry(Pawn pawn)
    {
        reinforcement.remove(pawn);
        units.add(pawn);
    }

    public void revertUnitEntry(Pawn pawn)
    {
        units.remove(pawn);
        reinforcement.add(pawn);
    }

    public void casualty(Pawn pawn)
    {
        units.remove(pawn);
        casualties.add(pawn);
    }
}
