package ch.asynk.tankontank.engine.gfx.animations;

import com.badlogic.gdx.utils.Pool;

import ch.asynk.tankontank.engine.gfx.Node;
import ch.asynk.tankontank.engine.gfx.Animation;

public class RunnableAnimation implements Animation, Pool.Poolable
{
    private Runnable runnable;
    private boolean ran;

    private static final Pool<RunnableAnimation> runnableAnimationPool = new Pool<RunnableAnimation>() {
        @Override
        protected RunnableAnimation newObject() {
            return new RunnableAnimation();
        }
    };

    public static RunnableAnimation get(Runnable runnable)
    {
        RunnableAnimation a = runnableAnimationPool.obtain();
        a.runnable = runnable;
        return a;
    }

    @Override
    public void reset()
    {
        ran = false;
    }

    @Override
    public void free()
    {
        runnableAnimationPool.free(this);
    }

    @Override
    public Node getNode()
    {
        return null;
    }

    @Override
    public boolean act(float delta)
    {
        if (ran) return true;

        runnable.run();
        runnable = null;
        ran = true;
        free();

        return true;
    }
}
