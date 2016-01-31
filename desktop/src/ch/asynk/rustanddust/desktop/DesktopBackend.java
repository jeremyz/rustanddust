package ch.asynk.rustanddust.desktop;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.Backend;

public class DesktopBackend implements Backend
{
    private int me;
    private int opponent;

    public DesktopBackend()
    {
    }

    public int getMyId() { return me; }
    public int getOpponentId() { return opponent; }

    public void init(RustAndDust game)
    {
        me = game.db.storePlayerGetId("me", "myself", "I");
        opponent = game.db.storePlayerGetId("opponent", "other", "you");
    }
}
