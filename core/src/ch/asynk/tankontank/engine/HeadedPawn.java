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
    protected Board.Orientation orientation;

    public HeadedPawn(TextureRegion region, TextureRegion head, TextureAtlas atlas)
    {
        super(region, atlas);
        this.head = new Image(head);
        this.orientation = Board.Orientation.KEEP;
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
    public void translate(float dx, float dy)
    {
        super.translate(dx, dy);
        head.translate(dx, dy);
    }

    @Override
    public void setPosition(float x, float y, float z)
    {
        super.setPosition(x, y);
        float cx = x + (getWidth() / 2f) - (head.getWidth() / 2f);
        float cy = y + (getHeight() / 2f) - (head.getHeight() / 2f);
        head.setPosition(cx, cy);
        head.setRotation(z);
        this.orientation = Board.Orientation.fromRotation(z);
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
