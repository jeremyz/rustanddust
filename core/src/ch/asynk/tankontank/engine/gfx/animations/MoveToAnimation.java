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

    @Override
    public Node getNode()
    {
        return node;
    }

    @Override
    public void free()
    {
        moveToAnimationPool.free(this);
    }

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

    protected void begin()
    {
        fromX = node.getX();
        fromY = node.getY();
        fromR = node.getRotation();
    }

    protected void end()
    {
        free();
    }

    protected void update(float percent)
    {
        if (percent == 1f)
            node.setCoords(toX, toY, (int) toR);
        else
            node.setCoords(fromX + ((toX - fromX) * percent), fromY + ((toY - fromY) * percent), (int) (fromR + ((toR - fromR) * percent)));
    }
}
