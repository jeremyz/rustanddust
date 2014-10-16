package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class Msg extends Bg
{
    private BitmapFont font;
    private String text;
    private int padding;
    private float x;
    private float y;
    private float duration;
    private float elapsed;
    private boolean visible;

    public Msg(BitmapFont font, TextureRegion region)
    {
        super(region);
        this.font = font;
    }

    public void write(String text, float duration)
    {
        TextBounds b = font.getBounds(text);
        write(text, ((Gdx.graphics.getWidth() - b.width) / 2), ((Gdx.graphics.getHeight() - b.height) / 2), duration, 10);
    }

    public void write(String text, float x, float y, float duration, int padding)
    {
        this.text = text;
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.padding = padding;
        this.visible = true;
        this.elapsed = 0f;
        TextBounds b = font.getBounds(text);
        set(x, y, (b.width + (padding * 2)), (b.height + (padding * 2)));
    }

    public void animate(float delta)
    {
        if (!visible) return;
        elapsed += delta;
        if (elapsed >= duration)
            visible = false;
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        font.draw(batch, text, (x + padding), (rect.y + rect.height - padding));
    }
}
