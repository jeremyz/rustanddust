package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Image extends Sprite implements Moveable, Disposable
{
    private Texture texture;

    protected Image()
    {
    }

    public Image(Texture texture)
    {
        super(texture);
        this.texture = texture;
    }

    public Image(TextureRegion region)
    {
        super(region);
        this.texture = null;
    }

    @Override
    public void dispose()
    {
        if (texture != null) texture.dispose();
    }

    @Override
    public void setPosition(float x, float y, float r)
    {
        setPosition(x, y);
        setRotation(r);
    }

    @Override
    public void centerOn(float cx, float cy)
    {
        setPosition((cx - (getWidth() / 2f)), (cy - (getHeight() / 2f)));
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        shapes.rect(getX(), getY(), (getWidth() / 2f), (getHeight() / 2f), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
