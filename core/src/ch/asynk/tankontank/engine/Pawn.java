package ch.asynk.tankontank.engine;

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
    private ArrayDeque<Vector3> path = new ArrayDeque<Vector3>();

    public abstract int getMovementPoints();
    public abstract int getRoadMarchBonus();
    public abstract boolean isEnemy(Pawn other);
    public abstract int getAngleOfAttack();
    public abstract int getAttackRangeFrom(Tile tile);

    public Pawn(TextureRegion pawn, TextureAtlas overlays)
    {
        super(pawn);
        this.overlays = new StackedImages(overlays);
    }

    public Vector3 getLastPosition()
    {
        if ((path == null) || (path.size() == 0)) return null;
        return path.getFirst();
    }

    public Board.Orientation getOrientation()
    {
        return Board.Orientation.fromRotation(getRotation());
    }

    public void moveDone()
    {
        Vector3 v = path.pop();
        path.clear();
        path.push(v);
    }

    public void pushMove(float x, float y, Board.Orientation o)
    {
        float r = ((o == Board.Orientation.KEEP) ? getRotation() : o.r());
        setPosition(x, y, r);
        Vector3 v = new Vector3(x, y, r);
        if ((path.size() == 0) || (!v.equals(path.getFirst())))
            path.push(new Vector3(x, y, r));
    }

    public AnimationSequence getResetMovesAnimation()
    {
        final Vector3 finalPos = path.getLast();

        AnimationSequence seq = AnimationSequence.get(path.size() + 1);

        while(path.size() != 0) {
            seq.addAnimation(MoveToAnimation.get(this, path.pop(), MOVE_TIME));
        }

        seq.addAnimation(RunnableAnimation.get(this, new Runnable() {
            @Override
            public void run() {
                path.push(finalPos);
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
        overlays.setPosition(x, y);
    }

    @Override
    public void setPosition(float x, float y, float z)
    {
        super.setPosition(x, y, z);
        overlays.setPosition(x, y, z);
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
