package ch.asynk.rustanddust.engine.gfx.animations;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Moveable;
import ch.asynk.rustanddust.engine.gfx.Animation;

public class RunnableAnimation implements Animation, Pool.Poolable
{
    private Runnable runnable;
    private Moveable moveable;
    private boolean ran;

    private static final Pool<RunnableAnimation> runnableAnimationPool = new Pool<RunnableAnimation>() {
        @Override
        protected RunnableAnimation newObject() {
            return new RunnableAnimation();
        }
    };

    public static RunnableAnimation get(Moveable moveable, Runnable runnable)
    {
        RunnableAnimation a = runnableAnimationPool.obtain();
        a.runnable = runnable;
        a.moveable = moveable;
        return a;
    }

    @Override
    public void reset()
    {
        ran = false;
    }

    @Override
    public void dispose()
    {
        runnableAnimationPool.free(this);
    }

    @Override
    public boolean animate(float delta)
    {
        if (ran) return true;

        runnable.run();
        runnable = null;
        ran = true;
        dispose();

        return true;
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
