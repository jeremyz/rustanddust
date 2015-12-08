package ch.asynk.rustanddust.game;

import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.battles.Factory.MapType;
import ch.asynk.rustanddust.ui.Position;

public interface Battle
{
    public String getName();

    public String getDescription();

    public Player getPlayer();

    public Player getOpponent();

    public MapType getMapType();

    public void changePlayer();

    public Player checkVictory(Ctrl ctrl);

    public boolean getReinforcement(Ctrl ctrl);

    public Zone getEntryZone(Unit unit);

    public Zone getExitZone(Unit unit);

    public Position getHudPosition();

    public State.StateType getState();

    public boolean deploymentDone();

    public Map setup(Ctrl ctrl);
}
