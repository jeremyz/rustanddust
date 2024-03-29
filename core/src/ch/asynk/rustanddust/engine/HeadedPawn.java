package ch.asynk.rustanddust.engine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class HeadedPawn extends Pawn
{
    private Sprite turret;
    private Sprite body;
    private float turretR;
    private boolean canAim;
    private boolean selectable;
    private boolean selected;
    protected Orientation orientation;

    protected HeadedPawn()
    {
        super();
        this.orientation = Orientation.KEEP;
    }

    public HeadedPawn(Faction faction, AtlasRegion chit, AtlasRegion body, AtlasRegion turret, TextureAtlas overlays, boolean selectable)
    {
        super(faction, chit, overlays);
        this.body = new Sprite(body);
        this.turret = ((turret == null) ? null : new Sprite(turret));
        this.turretR = 0f;
        this.orientation = Orientation.KEEP;
        this.descr += " " + orientation;
        this.canAim = (turret != null);
        this.selectable = selectable;
        this.selected = !selectable;
    }

    public void select(boolean s)
    {
        if (!selectable) return;
        this.selected = s;
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public boolean canAim()
    {
        return canAim;
    }

    @Override
    public void setAlpha(float alpha)
    {
        super.setAlpha(alpha);
        body.setAlpha(alpha);
        if (canAim) turret.setAlpha(alpha);
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
    public float getAiming()
    {
        return turretR;
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        float cx = x + (getWidth() / 2f);
        float cy = y + (getHeight() / 2f);
        body.setPosition((cx - (body.getWidth() / 2f)), (cy - (body.getHeight() / 2f)));
        if (canAim) turret.setPosition((cx - (turret.getWidth() / 2f)), (cy - (turret.getHeight() / 2f)));
    }

    @Override
    public void setRotation(float z)
    {
        getPosition().z = z;
        body.setRotation(z);
        if (canAim) turret.setRotation(z + turretR);
        this.orientation = Orientation.fromRotation(z);
    }

    @Override
    public void aimAt(float r)
    {
        if (canAim)
            turret.setRotation(body.getRotation() + r);
        else {
            float d = (r - turretR);
            body.setRotation(body.getRotation() + d);
        }
        turretR = r;
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
        if (selected) sprite.draw(batch);
        body.draw(batch);
        if (canAim) turret.draw(batch);
        if (selected) overlays.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        float w = sprite.getWidth();
        float h = sprite.getHeight();
        if (selected) debugShapes.rect(sprite.getX(), sprite.getY(), (w / 2f), (h / 2f), w, h, sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
        w = body.getWidth();
        h = body.getHeight();
        debugShapes.rect(body.getX(), body.getY(), (w / 2f), (h / 2f), w, h, body.getScaleX(), body.getScaleY(), body.getRotation());
        if (canAim) {
            w = turret.getWidth();
            h = turret.getHeight();
            debugShapes.rect(turret.getX(), turret.getY(), (w / 2f), (h / 2f), w, h, turret.getScaleX(), turret.getScaleY(), turret.getRotation());
        }
        if (selected) overlays.drawDebug(debugShapes);
    }
}
