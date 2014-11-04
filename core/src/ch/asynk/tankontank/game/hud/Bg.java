package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Drawable;

public class Bg implements Drawable, Disposable
{
    private TextureRegion region;
    protected Rectangle rect;

    public Bg(TextureRegion region)
    {
        this.region = region;
        this.rect = new Rectangle(0, 0, 0, 0);
    }

    public void set(float x, float y, float w, float h)
    {
        rect.x = x;
        rect.y = y;
        rect.width = w;
        rect.height = h;
    }

    public boolean hit(float x, float y)
    {
        return rect.contains(x, y);
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void draw(Batch batch)
    {
        batch.draw(region, rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        shapes.rect(rect.x, rect.y, rect.width, rect.height);
    }
}
