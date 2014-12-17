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

    // private int screenWidth;
    // private int screenHeight;
    private float zoomOut;
    private float zoomIn;
    private float widthFactor;
    private float heightFactor;
    private Rectangle screen;
    private Matrix4 hudMatrix;
    private Matrix4 hudInvProjMatrix;

    public GameCamera(float virtualWidth, float virtualHeight, float zoomOut, float zoomIn)
    {
        super(virtualWidth, virtualHeight);
        this.zoomOut = zoomOut;
        this.zoomIn = zoomIn;
        this.screen = new Rectangle();
        this.hudMatrix = new Matrix4();
        this.hudInvProjMatrix = new Matrix4();
    }

    public void updateViewport(int screenWidth, int screenHeight)
    {
        // this.screenWidth = screenWidth;
        // this.screenHeight = screenHeight;

        float viewportAspect = (viewportWidth / viewportHeight);
        float aspect = (screenWidth / (float) screenHeight);

        if ((viewportAspect - aspect) < ZEROF) {
            screen.width = (screenHeight * viewportAspect);
            screen.height = screenHeight;
            screen.x = ((screenWidth - screen.width) / 2f);
            screen.y = 0f;
        } else {
            screen.width = screenWidth;
            screen.height = (screenWidth / viewportAspect);
            screen.x = 0f;
            screen.y = ((screenHeight - screen.height) / 2f);
        }

        Gdx.gl.glViewport((int)screen.x, (int)screen.y, (int)screen.width, (int)screen.height);

        this.widthFactor = (viewportWidth / screenWidth);
        this.heightFactor = (viewportHeight / screenHeight);

        clampZoom();
        update(true);
        hudMatrix.set(combined);
        hudMatrix.setToOrtho2D(0, 0, screen.width, screen.height);
        hudInvProjMatrix.set(hudMatrix);
        Matrix4.inv(hudInvProjMatrix.val);
    }

    public Matrix4 getHudMatrix()
    {
        return hudMatrix;
    }

    public int getHudWidth()
    {
        return (int) screen.width;
    }

    public int getHudHeight()
    {
        return (int) screen.height;
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
        update(true);
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
        System.err.println(String.format("  SCREEN: %d;%d %dx%d", (int)screen.x, (int)screen.y, (int)screen.width, (int)screen.height));
        System.err.println("MATRIX:" + combined.toString());
    }

    public void unproject(int x, int y, Vector3 v)
    {
        unproject(v.set(x, y, 0), screen.x, screen.y, screen.width, screen.height);
    }

    public void unprojectHud(float x, float y, Vector3 v)
    {
        x = x - screen.x;
        y = Gdx.graphics.getHeight() - y - 1;
        y = y - screen.y;
        v.x = (2 * x) / screen.width - 1;
        v.y = (2 * y) / screen.height - 1;
        v.z = 2 * v.z - 1;
        v.prj(hudInvProjMatrix);
    }
}
