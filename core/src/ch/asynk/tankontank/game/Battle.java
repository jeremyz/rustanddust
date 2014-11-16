package ch.asynk.tankontank.game;

import ch.asynk.tankontank.engine.EntryPoint;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.hud.Position;

public interface Battle
{
    public String getName();

    public String getDescription();

    public Player getPlayer(boolean first, boolean deploymentPhase);

    public Map getMap();

    public Player checkVictory(Ctrl ctrl);

    public EntryPoint getEntryPoint(Unit unit);

    public Position getHudPosition(Player player);

    public State.StateType getState(Player player);

    public boolean deploymentDone(Player player);

    public void setup(Ctrl ctrl, Map map);
}
