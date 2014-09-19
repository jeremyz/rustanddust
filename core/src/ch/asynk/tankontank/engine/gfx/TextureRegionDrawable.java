package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRegionDrawable extends AbstractDrawable
{
    private TextureRegion region;

    public TextureRegionDrawable(TextureRegion region)
    {
        this.region = region;
        this.w = region.getRegionWidth();
        this.h = region.getRegionHeight();
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        batch.draw(region, x, y, (w / 2f), (h / 2f), w, h, s, s, r, true);
    }
}
