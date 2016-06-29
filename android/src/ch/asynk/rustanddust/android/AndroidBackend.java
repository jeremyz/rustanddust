package ch.asynk.rustanddust.android;

import android.app.Application;
import android.content.Context;

import android.accounts.Account;
import android.accounts.AccountManager;

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

    @Override
    public void init(final RustAndDust game)
    {
        AccountManager aMgr = (AccountManager) app.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = aMgr.getAccountsByType("com.google");
        for (Account account : accounts)
            RustAndDust.debug("account : " + account.toString());
        if (accounts.length > 0)
            me = game.db.storePlayerGetId(accounts[0].name.toString(), "myself");
        else
            me = game.db.storePlayerGetId("me", "myself");
        opponent = me;
    }
}
