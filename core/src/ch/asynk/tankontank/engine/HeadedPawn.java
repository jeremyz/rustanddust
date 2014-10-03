package ch.asynk.tankontank.engine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Image;

import com.badlogic.gdx.math.Vector3;

public abstract class HeadedPawn extends Pawn
{
    private Image head;
    protected Orientation orientation;

    public HeadedPawn(TextureAtlas atlas, String pawn, String head)
    {
        super(atlas, pawn);
        this.head = new Image(atlas.findRegion(head));
        this.orientation = Orientation.KEEP;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        head.dispose();
    }

    @Override
    public float getRotation()
    {
        return orientation.r();
    }

    @Override
    public void setPosition(float x, float y, float z)
    {
        super.setPosition(x, y);
        float cx = x + (getWidth() / 2f);
        float cy = y + (getHeight() / 2f);
        head.centerOn(cx, cy);
        head.setRotation(z);
        this.orientation = Orientation.fromRotation(z);
    }

    @Override
    public void draw(Batch batch)
    {
        head.draw(batch);
        super.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        head.drawDebug(debugShapes);
        super.drawDebug(debugShapes);
    }
}
