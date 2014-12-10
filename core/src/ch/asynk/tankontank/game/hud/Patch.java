package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public class Patch extends Widget
{
    private NinePatch patch;

    public Patch(NinePatch patch)
    {
        super();
        this.patch = patch;
        set(0, 0, patch.getTotalWidth(), patch.getTotalHeight());
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        patch.draw(batch, rect.x, rect.y, rect.width, rect.height);
    }
}
