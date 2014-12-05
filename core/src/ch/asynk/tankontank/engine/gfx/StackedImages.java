package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class StackedImages implements Drawable, Disposable
{
    private boolean enabled[];
    private Array<Sprite> sprites;

    public StackedImages(TextureAtlas atlas)
    {
        this.sprites = atlas.createSprites();
        this.enabled = new boolean[sprites.size];
    }

    @Override
    public void dispose()
    {
    }

    public void disableAll()
    {
        for (int i = 0; i < sprites.size; i++)
            enabled[i] = false;
    }

    public void enable(int i, boolean enable)
    {
        enabled[i] = enable;
    }

    public boolean isEnabled(int i)
    {
        return enabled[i];
    }

    public boolean isEnabled()
    {
        for (int i = 0; i < sprites.size; i++)
            if (enabled[i]) return true;
        return false;
    }

    public void setAlpha(float alpha)
    {
        for (int i = 0, n = sprites.size; i < n; i++)
            sprites.get(i).setAlpha(alpha);
    }

    public void rotate(int i, float r)
    {
        sprites.get(i).setRotation(r);
    }

    public void setRotation(float r)
    {
        for (int i = 0, n = sprites.size; i < n; i++)
            sprites.get(i).setRotation(r);
    }

    public void translate(float dx, float dy)
    {
        for (int i = 0, n =  sprites.size; i < n; i++)
            sprites.get(i).translate(dx, dy);
    }

    public void centerOn(float cx, float cy)
    {
        for (int i = 0, n = sprites.size; i < n; i++) {
            float x = (cx - (sprites.get(i).getWidth() / 2f));
            float y = (cy - (sprites.get(i).getHeight() / 2f));
            sprites.get(i).setPosition(x, y);
        }
    }

    @Override
    public void draw(Batch batch)
    {
        for (int i = 0, n = sprites.size; i < n; i++) {
            if (enabled[i])
                sprites.get(i).draw(batch);
        }
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        Sprite sprite = sprites.get(0);
        float w = sprite.getWidth();
        float h = sprite.getHeight();
        shapes.rect(sprite.getX(), sprite.getY(), (w / 2f), (h / 2f), w, h, sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
    }
}
