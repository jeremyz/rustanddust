package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class TextureDrawable extends AbstractDrawable
{
    private Texture texture;

    public TextureDrawable(Texture texture)
    {
        this.texture = texture;
        this.w = texture.getWidth();
        this.h = texture.getHeight();
    }

    @Override
    public void dispose()
    {
        texture.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        if (r != 0f)
            batch.draw(texture, x, y, (w / 2f), (h / 2f), w, h, s, s, r, (int) x, (int) y, (int) w, (int) h, false, false);
        else {
            if (s == 1f)
                batch.draw(texture, x, y);
            else
                batch.draw(texture, x, y, getWidth() * s, getHeight() * s);
        }
    }
}
