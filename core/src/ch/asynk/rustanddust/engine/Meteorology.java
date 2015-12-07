package ch.asynk.rustanddust.engine;

public class Meteorology
{
    public enum Day { DAY, NIGHT };
    public enum Season { SUMMER, SPRING, WINTER, FALL };
    public enum Weather { CLEAR, RAIN, SNOW, WIND };

    public Day day;
    public Season season;
    public Weather weather;

    public Meteorology()
    {
        clear();
    }

    public boolean isNight()
    {
        return (this.day == Day.NIGHT);
    }

    public void clear()
    {
        day = Day.DAY;
        season = Season.SUMMER;
        weather = Weather.CLEAR;
    }
}
