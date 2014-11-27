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

class Sprites
{
    public Texture texture;
    public TextureRegion[] frames;
    public final int width;
    public final int height;
    public final int cols;
    public final int rows;

    public Sprites(Texture texture, int cols, int rows)
    {
        this.cols = cols;
        this.rows = rows;
        this.width = (texture.getWidth() / cols);
        this.height = (texture.getHeight() / rows);
        this.texture = texture;
        TextureRegion[][] tmp = TextureRegion.split(texture, width, height);
        frames = new TextureRegion[cols * rows];
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[idx++] = tmp[i][j];
            }
        }
    }
}

public class ShotAnimation implements Disposable, Animation, Pool.Poolable
{
    private static final float SHOT_SCATTERING = 60f;
    private static final float TIME_SCATTERING = 0.6f;
    private static final float SHOT_SPEED = 700f;
    private static final float EXPLOSION_FRAME_DURATION = 0.05f;

    private static Random random = new Random();
    private static Sound shortShot;
    private static Sound longShot;
    private static Sprites shot;
    private static Sprites explosion;
    private static double longShotId;

    private TextureRegion shotRegion;
    private float shot_a;
    private float shot_x;
    private float shot_y;
    private float shot_w;
    private float shot_dx;
    private float shot_dy;
    private float shot_dw;

    private float smoke_dx;
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

    private static final Pool<ShotAnimation> shotAnimationPool = new Pool<ShotAnimation>() {
        @Override
        protected ShotAnimation newObject() {
            return new ShotAnimation();
        }
    };

    public static ShotAnimation get(float volume, float offset, float x0, float y0, float x1, float y1)
    {
        ShotAnimation a = shotAnimationPool.obtain();
        a.set(volume, offset, x0, y0, x1, y1);
        return a;
    }

    public static void init(Texture shot_texture, int scols, int srows, Texture explosion_texture, int ecols, int erows, Sound longSnd, Sound shortSnd)
    {
        longShot = longSnd;
        shortShot = shortSnd;
        shot = new Sprites(shot_texture, scols, srows);
        explosion = new Sprites(explosion_texture, ecols, erows);
        longShotId = -1;
    }

    public static void resetSound()
    {
        longShotId = -1;
    }

    public static void free()
    {
        shortShot.dispose();
        longShot.dispose();
        shot.texture.dispose();
        explosion.texture.dispose();
    }

    public ShotAnimation()
    {
        this.shotRegion = new TextureRegion(shot.frames[0]);
    }

    private void set(float volume, float offset, float x0, float y0, float x1, float y1)
    {
        this.fired = false;
        this.hit = false;
        this.volume = volume;

        // shot geometry
        y0 -= (shot.height / 2.0f);
        x1 += ((SHOT_SCATTERING * random.nextFloat()) - (SHOT_SCATTERING / 2f));
        y1 += ((SHOT_SCATTERING * random.nextFloat()) - (SHOT_SCATTERING / 2f));

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
        float delay = (random.nextFloat() * TIME_SCATTERING);
        float shot_duration = ((random.nextFloat() * TIME_SCATTERING) + (w / SHOT_SPEED));
        float explosion_duration = (explosion.cols * EXPLOSION_FRAME_DURATION);

        this.elapsed = 0f;
        this.fire_time = delay;
        this.hit_time = (fire_time + shot_duration);
        this.end_time = (hit_time + explosion_duration);

        // shot vars
        this.shot_a = a;
        this.shot_x = x0;
        this.shot_y = y0;
        this.shot_w = 0;
        this.shot_dx = (dx / shot_duration);
        this.shot_dy = (dy / shot_duration);
        this.shot_dw = (w  / shot_duration);

        // smoke var
        this.smoke_dx = 0f;
        this.smoke_df = (shot.rows / explosion_duration);
        this.smoke_frame = 0;

        // explosion vars
        this.explosion_x = (x1 - (explosion.width / 2.0f));
        this.explosion_y = (y1 - (explosion.height / 2.0f));
        this.explosion_df = (explosion.cols / explosion_duration);
        this.explosion_frame = (random.nextInt(explosion.rows) * explosion.cols);
    }

    @Override
    public void reset()
    {
    }

    @Override
    public void dispose()
    {
        shotAnimationPool.free(this);
    }

    @Override
    public boolean animate(float delta)
    {
        elapsed += delta;

        if (!fired && (elapsed < fire_time))
            return false;

        if (!fired) {
            fired = true;
            if (longShotId == -1)
                longShotId = longShot.play(volume);
            else
                shortShot.play(volume);
        }

        if (!hit && (elapsed < hit_time)) {
            shot_w += (shot_dw * delta);
            shot_x += (shot_dx * delta);
            shot_y += (shot_dy * delta);
            shotRegion.setRegionWidth((int) shot_w);
            return false;
        }

        if (!hit) {
            hit = true;
            // TODO hit sound
            shortShot.play(volume);
        }

        if (elapsed < end_time) {
            int frame = (int) ((elapsed - hit_time) * smoke_df);
            if (frame != smoke_frame) {
                smoke_frame = frame;
                shotRegion.setRegion(shot.frames[smoke_frame]);
                shotRegion.setRegionWidth((int) shot_w);
            }
            return false;
        }

        return true;
    }

    @Override
    public void draw(Batch batch)
    {
        if (fired)
            batch.draw(shotRegion, shot_x, shot_y, 0, 0, shotRegion.getRegionWidth(), shotRegion.getRegionHeight(), 1f, 1f, shot_a);

        if (hit) {
            int frame = (explosion_frame + (int) ((elapsed - hit_time) * explosion_df));
            batch.draw(explosion.frames[frame], explosion_x, explosion_y);
        }
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        debugShapes.end();
        debugShapes.begin(ShapeRenderer.ShapeType.Line);
        debugShapes.identity();
        debugShapes.translate(shot_x, shot_y, 0);
        debugShapes.rotate(0, 0, 1, shot_a);
        debugShapes.translate(-shot_x, -shot_y, 0);
        debugShapes.rect(shot_x, shot_y, shot_w, shot.height);
        debugShapes.end();
        debugShapes.begin(ShapeRenderer.ShapeType.Line);
        debugShapes.identity();
    }
}
