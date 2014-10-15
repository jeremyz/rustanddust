package ch.asynk.tankontank.engine.gfx.animations;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Moveable;

public class MoveToAnimation extends TimedAnimation
{
    private Moveable moveable;
    private float fromX;
    private float fromY;
    private float fromR;
    private float toX;
    private float toY;
    private float toR;
    private float rDelta;

    private static final Pool<MoveToAnimation> moveToAnimationPool = new Pool<MoveToAnimation>() {
        @Override
        protected MoveToAnimation newObject() {
            return new MoveToAnimation();
        }
    };

    public static MoveToAnimation get(Moveable moveable, Vector3 v, float duration)
    {
        return get(moveable, v.x, v.y, v.z, duration);
    }

    public static MoveToAnimation get(Moveable moveable, float x, float y, float r, float duration)
    {
        MoveToAnimation a = moveToAnimationPool.obtain();

        a.moveable = moveable;
        a.toX = x;
        a.toY = y;
        a.toR = r;
        a.duration = duration;
        a.rDelta = 0;

        return a;
    }

    @Override
    public void dispose()
    {
        moveToAnimationPool.free(this);
    }

    @Override
    protected void begin()
    {
        fromX = moveable.getX();
        fromY = moveable.getY();
        fromR = moveable.getRotation();

        if (Math.abs(toR - fromR) <= 180.f)
            rDelta = (toR - fromR);
        else {
            if (toR > fromR)
                rDelta = (toR - 360 - fromR);
            else
                rDelta = (toR + 360 - fromR);
        }
    }

    @Override
    protected void end()
    {
        dispose();
    }

    @Override
    protected void update(float percent)
    {
        if (percent == 1f)
            moveable.setPosition(toX, toY, (int) toR);
        else
            moveable.setPosition(fromX + ((toX - fromX) * percent), fromY + ((toY - fromY) * percent), (fromR + (rDelta * percent)));
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
