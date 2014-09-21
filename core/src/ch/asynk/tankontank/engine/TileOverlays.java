package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.StackedSpriteNode;

public abstract class TileOverlays implements Tile
{
    private StackedSpriteNode overlays;
    private ArrayDeque<Pawn> stack;

    public TileOverlays(TextureAtlas atlas)
    {
        this.stack = null;
        this.overlays = new StackedSpriteNode(atlas);
    }

    @Override
    public int push(Pawn pawn)
    {
        if (stack == null) stack = new ArrayDeque<Pawn>();
        stack.push(pawn);
        return stack.size();
    }

    @Override
    public int remove(Pawn pawn)
    {
        stack.remove(pawn);
        return stack.size();
    }

    @Override
    public Pawn getTopPawn()
    {
        if ((stack == null) || (stack.size() == 0)) return null;
        return stack.getFirst();
    }

    @Override
    public boolean mustBeDrawn()
    {
        if (occupied()) return true;
        return hasOverlayEnabled();
    }

    @Override
    public boolean occupied()
    {
        return (stack.size() != 0);
    }

    @Override
    public boolean hasOverlayEnabled()
    {
        return overlays.isEnabled();
    }

    @Override
    public void enableOverlay(int i, boolean enable)
    {
        overlays.enable(i, enable);
    }

    @Override
    public List<Tile> adjacents()
    {
        // FIXME
        System.err.println("adjacents() Not implemented yet");
        return null;
    }

    @Override
    public void setPosition(float x, float y, float z)
    {
        overlays.setPosition(x, y, z);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        overlays.draw(batch, parentAlpha);
        Pawn pawn = getTopPawn();
        if (pawn != null)
            pawn.draw(batch, parentAlpha);
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
