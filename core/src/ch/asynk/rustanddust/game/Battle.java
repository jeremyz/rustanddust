package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.battles.Factory.MapType;

public interface Battle
{
    public String getName();

    public String getDescription();

    public MapType getMapType();

    public Player getPlayer();

    public Player getOpponent();

    public Position getHudPosition();

    public Map setup();

    public void start();

    public boolean actionDone();

    public boolean turnDone();

    public boolean isDeploymentDone();

    public boolean hasReinforcement();

    public State.StateType getState();

    public Zone getEntryZone(Unit unit);

    public Zone getExitZone(Unit unit);
}
