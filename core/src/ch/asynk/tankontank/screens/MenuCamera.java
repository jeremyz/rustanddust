package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class MenuCamera extends OrthographicCamera
{
    private static final float ZEROF = 0.01f;

    private float virtualAspect;
    private final Rectangle virtual;
    private final Rectangle window;
    private final OrthographicCamera uiCamera;

    public MenuCamera(int cx, int cy, int width, int height)
    {
        super(width, height);
        this.virtual = new Rectangle();
        this.virtual.set(cx, cy, width, height);
        this.virtualAspect = (virtual.width / virtual.height);
        this.window = new Rectangle();
        this.window.set(0, 0, 0, 0);
        this.position.set(virtual.x, virtual.y, 0f);
        this.uiCamera = new OrthographicCamera();
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

        window.width= screenWidth;
        window.height= screenHeight;

        Gdx.gl.glViewport((int)window.x, (int)window.y, (int)window.width, (int)window.height);

        update(true);
        uiCamera.setToOrtho(false, screenWidth, screenHeight);
    }

    public float getScreenWidth()
    {
        return window.width;
    }

    public float getScreenHeight()
    {
        return window.height;
    }

    public Vector3 uiUnproject(float x, float y, Vector3 v)
    {
        return uiCamera.unproject(v.set(x, y, 0f));
    }

    public Matrix4 uiCombined()
    {
        return uiCamera.combined;
    }
}
