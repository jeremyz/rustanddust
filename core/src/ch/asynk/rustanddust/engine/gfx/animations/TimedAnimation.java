package ch.asynk.rustanddust.engine.gfx.animations;

import com.badlogic.gdx.utils.Pool;

import ch.asynk.rustanddust.engine.gfx.Animation;

public abstract class TimedAnimation implements Animation, Pool.Poolable
{
    private float time;
    private boolean began;
    private boolean completed;
    protected float duration;

    abstract protected void begin();
    abstract protected void end();
    abstract protected void update(float percent);

    @Override
    public void reset()
    {
        time = 0f;
        began = false;
        completed = false;
    }

    @Override
    public boolean animate(float delta)
    {
        if (completed) return true;

        if (!began) {
            begin();
            began = true;
        }

        time += delta;
        completed = (time >= duration);

        if (!completed) {
            update(time / duration);
            return false;
        }

        update(1);
        end();
        return true;
    }
}
