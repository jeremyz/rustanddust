package ch.asynk.tankontank.game;

import ch.asynk.tankontank.engine.TileSet;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Player;

public interface Battle
{
    public String getName();

    public String getDescription();

    public Map getMap();

    public Player getFirstPlayer();

    public Player getSecondPlayer();

    public void setup(Map map, Player firstPlayer, Player secondPlayer);

    public boolean checkVictory(Ctrl ctrl);

    public TileSet getEntryPoint(Unit unit);

    public Orientation getEntryOrientation(Player player);
}
