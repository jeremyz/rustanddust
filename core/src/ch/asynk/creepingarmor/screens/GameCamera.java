package ch.asynk.creepingarmor.screens;

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
    private float viewportAspect;
    private float widthFactor;
    private float heightFactor;
    private Rectangle virtual;
    private Rectangle window;
    private Rectangle hud;
    private Matrix4 hudMatrix;
    private Matrix4 hudInvProjMatrix;
    private int hudCorrection;
    private int hudLeft;
    private int hudBottom;

    public GameCamera(float virtualWidth, float virtualHeight, float zoomOut, float zoomIn, int hudCorrection)
    {
        super(virtualWidth, virtualHeight);
        this.zoomOut = zoomOut;
        this.zoomIn = zoomIn;
        this.viewportAspect = (viewportWidth / viewportHeight);
        this.virtual = new Rectangle();
        this.virtual.set(0, 0, virtualWidth, virtualHeight);
        this.window = new Rectangle();
        this.hud = new Rectangle();
        this.hudMatrix = new Matrix4();
        this.hudInvProjMatrix = new Matrix4();
        this.hudLeft = 0;
        this.hudBottom = 0;
        this.hudCorrection = hudCorrection;
    }

    public void updateViewport(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        float aspect = (screenWidth / (float) screenHeight);
        float diff = (viewportAspect - aspect);

        if (diff < -ZEROF) {
            // wider than tall
            window.width = java.lang.Math.min((screenHeight * viewportAspect / zoom), screenWidth);
            window.height = screenHeight;
            window.x = ((screenWidth - window.width) / 2f);
            window.y = 0f;
            viewportWidth = (viewportHeight * (window.width / window.height));
            hud.y = hudCorrection;
            hud.x = (hud.y * viewportWidth / viewportHeight);
        } else if (diff > ZEROF) {
            // taller than wide
            // FIXME fix hud vertical position
            window.width = screenWidth;
            window.height = java.lang.Math.min((screenWidth * viewportAspect / zoom), screenHeight);
            window.x = 0f;
            window.y = ((screenHeight - window.height) / 2f);
            viewportHeight = (viewportWidth * (window.height / window.width));
            hud.x = hudCorrection;
            hud.y = (hud.x / viewportWidth * viewportHeight);
        }

        hud.width = (window.width - (2 * hud.x));
        hud.height = (window.height - (2 * hud.y));

        widthFactor = (viewportWidth / screenWidth);
        heightFactor = (viewportHeight / screenHeight);

        clampPosition();
        update(true);
        hudMatrix.setToOrtho2D(hud.x, hud.y, hud.width, hud.height);
        hudInvProjMatrix.set(hudMatrix);
        Matrix4.inv(hudInvProjMatrix.val);

        Gdx.gl.glViewport((int)window.x, (int)window.y, (int)window.width, (int)window.height);
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

    public int getHudLeft()
    {
        return (int) hud.x;
    }

    public int getHudBottom()
    {
        return (int) hud.y;
    }

    public int getHudWidth()
    {
        return (int) hud.width;
    }

    public int getHudHeight()
    {
        return (int) hud.height;
    }

    public void centerOnWorld()
    {
        position.set((viewportWidth / 2f), (viewportHeight / 2f), 0f);
    }

    public void zoom(float dz)
    {
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
    }

    public void clampPosition()
    {
        float cx = (viewportWidth * zoom);
        float cy = (viewportHeight * zoom);

        if ((virtual.width - cx) > ZEROF) {
            cx /= 2f;
            position.x = MathUtils.clamp(position.x, cx, (virtual.width - cx));
        } else
            position.x = (virtual.width / 2f);

        if ((virtual.height - cy) > ZEROF) {
            cy /= 2f;
            position.y = MathUtils.clamp(position.y, cy, (virtual.height - cy));
        } else
            position.y = (virtual.height / 2f);
    }

    public void debug()
    {
        System.err.println(String.format(" VIEWPORT: %dx%d * %.2f -> %dx%d", (int)viewportWidth, (int)viewportHeight,
                zoom, (int)(viewportWidth * zoom), (int)(viewportHeight * zoom)));
        System.err.println(String.format("   WINDOW: %d;%d %dx%d", (int)window.x, (int)window.y, (int)window.width, (int)window.height));
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
