package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;

public class GameCamera extends OrthographicCamera
{
    private static final float ZEROF = 0.01f;

    private int screenWidth;
    private int screenHeight;
    private float zoomOut;
    private float zoomIn;
    private float widthFactor;
    private float heightFactor;
    private Rectangle window;
    private Matrix4 hudMatrix;
    private Matrix4 hudInvProjMatrix;

    public GameCamera(float virtualWidth, float virtualHeight, float zoomOut, float zoomIn)
    {
        super(virtualWidth, virtualHeight);
        this.zoomOut = zoomOut;
        this.zoomIn = zoomIn;
        this.window = new Rectangle();
        this.hudMatrix = new Matrix4();
        this.hudInvProjMatrix = new Matrix4();
    }

    public void updateViewport(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        float viewportAspect = (viewportWidth / viewportHeight);
        float aspect = (screenWidth / (float) screenHeight);
        float diff = (viewportAspect - aspect);

        if (diff < -ZEROF) {
            window.width = (screenHeight * viewportAspect);
            window.height = screenHeight;
            window.x = ((screenWidth - window.width) / 2f);
            window.y = 0f;
        } else if (diff > ZEROF) {
            window.width = screenWidth;
            window.height = (screenWidth / viewportAspect);
            window.x = 0f;
            window.y = ((screenHeight - window.height) / 2f);
        }

        Gdx.gl.glViewport((int)window.x, (int)window.y, (int)window.width, (int)window.height);

        this.widthFactor = (viewportWidth / screenWidth);
        this.heightFactor = (viewportHeight / screenHeight);

        clampZoom();
        update(true);
        hudMatrix.set(combined);
        hudMatrix.setToOrtho2D(0, 0, window.width, window.height);
        hudInvProjMatrix.set(hudMatrix);
        Matrix4.inv(hudInvProjMatrix.val);
    }

    public Matrix4 getHudMatrix()
    {
        return hudMatrix;
    }

    public int getScreenWidth()
    {
        return screenWidth;
    }

    public int getScreenHeight()
    {
        return screenHeight;
    }

    public int getHudWidth()
    {
        return (int) window.width;
    }

    public int getHudHeight()
    {
        return (int) window.height;
    }

    public void centerOnWorld()
    {
        position.set((viewportWidth / 2f), (viewportHeight / 2f), 0f);
    }

    public void zoom(float dz)
    {
        // TODO adapt screen -> glViewport
        zoom += dz;
        clampZoom();
        updateViewport(screenWidth, screenHeight);
    }

    public void translate(float dx, float dy)
    {
        float deltaX = (dx * zoom * widthFactor);
        float deltaY = (dy * zoom * heightFactor);
        translate(deltaX, -deltaY, 0);
        clampPosition();
        update(true);
    }

    public void clampZoom()
    {
        zoom = MathUtils.clamp(zoom, zoomIn, zoomOut);
        clampPosition();
    }

    public void clampPosition()
    {
        float cx = (viewportWidth * zoom);
        float cy = (viewportHeight * zoom);

        if ((viewportWidth - cx) > ZEROF) {
            cx /= 2f;
            position.x = MathUtils.clamp(position.x, cx, (viewportWidth - cx));
        } else
            position.x = (viewportWidth / 2f);

        if ((viewportHeight - cy) > ZEROF) {
            cy /= 2f;
            position.y = MathUtils.clamp(position.y, cy, (viewportHeight - cy));
        } else
            position.y = (viewportHeight / 2f);
    }

    public void debug()
    {
        System.err.println(String.format("VIEWPORT: %dx%d", (int)viewportWidth, (int)viewportHeight));
        System.err.println(String.format("  SCREEN: %d;%d %dx%d", (int)window.x, (int)window.y, (int)window.width, (int)window.height));
        System.err.println("MATRIX:" + combined.toString());
    }

    public void unproject(int x, int y, Vector3 v)
    {
        unproject(v.set(x, y, 0), window.x, window.y, window.width, window.height);
    }

    public void unprojectHud(float x, float y, Vector3 v)
    {
        x = x - window.x;
        y = Gdx.graphics.getHeight() - y - 1;
        y = y - window.y;
        v.x = (2 * x) / window.width - 1;
        v.y = (2 * y) / window.height - 1;
        v.z = 2 * v.z - 1;
        v.prj(hudInvProjMatrix);
    }
}
