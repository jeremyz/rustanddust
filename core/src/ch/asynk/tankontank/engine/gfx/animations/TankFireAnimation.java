package ch.asynk.tankontank.engine.gfx.animations;

import java.util.Random;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.Animation;

public class TankFireAnimation implements Disposable, Animation, Pool.Poolable
{
    private static final float SHOT_SCATTERING = 60f;
    private static final float TIME_SCATTERING = 0.6f;
    private static final float START_DELAY = 0.8f;
    private static final float SHOT_SPEED = 900f;
    private static final float EXPLOSION_FRAME_DURATION = 0.07f;

    private TextureRegion fireRegion;
    private float fire_a;
    private float fire_x;
    private float fire_y;
    private float fire_w;
    private float fire_dx;
    private float fire_dy;
    private float fire_dw;

    private float smoke_df;
    private int smoke_frame;

    private float explosion_x;
    private float explosion_y;
    private float explosion_df;
    private int explosion_frame;

    private boolean fired;
    private boolean hit;
    private float elapsed;
    private float fire_time;
    private float hit_time;
    private float end_time;

    private float volume;

    private static final Pool<TankFireAnimation> fireAnimationPool = new Pool<TankFireAnimation>() {
        @Override
        protected TankFireAnimation newObject() {
            return new TankFireAnimation();
        }
    };

    public static TankFireAnimation get(float volume, float offset, float x0, float y0, float x1, float y1)
    {
        TankFireAnimation a = fireAnimationPool.obtain();
        a.set(volume, offset, x0, y0, x1, y1);
        return a;
    }

    public TankFireAnimation()
    {
        this.fireRegion = new TextureRegion(FireAnimation.tankFire.frames[0]);
    }

    private void set(float volume, float offset, float x0, float y0, float x1, float y1)
    {
        this.fired = false;
        this.hit = false;
        this.volume = volume;

        // fire geometry
        y0 -= (FireAnimation.tankFire.height / 2.0f);
        x1 += ((SHOT_SCATTERING * FireAnimation.random.nextFloat()) - (SHOT_SCATTERING / 2f));
        y1 += ((SHOT_SCATTERING * FireAnimation.random.nextFloat()) - (SHOT_SCATTERING / 2f));

        double r = Math.atan2((y0 - y1), (x0 - x1));
        float xadj = (float) (Math.cos(r) * offset);
        float yadj = (float) (Math.sin(r) * offset);
        x0 -= xadj;
        y0 -= yadj;

        float a = (float) Math.toDegrees(r);
        float dx = (x1 - x0);
        float dy = (y1 - y0);
        float w = (float) Math.sqrt((dx * dx) + (dy * dy));

        // timing
        float delay = START_DELAY + (FireAnimation.random.nextFloat() * TIME_SCATTERING);
        float fire_duration = ((FireAnimation.random.nextFloat() * TIME_SCATTERING) + (w / SHOT_SPEED));
        float explosion_duration = (FireAnimation.explosion.cols * EXPLOSION_FRAME_DURATION);

        this.elapsed = 0f;
        this.fire_time = delay;
        this.hit_time = (fire_time + fire_duration);
        this.end_time = (hit_time + explosion_duration);

        // fire vars
        this.fire_a = a;
        this.fire_x = x0;
        this.fire_y = y0;
        this.fire_w = 0;
        this.fire_dx = (dx / fire_duration);
        this.fire_dy = (dy / fire_duration);
        this.fire_dw = (w  / fire_duration);

        // smoke var
        this.smoke_df = (FireAnimation.tankFire.rows / explosion_duration);
        this.smoke_frame = 0;

        // explosion vars
        this.explosion_x = (x1 - (FireAnimation.explosion.width / 2.0f));
        this.explosion_y = (y1 - (FireAnimation.explosion.height / 2.0f));
        this.explosion_df = (FireAnimation.explosion.cols / explosion_duration);
        this.explosion_frame = (FireAnimation.random.nextInt(FireAnimation.explosion.rows) * FireAnimation.explosion.cols);
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

        if (!fired && (elapsed < fire_time))
            return false;

        if (!fired) {
            fired = true;
            FireAnimation.tankFireSndPlay(volume);
        }

        if (!hit && (elapsed < hit_time)) {
            fire_w += (fire_dw * delta);
            fire_x += (fire_dx * delta);
            fire_y += (fire_dy * delta);
            fireRegion.setRegionWidth((int) fire_w);
            return false;
        }

        if (!hit) {
            hit = true;
            FireAnimation.explosionSndPlay(volume);
        }

        if (elapsed < end_time) {
            int frame = (int) ((elapsed - hit_time) * smoke_df);
            if (frame != smoke_frame) {
                smoke_frame = frame;
                fireRegion.setRegion(FireAnimation.tankFire.frames[smoke_frame]);
                fireRegion.setRegionWidth((int) fire_w);
            }
            return false;
        }

        return true;
    }

    @Override
    public void draw(Batch batch)
    {
        if (fired)
            batch.draw(fireRegion, fire_x, fire_y, 0, 0, fireRegion.getRegionWidth(), fireRegion.getRegionHeight(), 1f, 1f, fire_a);

        if (hit) {
            int frame = (explosion_frame + (int) ((elapsed - hit_time) * explosion_df));
            batch.draw(FireAnimation.explosion.frames[frame], explosion_x, explosion_y);
        }
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        debugShapes.end();
        debugShapes.begin(ShapeRenderer.ShapeType.Line);
        debugShapes.identity();
        debugShapes.translate(fire_x, fire_y, 0);
        debugShapes.rotate(0, 0, 1, fire_a);
        debugShapes.translate(-fire_x, -fire_y, 0);
        debugShapes.rect(fire_x, fire_y, fire_w, FireAnimation.tankFire.height);
        debugShapes.end();
        debugShapes.begin(ShapeRenderer.ShapeType.Line);
        debugShapes.identity();
    }
}
