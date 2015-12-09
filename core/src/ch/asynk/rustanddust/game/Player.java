package ch.asynk.rustanddust.game;

import java.util.Random;
import java.util.List;

import ch.asynk.rustanddust.RustAndDust;

public class Player
{
    private static final float MOVE_TIME = 0.4f;

    private static Random rand = new Random();

    private int turn;
    private int apSpent;
    private int actionPoints;
    private boolean deploymentDone;

    public Army army;
    public UnitList units;
    public UnitList casualties;
    public UnitList reinforcement;
    public UnitList withdrawed;

    public int actionCount;
    public int lostEngagementCount;
    public int wonEngagementCount;

    public Player(final RustAndDust game, Army army, int n)
    {
        this.army = army;
        this.units = new UnitList(n);
        this.casualties = new UnitList(n);
        this.reinforcement = new UnitList(n);
        this.withdrawed = new UnitList(n);
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
        return String.format("%s Turn:%d AP:%d units:%d casualties:%d", army, turn, actionPoints, units.size(), casualties.size());
    }

    public String getStats()
    {
        return String.format("%s\n%4d\n%4d\n%4d\n%4d\n%4d\n%4d", getName(), actionCount, unitsLeft(), withdrawed(), casualties(), wonEngagementCount, lostEngagementCount);
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

    public int withdrawed()
    {
        return withdrawed.size();
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

    public void unitWithdraw(Unit unit)
    {
        units.remove(unit);
        withdrawed.add(unit);
    }

    public int getAp()
    {
        return ((apSpent < actionPoints) ? (apSpent + 1) : apSpent);
    }

    public int getTurnDone()
    {
        return turn;
    }

    public int getCurrentTurn()
    {
        return (turn + 1);
    }

    public boolean apExhausted()
    {
        return (apSpent == actionPoints);
    }

    public boolean isDeploymentDone()
    {
        return (deploymentDone || (reinforcement.size() == 0));
    }

    public void burnDownOneAp()
    {
        RustAndDust.debug("Player", "burn down 1AP");
        apSpent += 1;
        actionCount += 1;
        if (apSpent > actionPoints) RustAndDust.debug("ERROR: spent too much AP, please report");
    }

    public void turnEnd()
    {
        if (deploymentDone)
            turn += 1;
        else
            deploymentDone = (reinforcement.size() == 0);
        for (Unit unit : units)
            unit.reset();
    }

    public void turnStart()
    {
        if (isDeploymentDone())
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

    public boolean promote(Unit unit)
    {
        for (Unit p: casualties) {
            if (p.isHqOf(unit)) {
                unit.promote();
                p.degrade();
                return true;
            }
        }

        return false;
    }
}
