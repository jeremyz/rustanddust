package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.Factory.MapType;

public interface Battle
{
    public int getId();

    public String getName();

    public String getDescription();

    public MapType getMapType();

    public Player getPlayer();

    public Player getOpponent();

    public Position getHudPosition();

    public State.StateType setup(Ctrl ctrl);

    public boolean actionDone();

    public boolean turnDone();

    public boolean isDeploymentDone();

    public boolean hasReinforcement();

    public State.StateType getState();

    public Zone getEntryZone(Unit unit);

    public Zone getExitZone(Unit unit);
}
