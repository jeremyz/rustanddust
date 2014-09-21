package ch.asynk.tankontank.engine.gfx.animations;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import ch.asynk.tankontank.engine.Pawn;

public class MoveToAnimation extends TimedAnimation
{
    private Pawn pawn;
    private float fromX;
    private float fromY;
    private float fromR;
    private float toX;
    private float toY;
    private float toR;

    private static final Pool<MoveToAnimation> moveToAnimationPool = new Pool<MoveToAnimation>() {
        @Override
        protected MoveToAnimation newObject() {
            return new MoveToAnimation();
        }
    };

    public static MoveToAnimation get(Pawn pawn, Vector3 v, float duration)
    {
        return get(pawn, v.x, v.y, v.z, duration);
    }

    public static MoveToAnimation get(Pawn pawn, float x, float y, float r, float duration)
    {
        MoveToAnimation a = moveToAnimationPool.obtain();

        a.pawn = pawn;
        a.toX = x;
        a.toY = y;
        a.toR = r;
        a.duration = duration;

        return a;
    }

    @Override
    public Pawn getPawn()
    {
        return pawn;
    }

    @Override
    public void dispose()
    {
        moveToAnimationPool.free(this);
    }

    @Override
    protected void begin()
    {
        fromX = pawn.getX();
        fromY = pawn.getY();
        fromR = pawn.getRotation();
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
            pawn.setPosition(toX, toY, (int) toR);
        else
            pawn.setPosition(fromX + ((toX - fromX) * percent), fromY + ((toY - fromY) * percent), (int) (fromR + ((toR - fromR) * percent)));
    }
}
