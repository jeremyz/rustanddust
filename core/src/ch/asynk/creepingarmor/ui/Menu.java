package ch.asynk.creepingarmor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public class Menu extends Patch
{
    public static int PADDING = 40;
    public static int VSPACING = 8;

    protected Label []labels;

    public interface MenuItem
    {
        public int last();
        public int i();
    };

    protected MenuItem menuItem;

    public Menu(MenuItem menuItem, BitmapFont font, NinePatch ninePatch)
    {
        super(ninePatch);
        this.menuItem = menuItem;
        this.labels = new Label[menuItem.last()];
        for (int i = 0; i< menuItem.last(); i ++)
            labels[i] = new Label(font, 10);
    }

    protected Label label(MenuItem m)
    {
        return labels[m.i()];
    }

    protected float widestLabel()
    {
        float w = 0f;
        for (int i = 0; i< menuItem.last(); i ++) {
            float t = labels[i].getWidth();
            if (t> w)
                w = t;
        }
        return w;
    }

    protected float highestLabel()
    {
        float h = 0f;
        for (int i = 0; i< menuItem.last(); i ++) {
            float t = labels[i].getHeight();
            if (t> h)
                h = t;
        }
        return h;
    }

    public void setPosition()
    {
        float lh = highestLabel();
        float h = ((menuItem.last() * lh) + (2 * PADDING) + ((menuItem.last() - 1) * VSPACING));
        float w = (widestLabel() + (2 * PADDING));
        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        y += PADDING;
        x += PADDING;
        float dy = (VSPACING + lh);

        for (int i = 0; i< menuItem.last(); i ++) {
            labels[i].setPosition(x, y);
            y += dy;
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();
        for (int i = 0; i < menuItem.last(); i ++)
            labels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        for (int i = 0; i < menuItem.last(); i ++)
            labels[i].draw(batch);
    }
}
