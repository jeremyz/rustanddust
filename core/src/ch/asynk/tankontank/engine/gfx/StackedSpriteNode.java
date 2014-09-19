package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import ch.asynk.tankontank.engine.Layer;

public class StackedSpriteNode implements Node
{
    private Layer layer;
    private boolean enabled[];
    private Array<Sprite> sprites;

    public StackedSpriteNode(TextureAtlas atlas)
    {
        this.sprites = atlas.createSprites();
        this.enabled = new boolean[sprites.size];
    }

    public void enable(int i, boolean enable)
    {
        enabled[i] = enable;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    @Override
    public void clear()
    {
        // FIXME : clear StackedSpriteNode
    }

    @Override
    public void act(float delta) { }

    @Override
    public float getX()
    {
        return sprites.get(0).getX();
    }

    @Override
    public float getY()
    {
        return sprites.get(0).getY();
    }

    @Override
    public float getWidth()
    {
        return sprites.get(0).getWidth();
    }

    @Override
    public float getHeight()
    {
        return sprites.get(0).getHeight();
    }

    @Override
    public float getRotation()
    {
        return sprites.get(0).getRotation();
    }

    @Override
    public void setScale(float s)
    {
        for (int i = 0; i < sprites.size; i++)
            sprites.get(i).setScale(s);
    }

    @Override
    public void moveBy(float dx, float dy)
    {
        for (int i = 0; i < sprites.size; i++)
            sprites.get(i).translate(dx, dy);
    }

    @Override
    public void setRotation(float r)
    {
        for (int i = 0; i < sprites.size; i++)
            sprites.get(i).setRotation(r);
    }

    @Override
    public void setPosition(float x, float y)
    {
        for (int i = 0; i < sprites.size; i++)
            sprites.get(i).setPosition(x, y);
    }

    @Override
    public void setCoords(float x, float y, float r)
    {
        for (int i = 0; i < sprites.size; i++) {
            sprites.get(i).setPosition(x, y);
            sprites.get(i).setRotation(r);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        for (int i = 0; i < sprites.size; i++) {
            if (enabled[i])
                sprites.get(i).draw(batch, parentAlpha);
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
