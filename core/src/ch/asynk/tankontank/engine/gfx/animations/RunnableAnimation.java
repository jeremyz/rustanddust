package ch.asynk.tankontank.engine.gfx.animations;

import com.badlogic.gdx.utils.Pool;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.gfx.Animation;

public class RunnableAnimation implements Animation, Pool.Poolable
{
    private Runnable runnable;
    private Pawn pawn;
    private boolean ran;

    private static final Pool<RunnableAnimation> runnableAnimationPool = new Pool<RunnableAnimation>() {
        @Override
        protected RunnableAnimation newObject() {
            return new RunnableAnimation();
        }
    };

    public static RunnableAnimation get(Pawn pawn, Runnable runnable)
    {
        RunnableAnimation a = runnableAnimationPool.obtain();
        a.runnable = runnable;
        a.pawn = pawn;
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
    public Pawn getPawn()
    {
        return pawn;
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
}
