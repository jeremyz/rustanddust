package ch.asynk.tankontank.game;

public enum Army
{
    GE("German"), US("US");
    private String s;
    Army(String s) { this.s = s; }

    public boolean isEnemy(Army other)
    {
        return (this != other);
    }

    public String toString()
    {
        return s;
    }
}
