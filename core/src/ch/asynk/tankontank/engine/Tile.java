package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.StackedImages;

public abstract class Tile implements Drawable
{
    private StackedImages overlays;
    protected ArrayDeque<Pawn> stack;
    private Vector2 center;

    public abstract boolean atLeastOneMove(Pawn pawn);
    public abstract boolean road(Orientation side);
    public abstract int costFrom(Pawn pawn, Orientation side, boolean road);
    public abstract boolean hasTargetsFor(Pawn pawn);
    public abstract boolean isOffMap();
    public abstract boolean blockLineOfSightFrom(Tile tile);

    protected Tile()
    {
    }

    public Tile(float x, float y, TextureAtlas atlas)
    {
        this.stack = null;
        this.center = new Vector2(x, y);
        this.overlays = new StackedImages(atlas);
        this.overlays.centerOn(x, y);
    }

    public Vector2 getCenter()
    {
        return center;
    }

    public int push(Pawn pawn)
    {
        if (stack == null) stack = new ArrayDeque<Pawn>();
        stack.push(pawn);
        return stack.size();
    }

    public int remove(Pawn pawn)
    {
        stack.remove(pawn);
        return stack.size();
    }

    public Pawn getTopPawn()
    {
        if ((stack == null) || (stack.size() == 0)) return null;
        return stack.getFirst();
    }

    public boolean hasUnits()
    {
        if (isEmpty()) return false;
        Iterator<Pawn> itr = stack.iterator();
        while(itr.hasNext()) {
            if (itr.next().isUnit())
                return true;
        }
        return false;
    }

    public boolean isEmpty()
    {
        if (stack == null) return true;
        return (stack.size() == 0);
    }

    public boolean mustBeDrawn()
    {
        if (!isEmpty()) return true;
        return hasOverlayEnabled();
    }

    public boolean disableOverlays()
    {
        overlays.disableAll();
        return !isEmpty();
    }

    public boolean hasOverlayEnabled()
    {
        return overlays.isEnabled();
    }

    public boolean isOverlayEnabled(int i)
    {
        return overlays.isEnabled(i);
    }

    public boolean enableOverlay(int i, boolean enable)
    {
        overlays.enable(i, enable);
        if (enable) return true;
        return mustBeDrawn();
    }

    @Override
    public void draw(Batch batch)
    {
        overlays.draw(batch);
        Pawn pawn = getTopPawn();
        if (pawn != null)
            pawn.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        overlays.drawDebug(debugShapes);
        Pawn pawn = getTopPawn();
        if (pawn != null)
            pawn.drawDebug(debugShapes);
    }
}
