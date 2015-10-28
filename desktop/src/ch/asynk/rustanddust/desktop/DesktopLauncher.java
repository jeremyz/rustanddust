package ch.asynk.rustanddust.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.Files;
import ch.asynk.rustanddust.RustAndDust;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Rust And Dust";
        config.width = 1024;
        config.height = 768;
        // config.fullscreen = true;
        config.addIcon("data/icon.png", Files.FileType.Internal);
        new LwjglApplication(new RustAndDust(), config);
    }
}
