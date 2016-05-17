package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Bg extends Widget
{
    private static float SCALE = 1.0f;
    private TextureRegion region;

    public Bg(TextureRegion region)
    {
        super();
        this.region = region;
        rect.set(0, 0, region.getRegionWidth() * SCALE, region.getRegionHeight() * SCALE);
    }

    public static void setScale(float scale)
    {
        SCALE = scale;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        batch.draw(region, rect.x, rect.y, rect.width, rect.height);
    }
}
