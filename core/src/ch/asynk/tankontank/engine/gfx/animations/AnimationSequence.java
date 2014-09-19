package ch.asynk.tankontank.engine.gfx.animations;

import java.util.Vector;

import com.badlogic.gdx.utils.Pool;

import ch.asynk.tankontank.engine.gfx.Node;
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
            animations.get(i).free();
        animations.clear();
    }

    @Override
    public void free()
    {
        animationSequencePool.free(this);
    }

    @Override
    public Node getNode()
    {
        return animations.get(0).getNode();
    }

    public void addAnimation(Animation animation)
    {
        animations.add(animation);
    }

    public boolean act(float delta)
    {
        if(animations.isEmpty()) return true;

        Animation animation = animations.get(0);
        if (animation.act(delta)) {
            animations.removeElementAt(0);
        }

        return (animations.isEmpty());
    }
}
