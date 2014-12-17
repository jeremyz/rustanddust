package ch.asynk.tankontank.engine.gfx.animations;

import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.Animation;

public class DiceAnimation implements Animation, Drawable
{
    private static final float DURATION = 0.7f;
    private static final float DURATION_SCATTERING = 0.5f;
    private static final int DICE_DIMENSION = 24;

    private static Random random = new Random();
    private static Sprites dice;
    private static Sound sound;
    private static double sndId;
    private static float volume;
    private static int[][] rolls = new int[][]{
        { 25, 40, 55, 70, 85, 100, 115, 99, 83, 67, 51, 36, 37, 52, 67, 66, 65, 64 },
        { 58, 74, 59, 60, 45, 62, 78, 94, 109, 108, 123, 106, 89, 71, 70, 69, 68 },
        { 106, 121, 120, 103, 86, 70, 54, 37, 20, 19, 18, 34, 50, 51, 52, 69, 86, 103, 119, 128 },
        { 95, 79, 93, 92, 91, 90, 104, 103, 102, 85, 84, 67, 66, 65, 49, 32, 16, 0 },
        { 22, 39, 56, 73, 90, 107, 124, 128, 113, 98, 83, 68, 53, 38, 23, 0, 25, 42, 59, 76 },
        { 79, 78, 61, 76, 91, 106, 121, 120, 119, 102, 101, 84, 68, 52, 37, 38, 39, 40, 41, 58, 75, 74, 73, 72 },
    };

    private float x;
    private float y;
    private int frame;
    private int[] roll;
    private float elapsed;
    private float duration;
    // public boolean stop;

    public static void init(Texture texture, int cols, int rows, Sound s)
    {
        dice = new Sprites(texture, cols, rows);
        sound = s;
        sndId = -1;
    }

    public static void initSound(float v)
    {
        sndId = -1;
        volume = v;
    }

    public static void free()
    {
        sound.dispose();
        dice.dispose();
    }

    public void translate(float dx, float dy)
    {
        x += dx;
        y += dy;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public int getWidth()
    {
        return DICE_DIMENSION;
    }

    public int getHeight()
    {
        return DICE_DIMENSION;
    }

    public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void set(int result)
    {
        this.frame = 0;
        this.elapsed = 0f;
        this.roll = rolls[result - 1];
        this.duration = DURATION + (DURATION_SCATTERING * random.nextFloat());
        // this.stop = false;
    }

    public boolean isDone()
    {
        return (elapsed >= duration);
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public boolean animate(float delta)
    {
        // if (stop)
            // return true;
        elapsed += delta;
        if (elapsed < duration) {
            int idx = (int) (roll.length * elapsed / duration);
            if (idx >= roll.length)
                idx = (roll.length -1);
            frame = roll[idx];
        }
        if (sndId == -1)
            sndId = sound.play(volume);

        return false;
    }

    @Override
    public void draw(Batch batch)
    {
        batch.draw(dice.frames[frame], x, y, DICE_DIMENSION, DICE_DIMENSION);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        debugShapes.rect(x, y, dice.frames[frame].getRegionWidth(), dice.frames[frame].getRegionHeight());
    }
}
