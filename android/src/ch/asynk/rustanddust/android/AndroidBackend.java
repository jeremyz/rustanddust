package ch.asynk.rustanddust.android;

import android.app.Application;

import ch.asynk.rustanddust.Backend;

public class AndroidBackend implements Backend
{
    private Application app;

    public AndroidBackend(Application app)
    {
        this.app = app;
    }
}
