package ch.asynk.creepingarmor.engine.gfx.animations;

import java.util.Random;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.creepingarmor.engine.gfx.Drawable;
import ch.asynk.creepingarmor.engine.gfx.Animation;

public class SpriteAnimation implements Disposable, Animation
{
    private static Random random = new Random();
    private Sprites sprites;
    private float duration;
    private float frameDuration;
    private float elapsed;
    private float x0;
    private float y0;
    private float x1;
    private float y1;
    private int randFreq;

    public SpriteAnimation(Texture texture, int cols, int rows, int randFreq)
    {
        this.sprites = new Sprites(texture, cols, rows);
        this.randFreq = randFreq;
    }

    @Override
    public void dispose()
    {
        sprites.dispose();
    }

    public void init(float duration, float x, float y)
    {
        this.duration = duration;
        this.frameDuration = (duration / (float) sprites.frames.length);
        this.x0 = x - (sprites.width / 2f);
        this.y0 = y - (sprites.height / 2f);
        this.elapsed = 0f;
        randPos();
    }

    private void randPos()
    {
        this.x1 = this.x0 + (random.nextInt(sprites.width) - (sprites.width / 2));
        this.y1 = this.y0 + (random.nextInt(sprites.height) - (sprites.height / 2));
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
        int n = (((int)(elapsed / frameDuration)) % sprites.frames.length);
        if ((n > 0) && (n % randFreq) == 0)
            randPos();
        batch.draw(sprites.frames[n], x1, y1);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
    }
}
