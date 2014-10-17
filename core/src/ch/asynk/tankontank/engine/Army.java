package ch.asynk.tankontank.engine;

public enum Army
{
    GE("German"),
    US("US"),
    USSR("Soviet"),
    EN("English");

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
