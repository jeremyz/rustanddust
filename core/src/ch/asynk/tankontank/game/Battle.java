package ch.asynk.tankontank.game;

import ch.asynk.tankontank.game.Zone;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.battles.Factory.MapType;
import ch.asynk.tankontank.ui.Position;

public interface Battle
{
    public String getName();

    public String getDescription();

    public Player getPlayer();

    public Player opponent(Player player);

    public MapType getMapType();

    public Map getMap();

    public Player checkVictory(Ctrl ctrl);

    public boolean getReinforcement(Ctrl ctrl, Map map);

    public Zone getEntryZone(Unit unit);

    public Zone getExitZone(Unit unit);

    public Position getHudPosition(Player player);

    public State.StateType getState(Player player);

    public boolean deploymentDone(Player player);

    public void setup(Ctrl ctrl, Map map);
}
