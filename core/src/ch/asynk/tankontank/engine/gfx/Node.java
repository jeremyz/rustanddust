package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.Layer;

public interface Node extends Drawable
{
    public void act(float delta);

    public void clear();

    public void setLayer(Layer layer);
}
