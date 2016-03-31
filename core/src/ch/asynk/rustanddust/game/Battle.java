package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.util.Marshal;
import ch.asynk.rustanddust.game.Factory.MapType;

public interface Battle extends Marshal
{
    public int getId();

    public String getName();

    public String getDescription();

    public Map getMap();

    public MapType getMapType();

    public Position getHudPosition();

    public int getTurnCount();

    public Player getPlayer();

    public Player getOpponent();

    public void init();

    public void desinit();

    public void initialDeployment();

    public boolean actionDone();

    public boolean turnDone();

    public boolean isDeploymentDone();

    public State.StateType getState();
}
