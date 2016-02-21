package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Drawable;

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
    public float getTop() { return rect.y + rect.height; }

    public void translate(float dx, float dy)
    {
        rect.x += dx;
        rect.y += dy;
    }

    public void setPosition(Rectangle r)
    {
        rect.set(r.x, r.x, r.width, r.height);
    }

    public void setPosition(float x, float y)
    {
        setPosition(x, y, rect.width, rect.height);
    }

    // override this if needed
    public void setPosition(float x, float y, float w, float h)
    {
        rect.set(x, y, w, h);
    }

    public void setPosition(Position position)
    {
        setPosition(position, this.parent);
    }

    public void setPosition(Position position, Widget parent)
    {
        this.position = position;
        setParent(parent);
    }

    public void setParent(Widget parent)
    {
        float x, y;
        this.parent = parent;
        if (parent == null) {
            x = position.getX(rect.width);
            y = position.getY(rect.height);
        } else {
            x = position.getX(parent, rect.width);
            y = position.getY(parent, rect.height);
        }
        setPosition(x, y);
    }

    public void setTopRight(Widget btn)
    {
        btn.setPosition((getX() + getWidth() - (btn.getWidth() * 0.666f)), (getTop() -  (btn.getHeight() * 0.666f)));
    }

    public void setTopLeft(Widget btn)
    {
        btn.setPosition((getX() - (btn.getWidth() * 0.333f)), (getTop() -  (btn.getHeight() * 0.666f)));
    }

    public void setBottomRight(Widget btn)
    {
        btn.setPosition((getX() + getWidth() - (btn.getWidth() * 0.666f)), (getY() -  (btn.getHeight() * 0.333f)));
    }

    public void setBottomLeft(Widget btn)
    {
        btn.setPosition((getX() - (btn.getWidth() * 0.333f)), (getY() -  (btn.getHeight() * 0.333f)));
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
