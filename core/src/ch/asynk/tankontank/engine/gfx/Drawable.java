package ch.asynk.tankontank.engine.gfx;

public interface Drawable extends BasicDrawable
{
    public float getX();

    public float getY();

    public float getWidth();

    public float getHeight();

    public float getRotation();

    public void setScale(float s);

    public void setRotation(float r);

    public void translate(float dx, float dy);

    public void setPosition(float x, float y);
}
