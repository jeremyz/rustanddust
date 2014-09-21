package ch.asynk.tankontank.engine.gfx.animations;

import java.util.Vector;

import com.badlogic.gdx.utils.Pool;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.gfx.Animation;

public class AnimationSequence implements Animation, Pool.Poolable
{
    private Vector<Animation> animations;

    private static final Pool<AnimationSequence> animationSequencePool = new Pool<AnimationSequence>() {
        @Override
        protected AnimationSequence newObject() {
            return new AnimationSequence();
        }
    };

    public static AnimationSequence get(int capacity)
    {
        AnimationSequence seq = animationSequencePool.obtain();
        if (seq.animations == null)
            seq.animations = new Vector<Animation>(capacity);
        else
            seq.animations.setSize(capacity);

        return seq;
    }

    @Override
    public void reset()
    {
        for (int i = 0, n = animations.size(); i < n; i++)
            animations.get(i).dispose();
        animations.clear();
    }

    @Override
    public void dispose()
    {
        animationSequencePool.free(this);
    }

    @Override
    public Pawn getPawn()
    {
        return animations.get(0).getPawn();
    }

    public void addAnimation(Animation animation)
    {
        animations.add(animation);
    }

    @Override
    public boolean animate(float delta)
    {
        if(animations.isEmpty()) return true;

        Animation animation = animations.get(0);
        if (animation.animate(delta)) {
            animations.removeElementAt(0);
        }

        return (animations.isEmpty());
    }
}
