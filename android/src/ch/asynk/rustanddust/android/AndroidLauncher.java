package ch.asynk.rustanddust.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.Backend;

public class AndroidLauncher extends AndroidApplication
{
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new RustAndDust(new AndroidBackend(this.getApplication())), config);
    }
}
