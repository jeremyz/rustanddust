package ch.asynk.tankontank.engine;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.animations.MoveToAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;

public class PawnNode extends Image implements Pawn
{
    private static final float MOVE_TIME = 0.3f;

    private ArrayDeque<Vector3> path = new ArrayDeque<Vector3>();

    public PawnNode(TextureRegion region)
    {
        super(region);
    }

    @Override
    public Vector3 getLastPosition()
    {
        if ((path == null) || (path.size() == 0)) return null;
        return path.getFirst();
    }

    @Override
    public void moveDone()
    {
        Vector3 v = path.pop();
        path.clear();
        path.push(v);
    }

    @Override
    public void pushMove(float x, float y, Pawn.Orientation o)
    {
        float r = ((o == Pawn.Orientation.KEEP) ? getRotation() : o.v);
        setPosition(x, y, r);
        Vector3 v = new Vector3(x, y, r);
        if ((path.size() == 0) || (!v.equals(path.getFirst())))
            path.push(new Vector3(x, y, r));
    }

    @Override
    public AnimationSequence getResetMovesAnimation()
    {
        final Vector3 finalPos = path.getLast();

        AnimationSequence seq = AnimationSequence.get(path.size() + 1);

        while(path.size() != 0) {
            seq.addAnimation(MoveToAnimation.get(this, path.pop(), MOVE_TIME));
        }

        seq.addAnimation(RunnableAnimation.get(new Runnable() {
            @Override
            public void run() {
                path.push(finalPos);
            }
        }));

        return seq;
    }
}
