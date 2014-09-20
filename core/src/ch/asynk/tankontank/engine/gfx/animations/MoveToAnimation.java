package ch.asynk.tankontank.engine.gfx.animations;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import ch.asynk.tankontank.engine.gfx.Node;

public class MoveToAnimation extends TimedAnimation
{
    private Node node;
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

    public static MoveToAnimation get(Node node, Vector3 v, float duration)
    {
        return get(node, v.x, v.y, v.z, duration);
    }

    public static MoveToAnimation get(Node node, float x, float y, float r, float duration)
    {
        MoveToAnimation a = moveToAnimationPool.obtain();

        a.node = node;
        a.toX = x;
        a.toY = y;
        a.toR = r;
        a.duration = duration;

        return a;
    }

    @Override
    public Node getNode()
    {
        return node;
    }

    @Override
    public void dispose()
    {
        moveToAnimationPool.free(this);
    }

    @Override
    protected void begin()
    {
        fromX = node.getX();
        fromY = node.getY();
        fromR = node.getRotation();
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
            node.setPosition(toX, toY, (int) toR);
        else
            node.setPosition(fromX + ((toX - fromX) * percent), fromY + ((toY - fromY) * percent), (int) (fromR + ((toR - fromR) * percent)));
    }
}
