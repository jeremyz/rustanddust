package ch.asynk.tankontank.engine;

import java.util.Vector;
import java.util.ArrayDeque;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.StackedImages;
import ch.asynk.tankontank.engine.gfx.animations.MoveToAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;

public abstract class Pawn extends Image implements Disposable
{
    private static final float MOVE_TIME = 0.3f;

    private StackedImages overlays;
    private ArrayDeque<Vector3> moves = new ArrayDeque<Vector3>();

    public abstract int getMovementPoints();
    public abstract int getRoadMarchBonus();
    public abstract boolean isUnit();
    public abstract boolean isEnemy(Pawn other);
    public abstract int getAngleOfAttack();
    public abstract int getAttackRangeFrom(Tile tile);

    protected Pawn()
    {
        super();
    }

    public Pawn(TextureRegion pawn, TextureAtlas overlays)
    {
        super(pawn);
        this.overlays = new StackedImages(overlays);
    }

    public Vector3 getLastPosition()
    {
        if ((moves == null) || (moves.size() == 0)) return null;
        return moves.getFirst();
    }

    public Orientation getOrientation()
    {
        return Orientation.fromRotation(getRotation());
    }

    public void moveDone()
    {
        Vector3 v = moves.pop();
        moves.clear();
        moves.push(v);
    }

    public void pushMove(float x, float y, Orientation o)
    {
        float r = ((o == Orientation.KEEP) ? getRotation() : o.r());
        setPosition(x, y, r);
        Vector3 v = new Vector3(x, y, r);
        if ((moves.size() == 0) || (!v.equals(moves.getFirst())))
            moves.push(new Vector3(x, y, r));
    }

    public AnimationSequence getResetMovesAnimation()
    {
        final Vector3 finalPos = moves.getLast();

        AnimationSequence seq = AnimationSequence.get(moves.size() + 1);

        while(moves.size() != 0) {
            seq.addAnimation(MoveToAnimation.get(this, moves.pop(), MOVE_TIME));
        }

        seq.addAnimation(RunnableAnimation.get(this, new Runnable() {
            @Override
            public void run() {
                moves.push(finalPos);
            }
        }));

        return seq;
    }

    public AnimationSequence getMoveAnimation(Vector<Vector3> path)
    {
        int s = path.size();
        final Vector3 finalPos = path.get(s - 1);

        AnimationSequence seq = AnimationSequence.get(s + 1);

        for (Vector3 v : path) {
            seq.addAnimation(MoveToAnimation.get(this, v, MOVE_TIME));
        }

        seq.addAnimation(RunnableAnimation.get(this, new Runnable() {
            @Override
            public void run() {
                moves.push(finalPos);
            }
        }));

        return seq;
    }

    public boolean hasOverlayEnabled()
    {
        return overlays.isEnabled();
    }

    public boolean enableOverlay(int i, boolean enable)
    {
        overlays.enable(i, enable);
        if (enable) return true;
        return hasOverlayEnabled();
    }

    @Override
    public void translate(float dx, float dy)
    {
        super.translate(dx, dy);
        overlays.translate(dx, dy);
    }

    @Override
    public void centerOn(float cx, float cy)
    {
        setPosition((cx - (getWidth() / 2f)), (cy - (getHeight() / 2f)));
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        float cx = x + (getWidth() / 2f);
        float cy = y + (getHeight() / 2f);
        overlays.centerOn(cx, cy);
    }

    @Override
    public void setPosition(float x, float y, float z)
    {
        super.setPosition(x, y, z);
        float cx = x + (getWidth() / 2f);
        float cy = y + (getHeight() / 2f);
        overlays.centerOn(cx, cy);
        overlays.setRotation(z);
    }

    @Override
    public void draw(Batch batch)
    {
        super.draw(batch);
        overlays.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        super.drawDebug(debugShapes);
        overlays.drawDebug(debugShapes);
    }
}
