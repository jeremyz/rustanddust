package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

import ch.asynk.tankontank.engine.gfx.Drawable;

public class Label extends Widget
{
    private BitmapFont font;
    private float padding;
    private float rx;
    private float ry;
    protected String text;

    public Label(BitmapFont font)
    {
        this(font, 0f);
    }

    public Label(BitmapFont font, float padding)
    {
        this.font = font;
        this.padding = padding;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void setPosition(float x, float y)
    {
        TextBounds b = font.getMultiLineBounds((text == null) ? "" : text);
        setPosition(x, y, (b.width + (2 * padding)), (b.height + (2 * padding)));
        this.rx = x + (padding);
        this.ry = (y + padding + b.height);
    }

    public void write(String text)
    {
        this.text = text;
        setPosition(rect.x, rect.y);
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
        font.drawMultiLine(batch, text, rx, ry);
    }
}
