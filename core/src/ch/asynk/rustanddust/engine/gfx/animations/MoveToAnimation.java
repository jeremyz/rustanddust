package ch.asynk.rustanddust.engine.gfx.animations;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Moveable;

public class MoveToAnimation extends TimedAnimation
{
    public interface MoveToAnimationCb {
        void moveToAnimationLeave(Moveable moveable, float x, float y, float r);
        void moveToAnimationEnter(Moveable moveable, float x, float y, float r);
        void moveToAnimationDone(Moveable moveable, float x, float y, float r);
    }

    private Moveable moveable;
    private float fromX;
    private float fromY;
    private float fromR;
    private float toX;
    private float toY;
    private float toR;
    private float rDelta;
    private boolean notified;
    private MoveToAnimationCb cb;

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

    public static MoveToAnimation get(Moveable moveable, Vector3 v, float duration, MoveToAnimationCb cb)
    {
        return get(moveable, v.x, v.y, v.z, duration, cb);
    }

    public static MoveToAnimation get(Moveable moveable, float x, float y, float r, float duration)
    {
        return get(moveable, x, y, r, duration, null);
    }

    public static MoveToAnimation get(Moveable moveable, float x, float y, float r, float duration, MoveToAnimationCb cb)
    {
        MoveToAnimation a = moveToAnimationPool.obtain();

        a.moveable = moveable;
        a.toX = x;
        a.toY = y;
        a.toR = r;
        a.duration = duration;
        a.cb = cb;
        a.rDelta = 0;
        a.notified = false;

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
        notified = ((fromX == toX) && (fromY == toY));

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
        if (cb != null)
            cb.moveToAnimationDone(moveable, (toX + (moveable.getWidth() / 2)), (toY + (moveable.getHeight() / 2)), toR);
        dispose();
    }

    @Override
    protected void update(float percent)
    {
        if ((cb != null) && !notified && (percent >= 0.5)) {
            float dw = (moveable.getWidth() / 2);
            float dh = (moveable.getHeight() / 2);
            cb.moveToAnimationLeave(moveable, (fromX + dw), (fromY + dh), fromR);
            cb.moveToAnimationEnter(moveable, (toX + dw), (toY + dh), toR);
            notified = true;
        }
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
