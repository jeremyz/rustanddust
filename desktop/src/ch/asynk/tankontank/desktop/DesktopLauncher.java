package ch.asynk.tankontank.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ch.asynk.tankontank.TankOnTank;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Tank On Tank";
        config.width = 800;
        config.height = 626;
        new LwjglApplication(new TankOnTank(), config);
    }
}
