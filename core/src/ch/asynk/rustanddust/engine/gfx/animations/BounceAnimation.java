package ch.asynk.rustanddust.engine.gfx.animations;

import java.lang.Math;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Moveable;

public class BounceAnimation extends TimedAnimation
{
    public static float bounceFactor = 0.3f;

    private Moveable moveable;

    private static final Pool<BounceAnimation> bounceAnimationPool = new Pool<BounceAnimation>() {
        @Override
        protected BounceAnimation newObject() {
            return new BounceAnimation();
        }
    };

    public static BounceAnimation get(Moveable moveable, float duration)
    {
        BounceAnimation a = bounceAnimationPool.obtain();

        a.moveable = moveable;
        a.duration = duration;

        return a;
    }

    @Override
    public void dispose()
    {
        bounceAnimationPool.free(this);
    }

    @Override
    protected void begin()
    {
    }

    @Override
    protected void end()
    {
        moveable.setScale(1f);
    }

    @Override
    protected void update(float percent)
    {
        moveable.setScale(1 + bounceFactor * (float) Math.sin(percent * Math.PI));
    }

    @Override
    public void draw(Batch batch)
    {
        moveable.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
    }
}
