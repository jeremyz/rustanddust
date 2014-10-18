package ch.asynk.tankontank.game.battles;

import java.util.Random;

import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Battle;

public abstract class BattleCommon implements Battle
{
    protected final static Random random = new Random();

    protected String name;
    protected String description;
    protected Army firstArmy;
    protected Army secondArmy;
    protected Factory factory;

    public BattleCommon(Factory factory)
    {
        this.factory = factory;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void randomizeArmies()
    {
        if (random.nextInt(2) == 0) {
            firstArmy = Army.US;
            secondArmy = Army.GE;
        } else {
            firstArmy = Army.GE;
            secondArmy = Army.US;
        }
    }
}
