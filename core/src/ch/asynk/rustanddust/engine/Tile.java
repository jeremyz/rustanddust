package ch.asynk.rustanddust.engine;

import java.util.Iterator;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.util.IterableStack;
import ch.asynk.rustanddust.engine.gfx.Drawable;
import ch.asynk.rustanddust.engine.gfx.StackedImages;

public abstract class Tile implements Drawable, Disposable, Iterable<Pawn>
{
    public interface TileTerrain
    {
    }

    public enum Objective
    {
        NONE,
        PERSISTENT,
        VERSATILE,
        FINAL
    }

    protected int col;
    protected int row;
    protected float x;
    protected float y;
    private StackedImages overlays;
    private IterableStack<Pawn> stack;

    protected Faction curFaction;
    protected Faction prevFaction;
    protected Objective objective;

    public abstract int defense();
    public abstract int exitCost();
    public abstract int costFrom(Pawn pawn, Orientation side);

    public abstract boolean isOffMap();
    public abstract boolean isA(TileTerrain terrain);
    public abstract boolean roadFrom(Orientation side);
    public abstract boolean atLeastOneMove(Pawn pawn);
    public abstract boolean blockLineOfSight(Tile from, Tile to);

    protected Tile(int col, int row, int capacity, Faction defaultFaction)
    {
        this.col = col;
        this.row = row;
        this.stack = new IterableStack<Pawn>(capacity);
        this.curFaction = defaultFaction;
        this.prevFaction = defaultFaction;
        this.objective = Objective.NONE;
    }

    public Tile(float x, float y, int col, int row, int capacity, Faction defaultFaction, TextureAtlas atlas)
    {
        this(col, row, capacity, defaultFaction);
        this.x = x;
        this.y = y;
        this.overlays = new StackedImages(atlas);
        this.overlays.centerOn(x, y);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getCol() { return col; }
    public int getRow() { return row; }

    @Override
    public String toString()
    {
        return String.format("(%d;%d) %s", col, row, (isOffMap() ? "x" : ""));
    }

    public String toShort()
    {
        return String.format("(%d;%d)", col, row);
    }

    @Override
    public void dispose()
    {
        stack.clear();
        overlays.dispose();
    }

    // STACK

    public boolean isEmpty()
    {
        return stack.isEmpty();
    }

    @Override
    public Iterator<Pawn> iterator()
    {
        return stack.iterator();
    }

    public int push(Pawn pawn)
    {
        if (!stack.contains(pawn))
            stack.add(pawn);
        return stack.size();
    }

    public int remove(Pawn pawn)
    {
        stack.remove(pawn);
        return stack.size();
    }

    protected Pawn getTopPawn()
    {
        return stack.getTop();
    }

    public boolean hasUnits()
    {
        if (isEmpty()) return false;
        for (Pawn p : this) {
            if (p.isUnit())
                return true;
        }
        return false;
    }

    // OBJECTIVE

    public void setObjective(Faction faction, Objective objective)
    {
        this.curFaction = faction;
        this.prevFaction = faction;
        this.objective = objective;
    }

    public boolean isPersistent()
    {
        return ((objective == Objective.PERSISTENT) || (objective == Objective.FINAL));
    }

    public boolean isFinal()
    {
        return (objective == Objective.FINAL);
    }

    public Faction belongsTo()
    {
        return curFaction;
    }

    public boolean belongsTo(Faction faction)
    {
        return (faction == curFaction);
    }

    public boolean isObjective()
    {
        return (objective != Objective.NONE);
    }

    public boolean isOwnedObjective(Faction faction)
    {
        return (isObjective() && belongsTo(faction));
    }

    public boolean isObjectiveFor(Pawn pawn)
    {
        if (!isObjective())
            return false;

        if (belongsTo(pawn.getFaction()))
            return false;

        return isPersistent();
    }

    public boolean claim(Faction faction)
    {
        if (belongsTo(faction))
            return false;

        if (isFinal() && (curFaction != prevFaction))
            return false;

        prevFaction = curFaction;
        curFaction = faction;
        return true;
    }

    public boolean unclaim()
    {
        if (isPersistent())
            return false;
        revertClaim();
        return true;
    }

    public Faction revertClaim()
    {
        curFaction = prevFaction;
        return curFaction;
    }

    // OVERLAYS

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
        if (i >= 0) {
            overlays.enable(i, enable);
            if (enable) return true;
        }
        return mustBeDrawn();
    }

    public boolean enableOverlay(int i, boolean enable, float r)
    {
        if (i >= 0) {
            overlays.enable(i, enable);
            overlays.rotate(i, r);
            if (enable) return true;
        }
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
