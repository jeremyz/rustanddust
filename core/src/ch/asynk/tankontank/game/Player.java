package ch.asynk.tankontank.game;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Pawn;

public class Player
{
    private static final float MOVE_TIME = 0.4f;

    private static Random rand = new Random();

    private int turn;
    private int apSpent;
    private int actionPoints;
    private boolean deploymentDone;

    public Army army;
    public ArrayList<Unit> units;
    public ArrayList<Unit> casualties;
    public ArrayList<Unit> reinforcement;

    public int actionCount;
    public int lostEngagementCount;
    public int wonEngagementCount;

    public Player(final TankOnTank game, Army army, int n)
    {
        this.army = army;
        this.units = new ArrayList<Unit>(n);
        this.casualties = new ArrayList<Unit>(n);
        this.reinforcement = new ArrayList<Unit>(n);
        this.turn = 0;
        this.apSpent = 0;
        this.actionPoints = 0;
        this.deploymentDone = false;
        this.actionCount = 0;
        this.lostEngagementCount = 0;
        this.wonEngagementCount = 0;
    }

    public String getName()
    {
        return army.toString();
    }

    public String toString()
    {
        return army + " AP: " + actionPoints +
            " units:" + units.size() + " casualties:" + casualties.size();
    }

    public String getStats()
    {
        return String.format("%s\n%4d\n%4d\n%4d\n%4d\n%4d", getName(), actionCount, unitsLeft(), casualties.size(), wonEngagementCount, lostEngagementCount);
    }

    public boolean is(Army army)
    {
        return (this.army == army);
    }

    public boolean isEnemy(Unit unit)
    {
        return unit.isEnemy(army);
    }

    public boolean isEnemy(Army other)
    {
        return army.isEnemy(other);
    }

   @SuppressWarnings("unchecked")
    public List<Pawn> unitsAsPawns()
    {
        return (List) units;
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

    public void addUnit(Unit unit)
    {
        units.add(unit);
    }

    public void addReinforcement(Unit unit)
    {
        reinforcement.add(unit);
    }

    public void unitEntry(Unit unit)
    {
        reinforcement.remove(unit);
        units.add(unit);
    }

    public void revertUnitEntry(Unit unit)
    {
        units.remove(unit);
        reinforcement.add(unit);
    }

    public void casualty(Unit unit)
    {
        units.remove(unit);
        casualties.add(unit);
    }

    public int getAp()
    {
        return ((apSpent < actionPoints) ? (apSpent + 1) : apSpent);
    }

    public int getTurn()
    {
        return turn;
    }

    public boolean apExhausted()
    {
        return (apSpent == actionPoints);
    }

    public boolean isDeploymentDone()
    {
        return deploymentDone;
    }

    public void deploymentDone()
    {
        deploymentDone = true;
    }

    public void burnDownOneAp()
    {
        apSpent += 1;
        actionCount += 1;
        if (apSpent > actionPoints) TankOnTank.debug("ERROR: spent too much AP, please report");
    }

    public void turnEnd()
    {
    }

    public void turnStart()
    {
        if (!deploymentDone)
            return;
        turn += 1;
        for (Unit unit : units)
            unit.reset();
        computeActionPoints();
    }

    public int d6()
    {
        return rand.nextInt(6) + 1;
    }

    private void computeActionPoints()
    {
        this.actionPoints = 2;
        if (d6() > 2) {
            this.actionPoints += 1;
            if (d6() > 3)
                this.actionPoints += 1;
        }
        apSpent = 0;
    }

    public boolean canPromote(Unit unit)
    {
        if (unit.isHq()) return false;
        for (Unit p: casualties)
            if (p.isHqOf(unit)) return true;
        return false;
    }

    public Unit promote(Unit unit)
    {
        for (Unit p: casualties) {
            if (p.isHqOf(unit)) {
                units.remove(unit);
                casualties.add(unit);
                units.add(p);
                casualties.remove(p);
                return p;
            }
        }
        return null;
    }
}
