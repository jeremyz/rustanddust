package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.RustAndDust;

public class Player
{
    private static final float MOVE_TIME = 0.4f;
    private static final int N = 10;

    private int id;
    private int turn;
    private int apSpent;
    private int ap;

    public Army army;
    public UnitList units;
    public UnitList casualties;
    public UnitList reinforcement;
    public UnitList withdrawed;

    public int actionCount;
    public int objectivesWon;
    public int engagementWon;
    public int engagementLost;

    public Player(int id, Army army)
    {
        this.id = id;
        this.army = army;
        this.units = new UnitList(N);
        this.casualties = new UnitList(N);
        this.reinforcement = new UnitList(N);
        this.withdrawed = new UnitList(N);
        this.turn = 0;
        this.apSpent = 0;
        this.ap = 0;
        this.actionCount = 0;
        this.objectivesWon = 0;
        this.engagementWon = 0;
        this.engagementLost = 0;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return army.toString();
    }

    public String toString()
    {
        return String.format("%s Turn:%d AP:%d/%d units:%d casualties:%d", army, turn, apSpent, ap, units.size(), casualties.size());
    }

    public String getStats()
    {
        return String.format("%s\n%4d\n%4d\n%4d\n%4d\n%4d", getName(), actionCount, unitsLeft(), withdrawed(), casualties(), objectivesWon);
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

    public boolean hasReinforcement()
    {
        return (reinforcement() > 0);
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

    public void addReinforcement(Unit unit)
    {
        reinforcement.add(unit);
    }

    public void unitEntry(Unit unit)
    {
        reinforcement.remove(unit);
        addUnit(unit);
    }

    public void revertUnitEntry(Unit unit)
    {
        removeUnit(unit);
        reinforcement.add(unit);
    }

    public void casualty(Unit unit)
    {
        removeUnit(unit);
        casualties.add(unit);
    }

    public void unitWithdraw(Unit unit)
    {
        removeUnit(unit);
        withdrawed.add(unit);
    }

    private void addUnit(Unit unit)
    {
        units.add(unit);
    }

    private void removeUnit(Unit unit)
    {
        units.remove(unit);
    }

    public int getAp()
    {
        return ((apSpent < ap) ? (apSpent + 1) : apSpent);
    }

    public int getTurn()
    {
        return turn;
    }

    public boolean apExhausted()
    {
        return (apSpent == ap);
    }

    public boolean canDoSomething()
    {
        if (reinforcement() > 0)
            return true;
        for (Unit unit : units) {
            if (unit.canMove() || unit.canEngage() || canPromote(unit))
                return true;
        }
        return false;
    }

    public boolean isDeploymentDone()
    {
        return ((turn > 0) || !hasReinforcement());
    }

    public void burnDownOneAp()
    {
        apSpent += 1;
        actionCount += 1;
        RustAndDust.debug("Player", String.format("%d/%d - %d", apSpent, ap, actionCount));
        if (apSpent > ap) RustAndDust.debug("ERROR: spent too much AP, please report");
    }

    public void turnEnd()
    {
        apSpent = ap;
        for (Unit unit : units)
            unit.reset();
    }

    public void turnStart(int aps)
    {
        if (isDeploymentDone()) {
            ap = aps;
            apSpent = 0;
            turn += 1;
        }
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
