package ch.asynk.creepingarmor.engine;

public class Attack
{
    public Pawn attacker;
    public Pawn target;
    public int distance;
    public boolean isClear;
    public boolean isFlank;

    public Attack(Pawn attacker)
    {
        this.attacker = attacker;
    }

    public String toString()
    {
        return String.format("attack : %s -> %s dist:%d clear:%b flank:%b", attacker, target, distance, isClear, isFlank);
    }

    public void reset()
    {
        target = null;
        distance = 0;;
        isClear = false;
        isFlank = false;
    }
}
