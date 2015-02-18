package ch.asynk.tankontank.engine;

import java.util.Iterator;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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

    public interface PawnId
    {
    }

    private static final float MOVE_TIME = 0.4f;

    private Vector3 position;
    private Vector3 prevPosition;
    private Tile tile;
    private Tile prevTile;
    protected Faction faction;
    protected String descr;
    private Sprite sprite;
    private StackedImages overlays;
    protected Attack attack;
    protected Move move;

    public abstract int getMovementPoints();
    public abstract int getRoadMarchBonus();
    public abstract int getAngleOfAttack();
    public abstract int getFlankSides();
    public abstract int getEngagementRangeFrom(Tile tile);
    public abstract int getDefense(Tile tile);

    public abstract boolean isUnit();
    public abstract boolean isA(PawnId id);
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
        this.attack = new Attack(this);
    }

    public Pawn(Faction faction, String name, TextureAtlas pawns, TextureAtlas overlays)
    {
        this();
        this.faction = faction;
        this.descr = descr;
        this.sprite = new Sprite(pawns.findRegion(name));
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
    }

    @Override
    public Faction getFaction()
    {
        return faction;
    }

    public void reset()
    {
        move = null;
        attack.reset();
    }

    public void move(Move move)
    {
        switch(move.type)
        {
            case REGULAR:
                if ((this.move != null) && (!this.move.isEnter()))
                    throw new RuntimeException("try to override an existing move instance");
                break;
            case ENTER:
                if (this.move != null)
                    throw new RuntimeException("try to override an existing move instance");
                break;
            case SET:
                break;
            default:
                throw new RuntimeException("unsupported MoveType");
        }

        this.move = move;
        move();
    }

    public void setAttack(Pawn target, int distance)
    {
        attack.reset();
        attack.target = target;
        attack.distance = distance;
    }

    public boolean justEntered()
    {
        return ((move != null) && move.isEnter());
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
        return (attack.isClear && attack.isFlank);
    }

    public int attackDistance()
    {
        return attack.distance;
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

    public float getCenterX()
    {
        return (getX() + (getWidth() / 2f));
    }

    public float getCenterY()
    {
        return (getY() + (getHeight() / 2f));
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
    public void setAlpha(float alpha)
    {
        sprite.setAlpha(alpha);
        overlays.setAlpha(alpha);
    }

    @Override
    public float getX()
    {
        return sprite.getX();
    }

    @Override
    public float getY()
    {
        return sprite.getY();
    }

    @Override
    public float getWidth()
    {
        return sprite.getWidth();
    }

    @Override
    public float getHeight()
    {
        return sprite.getHeight();
    }

    @Override
    public float getRotation()
    {
        return sprite.getRotation();
    }

    public Orientation getOrientation()
    {
        return Orientation.fromRotation(getRotation());
    }

    public void translate(float dx, float dy)
    {
        setPosition((getX() + dx), (getY() + dy));
    }

    public void centerOn(float x, float y)
    {
        setPosition((x - (getWidth() / 2f)), (y - (getHeight() / 2f)));
    }

    @Override
    public void setPosition(float x, float y)
    {
        position.set(x, y, 0f);
        sprite.setPosition(x, y);
        float cx = x + (getWidth() / 2f);
        float cy = y + (getHeight() / 2f);
        overlays.centerOn(cx, cy);
    }

    public void setRotation(float z)
    {
        position.z = z;
        sprite.setRotation(z);
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

    public AnimationSequence getMoveAnimation(Iterator<Vector3> vectors, int size, MoveToAnimation.MoveToAnimationCb cb)
    {
        prevPosition.set(position);
        AnimationSequence seq = AnimationSequence.get(size);
        while (vectors.hasNext())
            seq.addAnimation(MoveToAnimation.get(this, vectors.next(), MOVE_TIME, cb));

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
        sprite.draw(batch);
        overlays.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        float w = sprite.getWidth();
        float h = sprite.getHeight();
        debugShapes.rect(sprite.getX(), sprite.getY(), (w / 2f), (h / 2f), w, h, sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
        overlays.drawDebug(debugShapes);
    }
}
