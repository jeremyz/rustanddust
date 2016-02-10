package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.Factory.MapType;

public interface Battle
{
    public int getId();

    public String getName();

    public String getDescription();

    public Map getMap();

    public MapType getMapType();

    public Position getHudPosition();

    public Player getPlayer();

    public Player getOpponent();

    public void init(Ctrl ctrl, int idA, int idB);

    public void init(Ctrl ctrl, String payload);

    public boolean actionDone();

    public boolean turnDone();

    public boolean isDeploymentDone();

    public boolean hasReinforcement();

    public State.StateType getState();

    public String unload();
}
