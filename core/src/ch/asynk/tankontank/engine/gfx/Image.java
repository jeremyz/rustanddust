package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Image extends Sprite implements Drawable, Disposable
{
    public boolean visible ;
    private Texture texture;

    protected Image()
    {
    }

    public Image(Texture texture)
    {
        super(texture);
        this.texture = texture;
        this.visible = true;
    }

    public Image(TextureRegion region)
    {
        super(region);
        this.texture = null;
        this.visible = true;
    }

    @Override
    public void dispose()
    {
        if (texture != null) texture.dispose();
    }

    public boolean hit(float x, float y)
    {
        if (!visible) return false;
        return ((x >= getX()) && (y >= getY()) && (x <= (getX() + getWidth())) && (y <= (getY() + getHeight())));
    }

    public void setPosition(float x, float y, float r)
    {
        setPosition(x, y);
        setRotation(r);
    }

    public void centerOn(float cx, float cy)
    {
        setPosition((cx - (getWidth() / 2f)), (cy - (getHeight() / 2f)));
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        shapes.rect(getX(), getY(), (getWidth() / 2f), (getHeight() / 2f), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
