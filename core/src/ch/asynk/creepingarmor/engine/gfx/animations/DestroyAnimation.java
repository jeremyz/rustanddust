package ch.asynk.creepingarmor.engine.gfx.animations;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.creepingarmor.engine.gfx.Moveable;
import ch.asynk.creepingarmor.engine.gfx.Animation;

public class DestroyAnimation implements Disposable, Animation
{
    private static final float DELAY = 1.5f;
    private static final float DURATION = 1.5f;

    private Moveable moveable;
    private float x;
    private float y;
    private int alphaP;
    private float elapsed;

    @Override
    public void dispose()
    {
    }

    public void set(float duration, Moveable moveable)
    {
        this.moveable = moveable;
        this.alphaP = 0;
        this.elapsed = 0f;
        this.x = (moveable.getX() + (moveable.getWidth() / 2f));
        this.y = (moveable.getY() + (moveable.getHeight() / 2f));
    }

    @Override
    public boolean animate(float delta)
    {
        elapsed += delta;
        if (elapsed < DELAY)
            return false;

        int a = (int) (((elapsed - DELAY) / DURATION) * 10);
        if (a != alphaP) {
            alphaP = a;
            moveable.setAlpha(1f - (alphaP / 10f));
        }

        return (elapsed >= (DELAY + DURATION));
    }

    @Override
    public void draw(Batch batch)
    {
        moveable.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        moveable.drawDebug(debugShapes);
    }
}
