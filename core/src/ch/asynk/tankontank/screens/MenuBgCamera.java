package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

public class MenuBgCamera extends OrthographicCamera
{
    private static final float ZEROF = 0.01f;

    private float virtualAspect;
    private final Rectangle virtual;
    private final Rectangle screen;

    public MenuBgCamera(int cx, int cy, int width, int height)
    {
        super(width, height);
        this.virtual = new Rectangle();
        this.virtual.set(cx, cy, width, height);
        this.virtualAspect = (virtual.width / virtual.height);
        this.screen = new Rectangle();
        this.screen.set(0, 0, 0, 0);
        this.position.set(virtual.x, virtual.y, 0f);
    }

    public void updateViewport(int screenWidth, int screenHeight)
    {
        float aspect = (screenWidth / (float) screenHeight);
        float diff = (virtualAspect - aspect);

        if (diff < -ZEROF) {
            viewportWidth = (virtual.height * aspect);
            viewportHeight = virtual.height;
        } else if (diff > ZEROF) {
            viewportWidth = virtual.width;
            viewportHeight = (virtual.width / aspect);
        }

        screen.width= screenWidth;
        screen.height= screenHeight;

        Gdx.gl.glViewport((int)screen.x, (int)screen.y, (int)screen.width, (int)screen.height);

        update(true);
    }
}
