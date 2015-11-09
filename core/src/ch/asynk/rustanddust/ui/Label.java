package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Label extends Widget
{
    private BitmapFont font;
    private GlyphLayout layout;
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
        this.layout = new GlyphLayout();
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
        this.layout.setText(font, (text == null) ? "" : text);
        setPosition(x, y, (layout.width + (2 * padding)), (layout.height + (2 * padding)));
        this.dx = (x + padding);
        this.dy = (y + padding + layout.height);
    }

    public void write(String text)
    {
        this.text = text;
        compute();
        setPosition(position);
    }

    public void write(String text, float x, float y)
    {
        this.text = text;
        setPosition(x, y);
    }

    private void compute()
    {
        this.layout.setText(font, (text == null) ? "" : text);
        this.rect.width = (layout.width + (2 * padding));
        this.rect.height = (layout.height + (2 * padding));
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        font.draw(batch, layout, dx, dy);
    }
}
