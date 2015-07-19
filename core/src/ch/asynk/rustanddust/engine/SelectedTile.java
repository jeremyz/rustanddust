package ch.asynk.rustanddust.engine;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Drawable;
import ch.asynk.rustanddust.engine.gfx.Animation;
import ch.asynk.rustanddust.engine.gfx.animations.Sprites;

public class SelectedTile implements Disposable, Drawable, Animation
{
    private Sprites sprites;
    public Tile tile;
    public boolean visible;
    public float x;
    public float y;
    private float elapsed;
    private int frame;
    private float[] seq;

    public SelectedTile(Texture texture, float[] seq)
    {
        this.sprites = new Sprites(texture, seq.length, 1);
        this.visible = false;
        this.tile = null;
        this.elapsed = 0f;
        this.seq = seq;
    }

    public void hide()
    {
        tile = null;
        visible = false;
    }

    public void set(Tile tile)
    {
        this.visible = true;
        this.tile = tile;
        this.frame = 0;
        this.elapsed = 0f;
        this.x = (tile.getX() - (sprites.width / 2f));
        this.y = (tile.getY() - (sprites.height / 2f));
    }

    public void dispose()
    {
        sprites.dispose();
    }

    @Override
    public boolean animate(float delta)
    {
        if (visible) {
            elapsed += delta;
            if (elapsed > seq[frame]) {
                frame = ((frame + 1) % sprites.frames.length);
                elapsed = 0f;
            }
        }
        return false;
    }

    @Override
    public void draw(Batch batch)
    {
        if (visible)
            batch.draw(sprites.frames[frame], x, y);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        if (visible)
            debugShapes.rect(x, y, sprites.width, sprites.height);
    }
}
