package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

public interface Drawable extends Disposable
{
    public float getWidth();

    public float getHeight();

    public float getX();

    public float getY();

    public   int getRotation();

    public void setRotation(int r);

    public void setScale(float s);

    public void setPosition(float x, float y);

    public void moveBy(float dx, float dy);

    public void setCoords(float x, float y, int r);

    public void draw(Batch batch, float parentAlpha);
}
