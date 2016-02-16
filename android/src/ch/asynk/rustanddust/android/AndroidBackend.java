package ch.asynk.rustanddust.android;

import android.app.Application;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.util.Backend;

public class AndroidBackend implements Backend
{
    private int me;
    private int opponent;
    private Application app;

    public AndroidBackend(Application app)
    {
        this.app = app;
    }

    public int getMyId() { return me; }
    public int getOpponentId() { return opponent; }

    public void init(RustAndDust game)
    {
        me = game.db.storePlayerGetId("me", "myself");
        opponent = game.db.storePlayerGetId("opponent", "other");
    }
}
