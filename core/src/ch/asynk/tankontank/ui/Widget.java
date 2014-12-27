package ch.asynk.tankontank.ui;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;

public abstract class Widget implements Disposable, Drawable
{
    public boolean blocked;
    public boolean visible;
    protected float padding;
    protected Rectangle rect;
    protected Position position;
    protected Widget parent;

    protected Widget()
    {
        this.parent = null;
        this.blocked = false;
        this.visible = true;
        this.padding = 0f;
        this.rect = new Rectangle(0, 0, 0, 0);
        this.position = Position.MIDDLE_CENTER;
    }

    public float getX() { return rect.x; }
    public float getY() { return rect.y; }
    public float getWidth() { return rect.width; }
    public float getHeight() { return rect.height; }

    public void translate(float dx, float dy)
    {
        rect.x += dx;
        rect.y += dy;
    }

    public void setPosition(Rectangle r)
    {
        rect.set(r);
    }

    public void setPosition(float x, float y)
    {
        rect.x = x;
        rect.y = y;
    }

    public void setPosition(float x, float y, float w, float h)
    {
        rect.set(x, y, w, h);
    }

    public void setPosition(Position position)
    {
        this.position = position;
        setParent(this.parent);
    }

    public void setPosition(Position position, Widget parent)
    {
        this.position = position;
        setParent(parent);
    }

    public void setParent(Widget parent)
    {
        this.parent = parent;
        if (parent == null) {
            rect.x = position.getX(rect.width);
            rect.y = position.getY(rect.height);
        } else {
            rect.x = position.getX(parent, rect.width);
            rect.y = position.getY(parent, rect.height);
        }
        // might trigger something if overriden
        setPosition(rect.x, rect.y);
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
