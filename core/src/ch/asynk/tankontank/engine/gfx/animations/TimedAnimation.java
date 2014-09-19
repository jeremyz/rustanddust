package ch.asynk.tankontank.engine.gfx.animations;

import com.badlogic.gdx.utils.Pool.Poolable;

import ch.asynk.tankontank.engine.gfx.Animation;

abstract public class TimedAnimation implements Animation, Poolable
{
    private float time;
    private boolean began;
    private boolean completed;
    protected float duration;

    @Override
    public void reset()
    {
        time = 0f;
        began = false;
        completed = false;
    }

    abstract protected void begin();
    abstract protected void end();
    abstract protected void update(float percent);

    public boolean act(float delta)
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
