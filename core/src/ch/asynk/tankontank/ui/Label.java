package ch.asynk.tankontank.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class Label extends Widget
{
    private BitmapFont font;
    private float dx;
    private float dy;
    protected String text;

    public Label(BitmapFont font)
    {
        this(font, 0f);
    }

    public Label(BitmapFont font, float padding)
    {
        this(font, padding, Position.MIDDLE_CENTER);
    }

    public Label(BitmapFont font, float padding, Position position)
    {
        super();
        this.font = font;
        this.padding = padding;
        this.position = position;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void translate(float dx, float dy)
    {
        setPosition((rect.x + dx), (rect.y + dy));
    }

    @Override
    public void setPosition(float x, float y)
    {
        TextBounds b = font.getMultiLineBounds((text == null) ? "" : text);
        setPosition(x, y, (b.width + (2 * padding)), (b.height + (2 * padding)));
        this.dx = (x + padding);
        this.dy = (y + padding + b.height);
    }

    public void write(String text)
    {
        this.text = text;
        setPosition(position);
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
        font.drawMultiLine(batch, text, dx, dy);
    }
}