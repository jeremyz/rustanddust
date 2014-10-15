package ch.asynk.tankontank.engine.gfx.animations;

import java.util.Random;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.Animation;

public class SpriteAnimation implements Disposable, Animation
{
    private static Random random = new Random();
    private Texture texture;
    private TextureRegion[] frames;
    private float duration;
    private float frameDuration;
    private float elapsed;
    private float x0;
    private float y0;
    private float x1;
    private float y1;
    private int w;
    private int h;
    private int randFreq;

    public SpriteAnimation(Texture texture, int cols, int rows, int randFreq)
    {
        this.texture = texture;
        this.randFreq = randFreq;
        this.w = (texture.getWidth() / cols);
        this.h = (texture.getHeight() / rows);
        TextureRegion[][] tmp = TextureRegion.split(texture, w, h);
        this.frames = new TextureRegion[cols * rows];
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.frames[idx++] = tmp[i][j];
            }
        }
    }

    @Override
    public void dispose()
    {
        this.texture.dispose();
    }

    public void init(float duration, float x, float y)
    {
        this.duration = duration;
        this.frameDuration = (duration / (float) frames.length);
        this.x0 = x - (w/ 2);
        this.y0 = y - (h / 2);
        this.elapsed = 0f;
        randPos();
    }

    private void randPos()
    {
        this.x1 = this.x0 + (random.nextInt(w / 1) - (w / 2));
        this.y1 = this.y0 + (random.nextInt(h / 1) - (h / 2));
    }

    @Override
    public boolean animate(float delta)
    {
        elapsed += delta;
        return (elapsed >= duration);
    }

    @Override
    public void draw(Batch batch)
    {
        int n = (((int)(elapsed / frameDuration)) % frames.length);
        if ((n > 0) && (n % randFreq) == 0)
            randPos();
        batch.draw(frames[n], x1, y1, w, h);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
    }
}
