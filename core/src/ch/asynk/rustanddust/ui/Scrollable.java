package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public class Scrollable extends Widget
{
    private float offset;
    private Widget child;
    private NinePatch patch;
    private Rectangle clip;

    public Scrollable(Widget child)
    {
        this(child, null);
    }

    public Scrollable(Widget child, NinePatch patch)
    {
        super();
        this.offset = 0f;
        this.padding = 10f;
        this.child = child;
        this.patch = patch;
        this.clip = new Rectangle();
        if (patch != null)
            setPosition(0, 0, patch.getTotalWidth(), patch.getTotalHeight());
    }

    public Widget getChild() { return child; }

    @Override
    public boolean hit(float x, float y)
    {
        if (!super.hit(x, y)) return false;
        child.hit(x, y);
        return true;
    }

    public float getBestWidth()
    {
        return (child.getWidth() + (2 * padding));
    }

    public boolean drag(float x, float y, int dx, int dy)
    {
        child.translate(0, dy);
        if (child.getY() > (getY() + padding))
            child.rect.y = (getY() + padding);
        if (child.getTop() < (getTop() - padding))
            child.rect.y = (getTop() - padding - child.getHeight());
        return true;
    }

    @Override
    public void dispose()
    {
        child.dispose();
    }

    @Override
    public void setPosition(float x, float y, float w, float h)
    {
        rect.set(x, y, w, h);
        child.setPosition((x + padding), (getTop() - padding - child.getHeight()));
        clip.set((getX() + padding), (getY() + padding), (getWidth() - (2 * padding)), (getHeight() - (2 * padding)));
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;

        if (patch != null)
            patch.draw(batch, rect.x, rect.y, rect.width, rect.height);
        batch.flush();
        HdpiUtils.glScissor((int) clip.x, (int) clip.y, (int) clip.width, (int) clip.height);
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        child.draw(batch);
        batch.flush();
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }
}
