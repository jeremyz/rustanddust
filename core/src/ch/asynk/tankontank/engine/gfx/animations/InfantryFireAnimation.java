package ch.asynk.tankontank.engine.gfx.animations;

import java.util.Random;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.Animation;

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
    }

    private static final int SHOT_COUNT = 10;
    private static final float SHOT_SCATTERING = 40f;
    private static final float TIME_SCATTERING = 0.6f;
    private static final float START_DELAY = 0.8f;
    private static final float SHOT_SPEED = 1000f;
    private static final float HIT_FRAME_DURATION = 0.07f;

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

        for (Shot shot : shots) {
            // fire geometry
            float x0 = _x0;
            float y0 = (_y0 - (FireAnimation.infantryFire.height / 2.0f));
            float x1 = (_x1 + ((SHOT_SCATTERING * FireAnimation.random.nextFloat()) - (SHOT_SCATTERING / 2f)));
            float y1 = (_y1 + ((SHOT_SCATTERING * FireAnimation.random.nextFloat()) - (SHOT_SCATTERING / 2f)));

            double r = Math.atan2((y0 - y1), (x0 - x1));
            float xadj = (float) (Math.cos(r) * halfWidth);
            float yadj = (float) (Math.sin(r) * halfWidth);
            x0 -= xadj;
            y0 -= yadj;

            float a = (float) Math.toDegrees(r);
            float dx = (x1 - x0);
            float dy = (y1 - y0);
            float w = (float) Math.sqrt((dx * dx) + (dy * dy));

            // timing
            float delay = START_DELAY + (FireAnimation.random.nextFloat() * TIME_SCATTERING);
            float fire_duration = ((FireAnimation.random.nextFloat() * TIME_SCATTERING) + (w / SHOT_SPEED));
            float hit_duration = (FireAnimation.infantryFire.rows * HIT_FRAME_DURATION);

            shot.fired = false;
            shot.fire_time = delay;
            shot.hit_time = (shot.fire_time + fire_duration);
            shot.end_time = (shot.hit_time + hit_duration);

            // fire vars
            shot.fire_a = a;
            shot.fire_x = x0;
            shot.fire_y = y0;
            shot.fire_w = 0;
            shot.fire_dx = (dx / fire_duration);
            shot.fire_dy = (dy / fire_duration);
            shot.fire_dw = (w  / fire_duration);
            shot.hit_frame = 0;
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
        for (Shot shot : shots) {
            completed &= shot.completed;

            if (!shot.fired && (elapsed < shot.fire_time))
                continue;

            if (!shot.fired) {
                shot.fired = true;
                FireAnimation.infantryFireSndPlay(volume);
            }

            if (!shot.hit && (elapsed < shot.hit_time)) {
                shot.fire_w += (shot.fire_dw * delta);
                shot.fire_x += (shot.fire_dx * delta);
                shot.fire_y += (shot.fire_dy * delta);
                shot.fireRegion.setRegionWidth((int) shot.fire_w);
                continue;
            }

            if (!shot.hit)
                shot.hit = true;

            if (elapsed < shot.end_time) {
                int frame = (int) ((elapsed - shot.hit_time) / HIT_FRAME_DURATION);
                if (frame != shot.hit_frame) {
                    shot.hit_frame = frame;
                    shot.fireRegion.setRegion(FireAnimation.infantryFire.frames[shot.hit_frame]);
                    shot.fireRegion.setRegionWidth((int) shot.fire_w);
                }
                continue;
            } else
                shot.completed = true;
        }

        return completed;
    }

    @Override
    public void draw(Batch batch)
    {
        for (Shot shot : shots) {
            if (shot.fired)
                batch.draw(shot.fireRegion, shot.fire_x, shot.fire_y, 0, 0,
                        shot.fireRegion.getRegionWidth(), shot.fireRegion.getRegionHeight(), 1f, 1f, shot.fire_a);
        }
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
