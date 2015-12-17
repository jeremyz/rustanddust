package ch.asynk.rustanddust.engine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector3;

public abstract class HeadedPawn extends Pawn
{
    private Sprite head;
    protected Orientation orientation;

    protected HeadedPawn()
    {
        super();
        this.orientation = Orientation.KEEP;
    }

    public HeadedPawn(Faction faction, AtlasRegion body, AtlasRegion head, TextureAtlas overlays)
    {
        super(faction, body, overlays);
        this.head = new Sprite(head);
        this.orientation = Orientation.KEEP;
        this.descr += " " + orientation;
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public void setAlpha(float alpha)
    {
        super.setAlpha(alpha);
        head.setAlpha(alpha);
    }

    @Override
    public float getRotation()
    {
        return orientation.r();
    }

    @Override
    public Orientation getOrientation()
    {
        return orientation;
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        float cx = x + (getWidth() / 2f);
        float cy = y + (getHeight() / 2f);
        head.setPosition((cx - (head.getWidth() / 2f)), (cy - (head.getHeight() / 2f)));
    }

    @Override
    public void setRotation(float z)
    {
        getPosition().z = z;
        head.setRotation(z);
        this.orientation = Orientation.fromRotation(z);
    }

    @Override
    public void setPosition(float x, float y, float z)
    {
        setPosition(x, y);
        setRotation(z);
    }

    @Override
    public void draw(Batch batch)
    {
        sprite.draw(batch);
        head.draw(batch);
        overlays.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        float w = sprite.getWidth();
        float h = sprite.getHeight();
        debugShapes.rect(sprite.getX(), sprite.getY(), (w / 2f), (h / 2f), w, h, sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
        w = head.getWidth();
        h = head.getHeight();
        debugShapes.rect(head.getX(), head.getY(), (w / 2f), (h / 2f), w, h, head.getScaleX(), head.getScaleY(), head.getRotation());
        overlays.drawDebug(debugShapes);
    }
}
