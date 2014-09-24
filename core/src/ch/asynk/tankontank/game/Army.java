package ch.asynk.tankontank.game;

public enum Army
{
    GE, US;

    public boolean isEnemy(Army other)
    {
        return (this != other);
    }
}
