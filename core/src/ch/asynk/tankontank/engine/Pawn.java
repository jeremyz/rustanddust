package ch.asynk.tankontank.engine;

import java.util.Iterator;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Moveable;
import ch.asynk.tankontank.engine.gfx.StackedImages;
import ch.asynk.tankontank.engine.gfx.animations.MoveToAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;

public abstract class Pawn implements Moveable, Disposable
{
    public interface PawnType
    {
    }

    public class Engagement
    {
        Pawn target;
        int distance;
        boolean isClear;
        boolean isFlank;
        public String calculus;

        public String toString()
        {
            return "engage : " + target + " distance:" + distance + " clear:" + isClear + " flank:" + isFlank + " " + calculus;
        }

        public void reset()
        {
            target = null;
            distance = 0;;
            isClear = false;
            isFlank = false;
        }
    }

    public class Movement
    {
        Tile from;
        Tile to;
        int distance;
        public int cost;
        boolean roadMarch;
        public boolean entryMove;
        Orientation orientation;

        public String toString()
        {
            if (from == null)
                return "move : reinforcement -> [" + to.col + ";" + to.row + ";" + orientation + "] dist:" + distance + " cost:" + cost + " road:" + roadMarch + " entry:" + entryMove;
            else
                return "move : [" + from.col + ";" + from.row + "] -> [" + to.col + ";" + to.row + ";" + orientation + "] dist:" + distance + " cost:" + cost + " road:" + roadMarch + " entry:" + entryMove;
        }

        public void setRotation(Tile tile, Orientation o)
        {
            from = tile;
            to = tile;
            cost = 0;
            roadMarch = false;
            entryMove = false;
            orientation = o;
        }

        public void reset()
        {
            from = null;
            to = null;
            cost = Integer.MAX_VALUE;
            roadMarch = false;
            entryMove = false;
            orientation = Orientation.KEEP;
        }

        public boolean isComplete()
        {
            return ((from != null) && (to != null));
        }
    }

    private static final float MOVE_TIME = 0.4f;

    private Vector3 position;
    private Vector3 prevPosition;
    private Tile tile;
    private Tile prevTile;
    protected Faction faction;
    protected String descr;
    private Image image;
    private StackedImages overlays;
    public Engagement engagement = new Engagement();
    public Movement movement= new Movement();

    public abstract int getMovementPoints();
    public abstract int getRoadMarchBonus();
    public abstract int getAngleOfAttack();
    public abstract int getFlankSides();
    public abstract int getEngagementRangeFrom(Tile tile);

    public abstract boolean isUnit();
    public abstract boolean isA(PawnType type);
    public abstract boolean isHq();
    public abstract boolean isHqOf(Pawn other);
    public abstract boolean isHardTarget();

    public abstract boolean canMove();
    public abstract boolean canRotate();
    public abstract boolean canEngage();
    public abstract boolean canEngage(Pawn other);
    public abstract boolean canAssistEngagementWithoutLos();

    public abstract void move();
    public abstract void engage();

    public abstract void revertLastMove();

    protected Pawn()
    {
        this.tile = null;
        this.prevTile = null;
        this.position = new Vector3(0f, 0f, 0f);
        this.prevPosition = new Vector3(0f, 0f, 0f);
    }

    public Pawn(Faction faction, String name, TextureAtlas pawns, TextureAtlas overlays)
    {
        this();
        this.faction = faction;
        this.descr = descr;
        this.image = new Image(pawns.findRegion(name));
        this.overlays = new StackedImages(overlays);
    }

    @Override
    public String toString()
    {
        return descr;
    }

    @Override
    public void dispose()
    {
        image.dispose();
    }

    public void reset()
    {
        engagement.reset();
        movement.reset();
    }

    public void enterBoard(Tile to, Orientation o)
    {
        movement.to = to;
        movement.from = null;
        movement.entryMove = true;
        movement.orientation = o;
        movement.cost = to.costFrom(this, o);
        move();
    }

    public boolean is(Faction faction)
    {
        return (this.faction == faction);
    }

    public boolean isEnemy(Faction other)
    {
        return faction.isEnemy(other);
    }

    public boolean isEnemy(Pawn other)
    {
        return faction.isEnemy(other.faction);
    }

    public boolean isFlankAttack()
    {
        return (engagement.isClear && engagement.isFlank);
    }

    public Tile getTile()
    {
        return tile;
    }

    public Tile getPreviousTile()
    {
        return prevTile;
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public Vector3 getPreviousPosition()
    {
        return prevPosition;
    }

    private void revertPosition()
    {
        this.tile = this.prevTile;
        this.prevTile = null;
        position.set(prevPosition);
        prevPosition.set(0f, 0f, 0f);
        setPosition(position.x, position.y, position.z);
    }

    public Vector2 getCenter()
    {
        float x = (getX() + (getWidth() / 2f));
        float y = (getY() + (getHeight() / 2f));
        return new Vector2(x, y);
    }

    public Vector2 getPosAt(Tile tile, Vector2 pos)
    {
        float x = (tile.getX() - (getWidth() / 2f));
        float y = (tile.getY() - (getHeight() / 2f));
        if (pos == null)
            return new Vector2(x, y);
        else
            pos.set(x, y);
        return pos;
    }

    public void setOnTile(Tile tile, float z)
    {
        this.prevTile = this.tile;
        this.tile = tile;
        float x = (tile.getX() - (getWidth() / 2f));
        float y = (tile.getY() - (getHeight() / 2f));
        setPosition(x, y, z);
    }

    @Override
    public float getX()
    {
        return image.getX();
    }

    @Override
    public float getY()
    {
        return image.getY();
    }

    @Override
    public float getWidth()
    {
        return image.getWidth();
    }

    @Override
    public float getHeight()
    {
        return image.getHeight();
    }

    @Override
    public float getRotation()
    {
        return image.getRotation();
    }

    public Orientation getOrientation()
    {
        return Orientation.fromRotation(getRotation());
    }

    public void centerOn(float x, float y)
    {
        setPosition((x - (getWidth() / 2f)), (y - (getHeight() / 2f)));
    }

    @Override
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

    @Override
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

    public AnimationSequence getRotateAnimation(float z, int size)
    {
        prevPosition.set(position);
        AnimationSequence seq = AnimationSequence.get(1 + size);
        seq.addAnimation(MoveToAnimation.get(this, position.x, position.y, z, MOVE_TIME));

        return seq;
    }

    public AnimationSequence getMoveAnimation(Iterator<Vector3> vectors, int size)
    {
        prevPosition.set(position);
        AnimationSequence seq = AnimationSequence.get(size);
        while (vectors.hasNext())
            seq.addAnimation(MoveToAnimation.get(this, vectors.next(), MOVE_TIME));

        return seq;
    }

    public AnimationSequence getRevertLastMoveAnimation(int size)
    {
        AnimationSequence seq = AnimationSequence.get(2 + size);
        seq.addAnimation(MoveToAnimation.get(this, prevPosition, MOVE_TIME));
        seq.addAnimation(RunnableAnimation.get(this, new Runnable() {
            @Override
            public void run() {
                revertPosition();
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
