package ch.asynk.rustanddust.engine.gfx.animations;

import java.util.Random;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Drawable;
import ch.asynk.rustanddust.engine.gfx.Animation;

public class InfantryFireAnimation implements Disposable, Animation, Pool.Poolable
{
    class Shot
    {
        public TextureRegion fireRegion;
        public float fire_a;
        public float fire_x;
        public float fire_y;
        public float fire_w;
        public float fire_dx;
        public float fire_dy;
        public float fire_dw;

        public boolean fired;
        public boolean hit;
        public boolean completed;

        public float fire_time;
        public float hit_time;
        public float end_time;

        public int hit_frame;

        public Shot(TextureRegion region)
        {
            this.fireRegion = region;
        }

        public void set(float delay, float x0, float y0, float x1, float y1, float w, float a)
        {
            float dx = (x1 - x0);
            float dy = (y1 - y0);

            // timing
            float fire_duration = ((FireAnimation.random.nextFloat() * TIME_SCATTERING) + (w / SHOT_SPEED));
            float hit_duration = (FireAnimation.infantryFire.rows * HIT_FRAME_DURATION);

            this.fired = false;
            this.fire_time = delay;
            this.hit_time = (this.fire_time + fire_duration);
            this.end_time = (this.hit_time + hit_duration);

            // fire vars
            this.fire_a = a;
            this.fire_x = x0;
            this.fire_y = y0;
            this.fire_w = 0;
            this.fire_dx = (dx / fire_duration);
            this.fire_dy = (dy / fire_duration);
            this.fire_dw = (w  / fire_duration);
            this.hit_frame = 0;
        }

        public boolean animate(float delta)
        {
            if (!fired && (elapsed < fire_time))
                return false;

            if (!fired) {
                fired = true;
                FireAnimation.infantryFireSndPlay(volume);
            }

            if (!hit && (elapsed < hit_time)) {
                fire_w += (fire_dw * delta);
                fire_x += (fire_dx * delta);
                fire_y += (fire_dy * delta);
                fireRegion.setRegionWidth((int) fire_w);
                return false;
            }

            if (!hit)
                hit = true;

            if (elapsed < end_time) {
                int frame = (int) ((elapsed - hit_time) / HIT_FRAME_DURATION);
                if (frame != hit_frame) {
                    hit_frame = frame;
                    fireRegion.setRegion(FireAnimation.infantryFire.frames[hit_frame]);
                    fireRegion.setRegionWidth((int) fire_w);
                }
                return false;
            }

            completed = true;
            return true;
        }

        public void draw(Batch batch)
        {
            if (fired && !completed)
                batch.draw(fireRegion, fire_x, fire_y, 0, 0, fireRegion.getRegionWidth(), fireRegion.getRegionHeight(), 1f, 1f, fire_a);
        }
    }

    private static final int SHOT_COUNT = 19;
    private static final float SHOT_DELAY = (1.6f / SHOT_COUNT);
    private static final float SHOT_SCATTERING = 40f;
    private static final float TIME_SCATTERING = 0.6f;
    private static final float START_DELAY = 0.8f;
    private static final float SHOT_SPEED = 1000f;
    private static final float HIT_FRAME_DURATION = 0.05f;

    private Shot[] shots;

    private float elapsed;

    private float volume;

    private static final Pool<InfantryFireAnimation> fireAnimationPool = new Pool<InfantryFireAnimation>() {
        @Override
        protected InfantryFireAnimation newObject() {
            return new InfantryFireAnimation();
        }
    };

    public static InfantryFireAnimation get(float volume, float x0, float y0, float x1, float y1, float halfWidth)
    {
        InfantryFireAnimation a = fireAnimationPool.obtain();
        a.set(volume, x0, y0, x1, y1, halfWidth);
        return a;
    }

    public InfantryFireAnimation()
    {
        this.shots = new Shot[SHOT_COUNT];
        for (int i = 0; i < shots.length; i++)
            shots[i] = new Shot(new TextureRegion(FireAnimation.infantryFire.frames[0]));
    }

    private void set(float volume, float x0, float y0, float x1, float y1, float halfWidth)
    {
        this.volume = volume;
        this.elapsed = 0f;

        float delay = START_DELAY + (FireAnimation.random.nextFloat() * TIME_SCATTERING);

        y0 -= (FireAnimation.infantryFire.height / 2.0f);
        double r = Math.atan2((y0 - y1), (x0 - x1));
        x0 -= ((float) (Math.cos(r) * halfWidth));
        y0 -= ((float) (Math.sin(r) * halfWidth));

        float dx = (x1 - x0);
        float dy = (y1 - y0);
        float w = (float) Math.sqrt((dx * dx) + (dy * dy));
        double dr = (Math.atan2(halfWidth, w) / 2f);

        double a = (r + (dr / 2f));
        double da = (dr / (float) SHOT_COUNT);

        for (Shot shot : shots) {
            float x = (float) (x0 - (Math.cos(a) * w));
            float y = (float) (y0 - (Math.sin(a) * w));

            shot.set(delay, x0, y0, x, y, w, (float) Math.toDegrees(a));

            delay += SHOT_DELAY;
            a -= 2 * (da * FireAnimation.random.nextFloat());
        }
    }

    @Override
    public void reset()
    {
    }

    @Override
    public void dispose()
    {
        fireAnimationPool.free(this);
    }

    @Override
    public boolean animate(float delta)
    {
        elapsed += delta;

        boolean completed = true;
        for (Shot shot : shots)
            completed &= shot.animate(delta);

        return completed;
    }

    @Override
    public void draw(Batch batch)
    {
        for (Shot shot : shots)
            shot.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        // debugShapes.end();
        // debugShapes.begin(ShapeRenderer.ShapeType.Line);
        // debugShapes.identity();
        // debugShapes.translate(fire_x, fire_y, 0);
        // debugShapes.rotate(0, 0, 1, fire_a);
        // debugShapes.translate(-fire_x, -fire_y, 0);
        // debugShapes.rect(fire_x, fire_y, fire_w, FireAnimation.infantryFire.height);
        // debugShapes.end();
        // debugShapes.begin(ShapeRenderer.ShapeType.Line);
        // debugShapes.identity();
    }
}
