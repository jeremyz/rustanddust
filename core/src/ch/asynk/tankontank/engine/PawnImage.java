package ch.asynk.tankontank.engine;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import com.badlogic.gdx.math.Vector3;

public class PawnImage extends Image implements Pawn
{
    private static final float MOVE_TIME = 0.3f;
    private static final float ROTATE_TIME = 0.2f;

    private ArrayDeque<Vector3> path = new ArrayDeque<Vector3>();

    public PawnImage(TextureRegion region)
    {
        super(region);
        setOrigin((getWidth() / 2.f), (getHeight() / 2.f));
    }

    public Vector3 getLastPosition()
    {
        if ((path == null) || (path.size() == 0)) return null;
        return path.getFirst();
    }

    public void pushMove(float x, float y, int z, Pawn.Orientation r)
    {
        setPosition(x, y);
        if (r != Pawn.Orientation.KEEP) setRotation(r.v);
        setZIndex(z);
        Vector3 v = new Vector3(x, y, r.v);
        if ((path.size() == 0) || (!v.equals(path.getFirst())))
            path.push(new Vector3(x, y, r.v));
    }

    public void resetMoves(Runnable cb)
    {
        final Vector3 finalPos = path.getLast();

        SequenceAction seq = new SequenceAction();

        while(path.size() != 0) {
            Vector3 v = path.pop();
            seq.addAction(Actions.moveTo(v.x, v.y, MOVE_TIME));
            if (v.z != Pawn.Orientation.KEEP.v)
                seq.addAction(Actions.rotateTo(v.z, ROTATE_TIME));
        }

        seq.addAction( Actions.run(new Runnable() {
            @Override
            public void run() {
                path.push(finalPos);
            }
        }));

        // the map must finalize this move
        seq.addAction(Actions.run(cb));

        addAction(seq);
    }

    public void moveDone()
    {
        Vector3 v = path.pop();
        path.clear();
        path.push(v);
    }
}
