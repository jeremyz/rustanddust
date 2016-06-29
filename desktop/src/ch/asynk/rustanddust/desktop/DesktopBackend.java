package ch.asynk.rustanddust.desktop;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.util.Backend;

public class DesktopBackend implements Backend
{
    private int me;
    private int opponent;

    public DesktopBackend()
    {
    }

    public int getMyId() { return me; }
    public int getOpponentId() { return opponent; }

    public void init(final RustAndDust game)
    {
        me = game.db.storePlayerGetId("me", "myself");
        opponent = me;
    }
}
