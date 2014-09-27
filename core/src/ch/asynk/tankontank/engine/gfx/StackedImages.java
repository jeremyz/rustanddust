package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class StackedImages implements BasicDrawable
{
    private boolean enabled[];
    private Array<Sprite> sprites;

    public StackedImages(TextureAtlas atlas)
    {
        this.sprites = atlas.createSprites();
        this.enabled = new boolean[sprites.size];
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

    public boolean isEnabled()
    {
        for (int i = 0; i < sprites.size; i++)
            if (enabled[i]) return true;
        return false;
    }

    @Override
    public void translate(float dx, float dy)
    {
        for (int i = 0; i < sprites.size; i++) {
            sprites.get(i).translate(dx, dy);
        }
    }

    @Override
    public void setPosition(float x, float y, float r)
    {
        for (int i = 0; i < sprites.size; i++) {
            sprites.get(i).setPosition(x, y);
            sprites.get(i).setRotation(r);
        }
    }

    @Override
    public void draw(Batch batch)
    {
        for (int i = 0; i < sprites.size; i++) {
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
