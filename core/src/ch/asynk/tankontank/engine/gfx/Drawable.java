package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Drawable
{
    public void draw(Batch batch);

    public void drawDebug(ShapeRenderer debugShapes);
}
