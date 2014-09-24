package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface BasicDrawable
{
    public void translate(float dx, float dy);

    public void setPosition(float x, float y, float r);

    public void draw(Batch batch);

    public void drawDebug(ShapeRenderer debugShapes);
}
