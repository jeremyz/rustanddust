package ch.asynk.tankontank.game;

public interface Battle
{
    public String getName();

    public String getDescription();

    public Map getMap();

    public Player getFirstPlayer();

    public Player getSecondPlayer();

    public void setup(Map map, Player firstPlayer, Player secondPlayer);

    public boolean checkVictory(Ctrl ctrl);
}
