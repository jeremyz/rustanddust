package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayDeque;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.StackedImages;

public abstract class Tile implements Drawable, Disposable
{
    protected int col;
    protected int row;
    protected float x;
    protected float y;
    private StackedImages overlays;
    private ArrayDeque<Pawn> stack;

    public abstract int defenseFor(Pawn target, List<Pawn> foes);
    public abstract int costFrom(Pawn pawn, Orientation side, boolean road);

    public abstract boolean isOffMap();
    public abstract boolean road(Orientation side);
    public abstract boolean atLeastOneMove(Pawn pawn);
    public abstract boolean blockLineOfSightFrom(Tile tile);

    protected Tile()
    {
    }

    public Tile(float x, float y, int col, int row, TextureAtlas atlas)
    {
        this.stack = null;
        this.x = x;
        this.y = y;
        this.col = col;
        this.row = row;
        this.overlays = new StackedImages(atlas);
        this.overlays.centerOn(x, y);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getCol() { return col; }
    public int getRow() { return row; }

    @Override
    public void dispose()
    {
        if(stack != null) stack.clear();
        overlays.dispose();
    }

    public boolean isEmpty()
    {
        if (stack == null) return true;
        return stack.isEmpty();
    }

    public Iterator<Pawn> getIterator()
    {
        return stack.iterator();
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
        if (isEmpty()) return null;
        return stack.getFirst();
    }

    public boolean hasUnits()
    {
        if (isEmpty()) return false;
        Iterator<Pawn> itr = getIterator();
        while(itr.hasNext()) {
            if (itr.next().isUnit())
                return true;
        }
        return false;
    }

    public boolean hasTargetsFor(Pawn pawn)
    {
        if (isEmpty()) return false;
        Iterator<Pawn> itr = getIterator();
        while(itr.hasNext()) {
            Pawn target = itr.next();
            if (pawn.canAttack(target)) return true;
        }

        return false;
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

    public boolean enableOverlay(int i, boolean enable, float r)
    {
        overlays.enable(i, enable);
        overlays.rotate(i, r);
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
