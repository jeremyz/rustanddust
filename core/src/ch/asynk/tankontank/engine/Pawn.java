package ch.asynk.tankontank.engine;

import java.util.ArrayList;
import java.util.ArrayDeque;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.StackedImages;
import ch.asynk.tankontank.engine.gfx.animations.MoveToAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;

public abstract class Pawn implements Drawable, Disposable
{
    private static final float MOVE_TIME = 0.4f;

    private Vector3 position = new Vector3(0f, 0f, 0f);
    private Image image;
    private StackedImages overlays;

    public abstract int getMovementPoints();
    public abstract int getRoadMarchBonus();
    public abstract boolean isUnit();
    public abstract boolean isEnemy(Pawn other);
    public abstract boolean canAttack(Pawn other);
    public abstract int getAngleOfAttack();
    public abstract int getAttackRangeFrom(Tile tile);

    protected Pawn()
    {
    }

    public Pawn(TextureAtlas atlas, String pawn)
    {
        image = new Image(atlas.findRegion(pawn));
        this.overlays = new StackedImages(atlas);
    }

    @Override
    public void dispose()
    {
        image.dispose();
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public Vector2 getCenter()
    {
        float x = (image.getX() + (image.getWidth() / 2f));
        float y = (image.getY() + (image.getHeight() / 2f));
        return new Vector2(x, y);
    }

    public Vector2 getPosAt(Vector2 center, Vector2 pos)
    {
        float x = (center.x - (image.getWidth() / 2f));
        float y = (center.y - (image.getHeight() / 2f));
        if (pos == null)
            return new Vector2(x, y);
        else
            pos.set(x, y);
        return pos;
    }

    public float getX()
    {
        return image.getX();
    }

    public float getY()
    {
        return image.getY();
    }

    public float getWidth()
    {
        return image.getWidth();
    }

    public float getHeight()
    {
        return image.getHeight();
    }

    public float getRotation()
    {
        return image.getRotation();
    }

    public Orientation getOrientation()
    {
        return Orientation.fromRotation(getRotation());
    }

    public void setPosition(float x, float y)
    {
        position.set(x, y, 0f);
        image.setPosition(x, y);
        float cx = x + (getWidth() / 2f);
        float cy = y + (getHeight() / 2f);
        overlays.centerOn(cx, cy);
    }

    public void setRotation(float z)
    {
        position.z = z;
        image.setRotation(z);
        overlays.setRotation(z);
    }

    public void setPosition(float x, float y, float z)
    {
        setPosition(x, y);
        setRotation(z);
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

    public AnimationSequence getMoveAnimation(ArrayList<Vector3> path)
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
                setPosition(finalPos.x, finalPos.y, finalPos.z);
            }
        }));

        return seq;
    }

    @Override
    public void draw(Batch batch)
    {
        image.draw(batch);
        overlays.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        image.drawDebug(debugShapes);
        overlays.drawDebug(debugShapes);
    }
}
