package ch.asynk.tankontank.engine.gfx.animations;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Animation;

public class AnimationSequence implements Animation, Pool.Poolable
{
    private ArrayList<Animation> animations;

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
            seq.animations = new ArrayList<Animation>(capacity);
        else
            seq.animations.ensureCapacity(capacity);

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
            animations.remove(0);
        }

        return (animations.isEmpty());
    }

    @Override
    public void draw(Batch batch)
    {
        animations.get(0).draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        animations.get(0).drawDebug(debugShapes);
    }
}
