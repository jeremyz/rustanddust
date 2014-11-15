package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Bg extends Widget
{
    private TextureRegion region;

    public Bg(TextureRegion region)
    {
        super();
        this.region = region;
        set(0, 0, region.getRegionWidth(), region.getRegionHeight());
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
