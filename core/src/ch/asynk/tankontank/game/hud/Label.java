package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Drawable;

public class Label implements Drawable, Disposable
{
    public boolean visible;
    public float x;
    public float y;
    private float ry;
    private String text;
    private BitmapFont font;

    public Label(BitmapFont font, String text)
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

    public float getWidth()
    {
        TextBounds b = getBounds();
        return b.width;
    }

    public float getHeight()
    {
        TextBounds b = getBounds();
        return b.height;
    }

    public void setPosition(float x, float y)
    {
        TextBounds b = getBounds();
        this.x = x;
        this.y = y;
        this.ry = (y + b.height);
    }

    public TextBounds getBounds()
    {
        return font.getMultiLineBounds(text);
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
        font.drawMultiLine(batch, text, x, ry);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        TextBounds b = getBounds();
        shapes.rect(x, y, b.width, b.height);
    }
}
