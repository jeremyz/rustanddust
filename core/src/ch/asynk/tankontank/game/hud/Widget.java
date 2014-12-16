package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;

public abstract class Widget implements Disposable, Drawable
{
    public boolean blocked;
    public boolean visible;
    protected Rectangle rect;
    protected Position position;

    protected Widget()
    {
        this.blocked = false;
        this.visible = true;
        this.rect = new Rectangle(0, 0, 0, 0);
        this.position = Position.MIDDLE_CENTER;
    }

    public float getX() { return rect.x; }
    public float getY() { return rect.y; }
    public float getWidth() { return rect.width; }
    public float getHeight() { return rect.height; }

    public void setPosition(float x, float y)
    {
        rect.x = x;
        rect.y = y;
    }
    public void setPosition(Rectangle base)
    {
        rect.set(base);
    }

    public void setPosition(float x, float y, float w, float h)
    {
        rect.set(x, y, w, h);
    }

    public void setPosition(Position position)
    {
        this.position = position;
        rect.x = position.getX(rect.width);
        rect.y = position.getY(rect.height);
    }

    public boolean hit(float x, float y)
    {
        if (blocked || !visible) return false;
        return rect.contains(x, y);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        shapes.rect(rect.x, rect.y, rect.width, rect.height);
    }
}
