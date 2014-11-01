package ch.asynk.tankontank.game;

import java.util.Random;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Pawn;

public class Player extends ch.asynk.tankontank.engine.Player
{
    private static final float MOVE_TIME = 0.4f;

    private static Random rand = new Random();

    private int turn;
    private int apSpent;
    private int actionPoints;

    public Player(final TankOnTank game, Army army, int n)
    {
        super(army, n);
        this.turn = 0;
        this.actionPoints = 0;
    }

    public String toString()
    {
        return faction + " AP: " + actionPoints +
            " units:" + units.size() + " casualties:" + casualties.size();
    }

    public String getStatus()
    {
        return "Turn: " + turn + " AP: " + (apSpent + 1);
    }

    public int getTurn()
    {
        return turn;
    }

    public boolean apExhausted()
    {
        return (apSpent == actionPoints);
    }

    public void burnDownOneAp()
    {
        apSpent += 1;
        if (apSpent > actionPoints) System.err.println("ERROR: spent too much AP, please report");
    }

    @Override
    public void turnEnd()
    {
    }

    @Override
    public void turnStart()
    {
        turn += 1;
        for (Pawn pawn : units)
            pawn.reset();
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

    public boolean canPromote(Pawn pawn)
    {
        if (pawn.isHq()) return false;
        for (Pawn p: casualties)
            if (p.isHqOf(pawn)) return true;
        return false;
    }

    public Unit promote(Unit unit)
    {
        for (Pawn p: casualties) {
            if (p.isHqOf(unit)) {
                units.remove(unit);
                casualties.add(unit);
                units.add(p);
                casualties.remove(p);
                return (Unit) p;
            }
        }
        return null;
    }
}
