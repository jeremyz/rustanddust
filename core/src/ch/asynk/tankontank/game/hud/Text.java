package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;

public class Text implements Drawable, Disposable
{
    public boolean visible;
    public float x;
    public float y;
    private float ry;
    private String text;
    private BitmapFont font;

    public Text(BitmapFont font, String text)
    {
        this.font = font;
        this.text = text;
        this.visible = true;
    }

    @Override
    public void dispose()
    {
        font.dispose();
    }

    public void setPosition(float x, float y)
    {
        TextBounds b = font.getBounds(text);
        this.x = x;
        this.y = y;
        this.ry = (y + b.height);
    }

    public void write(String text)
    {
        this.text = text;
    }

    public void write(String text, float x, float y)
    {
        this.text = text;
        setPosition(x, y);
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        font.draw(batch, text, x, ry);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        TextBounds b = font.getBounds(text);
        shapes.rect(x, y, b.width, b.height);
    }
}
