package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.Color;

public abstract class AbstractDrawable implements Drawable
{
    protected float w;
    protected float h;
    protected float x;
    protected float y;
    protected int r;
    protected float s;
    protected Color color;

    public AbstractDrawable()
    {
        this.w = 0f;
        this.h = 0f;
        this.x = 0f;
        this.y = 0f;
        this.r = 0;
        this.s = 1f;
        this.color = new Color(1, 1, 1, 1);
    }

    @Override
    public float getWidth()
    {
        return w;
    }

    @Override
    public float getHeight()
    {
        return h;
    }

    @Override
    public float getX()
    {
        return x;
    }

    @Override
    public float getY()
    {
        return y;
    }

    @Override
    public int getRotation()
    {
        return r;
    }

    @Override
    public void setRotation(int r)
    {
        this.r = r;
    }

    @Override
    public void setScale(float s)
    {
        this.s = s;
    }

    @Override
    public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void moveBy(float dx, float dy)
    {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public void setCoords(float x, float y, int r)
    {
        this.x = x;
        this.y = y;
        this.r = r;
    }
}
