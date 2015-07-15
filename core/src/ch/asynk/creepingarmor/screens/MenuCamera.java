package ch.asynk.creepingarmor.screens;

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
    private int hudLeft;
    private int hudBottom;
    private int hudCorrection;

    private Matrix4 uiMatrix;
    private Matrix4 uiInvProjMatrix;

    public MenuCamera(int cx, int cy, int width, int height, int hudCorrection)
    {
        super(width, height);
        this.virtual = new Rectangle();
        this.virtual.set(cx, cy, width, height);
        this.virtualAspect = (virtual.width / virtual.height);
        this.window = new Rectangle();
        this.window.set(0, 0, 0, 0);
        this.position.set(virtual.x, virtual.y, 0f);
        this.hudLeft = 0;
        this.hudBottom = 0;
        this.hudCorrection = hudCorrection;

        this.uiMatrix = new Matrix4();
        this.uiInvProjMatrix = new Matrix4();
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

        window.width = screenWidth;
        window.height = screenHeight;
        hudLeft = hudCorrection;
        hudBottom = (int) (hudLeft / aspect);

        Gdx.gl.glViewport((int)window.x, (int)window.y, (int)window.width, (int)window.height);

        update(true);

        uiMatrix.setToOrtho2D(getHudLeft(), getHudBottom(), getHudWidth(), getHudHeight());
        uiInvProjMatrix.set(uiMatrix);
        Matrix4.inv(uiInvProjMatrix.val);
    }

    public float getScreenWidth()
    {
        return window.width;
    }

    public float getScreenHeight()
    {
        return window.height;
    }

    public int getHudLeft()
    {
        return hudLeft;
    }

    public int getHudBottom()
    {
        return hudBottom;
    }

    public int getHudWidth()
    {
        return (int) window.width - (2 * getHudLeft());
    }

    public int getHudHeight()
    {
        return (int) window.height - (2 * getHudBottom());
    }

    public void uiUnproject(float x, float y, Vector3 v)
    {
        x = x - window.x;
        y = Gdx.graphics.getHeight() - y - 1;
        y = y - window.y;
        v.x = (2 * x) / window.width - 1;
        v.y = (2 * y) / window.height - 1;
        v.z = 2 * v.z - 1;
        v.prj(uiInvProjMatrix);
    }

    public Matrix4 uiCombined()
    {
        return uiMatrix;
    }
}
