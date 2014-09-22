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
    private ArrayDeque<Pawn> stack;

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
        return (stack.size() != 0);
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

    public List<Tile> adjacents()
    {
        // FIXME
        System.err.println("adjacents() Not implemented yet");
        return null;
    }

    public void setPosition(float x, float y, float z)
    {
        overlays.setPosition(x, y, z);
    }

    public void draw(Batch batch, float parentAlpha)
    {
        overlays.draw(batch, parentAlpha);
        Pawn pawn = getTopPawn();
        if (pawn != null)
            pawn.draw(batch, parentAlpha);
    }

    public void drawDebug(ShapeRenderer debugShapes)
    {
        overlays.drawDebug(debugShapes);
        Pawn pawn = getTopPawn();
        if (pawn != null)
            pawn.drawDebug(debugShapes);
    }
}
