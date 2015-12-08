package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.battles.Factory.MapType;

public interface Battle
{
    public String getName();

    public String getDescription();

    public Player getPlayer();

    public Player getOpponent();

    public MapType getMapType();

    public void changePlayer();

    public Player getVictor();

    public boolean hasReinforcement();

    public Zone getEntryZone(Unit unit);

    public Zone getExitZone(Unit unit);

    public Position getHudPosition();

    public State.StateType getState();

    public boolean deploymentDone();

    public Map setup();
}
