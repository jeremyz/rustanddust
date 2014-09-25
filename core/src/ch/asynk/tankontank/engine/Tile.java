package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.BasicDrawable;
import ch.asynk.tankontank.engine.gfx.StackedImages;

public abstract class Tile implements BasicDrawable
{
    private StackedImages overlays;
    protected ArrayDeque<Pawn> stack;

    public abstract Tile getNewAt(float x, float y);
    public abstract boolean atLeastOneMove(Pawn pawn);
    public abstract boolean road(Board.Orientation side);
    public abstract int costFrom(Pawn pawn, Board.Orientation side, boolean road);
    public abstract boolean hasTargetsFor(Pawn pawn);

    public Tile(TextureAtlas atlas)
    {
        this.stack = null;
        this.overlays = new StackedImages(atlas);
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

    public boolean mustBeDrawn()
    {
        if (occupied()) return true;
        return hasOverlayEnabled();
    }

    public boolean occupied()
    {
        if (stack == null) return false;
        return (stack.size() != 0);
    }

    public boolean clearOverlays()
    {
        overlays.clear();
        return occupied();
    }

    public boolean hasOverlayEnabled()
    {
        return overlays.isEnabled();
    }

    public boolean enableOverlay(int i, boolean enable)
    {
        overlays.enable(i, enable);
        if (enable) return true;
        return mustBeDrawn();
    }

    @Override
    public void translate(float dx, float dy)
    {
        overlays.translate(dx, dy);
    }

    @Override
    public void setPosition(float x, float y, float z)
    {
        overlays.setPosition(x, y, z);
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
