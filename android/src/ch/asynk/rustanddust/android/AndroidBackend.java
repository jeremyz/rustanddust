package ch.asynk.rustanddust.android;

import com.badlogic.gdx.Gdx;

import android.app.Application;
import android.content.Context;

import android.database.Cursor;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.provider.ContactsContract;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.List.ListElement;
import ch.asynk.rustanddust.util.Backend;
import ch.asynk.rustanddust.util.PlayerRecord;

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

    private void updateUsers(final RustAndDust game)
    {
        new Thread( new Runnable() {
            @Override
            public void run() {
                Cursor ccs = app.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        new String[] {
                            ContactsContract.Data.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Email.DATA1,
                        },
                        ContactsContract.CommonDataKinds.Email.DATA1 + " like '%gmail.com' and " + ContactsContract.CommonDataKinds.Email.DATA3 + " like 'Rustanddust'",
                        null, null);

                if (ccs == null) {
                    RustAndDust.debug("No Contacts found");
                    return;
                }

                PlayerRecord.clearList();
                RustAndDust.debug("Contacts :");
                while (ccs.moveToNext()) {
                    PlayerRecord r = PlayerRecord.get();
                    r.name = ccs.getString(0);
                    r.email = ccs.getString(1);
                    PlayerRecord.list.add(r);
                    RustAndDust.debug( "  " + r.toString());
                }

                Gdx.app.postRunnable( new Runnable() {
                    @Override
                    public void run() {
                        for (ListElement e : PlayerRecord.list) {
                            PlayerRecord r = (PlayerRecord) e;
                            game.db.storePlayer(r.name, r.email);
                        }
                        PlayerRecord.clearList();
                    }
                });
            }
        }).start();
    }

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

        updateUsers(game);
    }
}
