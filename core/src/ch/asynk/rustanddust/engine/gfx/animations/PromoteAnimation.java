package ch.asynk.rustanddust.engine.gfx.animations;

import java.lang.Math;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Drawable;
import ch.asynk.rustanddust.engine.gfx.Animation;

public class PromoteAnimation implements Animation, Drawable
{
    private static PromoteAnimation instance = new PromoteAnimation();

    private static final float DURATION = 0.5f;
    private static final float MAX_SCALE = 2f;

    private static Sound usSound;
    private static Sound geSound;
    private static Sound snd;
    private static TextureRegion region;

    private float x0;
    private float y0;
    private float x;
    private float y;
    private float scale;
    private float step;
    private float elapsed;

    public static void init(AtlasRegion r, Sound usSnd, Sound geSnd)
    {
        region = r;
        usSound = usSnd;
        geSound = geSnd;
    }

    public static void free()
    {
    }

    protected void PromoteAnimation()
    {
    }

    public static PromoteAnimation get(boolean us, float x, float y, float v)
    {
        x = (x - (region.getRegionWidth() / 2.0f));
        y = (y - (region.getRegionHeight() / 2.0f));

        instance.x0 = x;
        instance.y0 = y;
        instance.scale = 0f;
        instance.elapsed = 0f;
        (us ? usSound : geSound).play(v);

        return instance;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public boolean animate(float delta)
    {
        elapsed += delta;
        if (elapsed >= DURATION)
            return true;

        float s = MAX_SCALE * (float) Math.sin(Math.PI / DURATION * elapsed);
        scale = 1f + s;
        x = x0 - ((region.getRegionWidth() * scale) / 4f);
        y = y0 - ((region.getRegionHeight() * scale) / 4f);

        return false;
    }

    @Override
    public void draw(Batch batch)
    {
        batch.draw(region, x, y, 0, 0, region.getRegionWidth(), region.getRegionHeight(), scale, scale, 0f);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        debugShapes.rect(x, y, region.getRegionWidth(), region.getRegionHeight());
    }
}
