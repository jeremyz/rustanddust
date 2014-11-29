package ch.asynk.tankontank.engine.gfx.animations;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.Animation;

public class PromoteAnimation implements Animation, Drawable
{
    private static PromoteAnimation instance = new PromoteAnimation();

    private static final float DURATION = 0.3f;

    private static Sound sound;
    private static TextureRegion region;

    private float x;
    private float y;
    private float x1;
    private float y1;
    private float dx;
    private float dy;
    private float volume;
    private float elapsed;

    public static void init(TextureAtlas atlas, Sound snd)
    {
        region = atlas.findRegion("stars");
        sound = snd;
    }

    public static void free()
    {
        sound.dispose();
    }

    protected void PromoteAnimation()
    {
    }

    public static PromoteAnimation get(float x0, float y0, float x1, float y1, float v)
    {
        x0 = (x0 - (region.getRegionWidth() / 2.0f));
        y0 = (y0 - (region.getRegionHeight() / 2.0f));
        x1 = (x1 - (region.getRegionWidth() / 2.0f));
        y1 = (y1 - (region.getRegionHeight() / 2.0f));

        instance.volume = v;
        instance.x = x0;
        instance.y = y0;
        instance.x1 = x1;
        instance.y1 = y1;
        instance.dx = ((x1 - x0)/ DURATION);
        instance.dy = ((y1 - y0) / DURATION);
        instance.elapsed = 0f;

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
        if (elapsed >= DURATION) {
            x = x1;
            y = y1;
            sound.play(volume);
            return true;
        }

        x += (dx * delta);
        y += (dy * delta);

        return false;
    }

    @Override
    public void draw(Batch batch)
    {
        batch.draw(region, x, y);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        debugShapes.rect(x, y, region.getRegionWidth(), region.getRegionHeight());
    }
}
