package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class SpriteNode extends Sprite implements Node
{
    public SpriteNode(Texture texture)
    {
        super(texture);
    }

    public SpriteNode(TextureRegion region)
    {
        super(region);
    }

    @Override
    public void dispose()
    {
        // FIXME : what to do with dispose in SpriteNode
    }

    @Override
    public void moveBy(float dx, float dy)
    {
        translate(dx, dy);
    }

    @Override
    public void setCoords(float x, float y, float r)
    {
        setPosition(x, y);
        setRotation(r);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        shapes.rect(getX(), getY(), (getWidth() / 2f), (getHeight() / 2f), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
