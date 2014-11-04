package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ch.asynk.tankontank.engine.gfx.Image;

public class TextImage extends Image
{
    private Text text;

    public TextImage(TextureRegion region, BitmapFont font, String text)
    {
        super(region);
        this.text = new Text(font, text);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        text.dispose();
    }

    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        setTextPosition((x + ((getWidth() - text.getWidth()) / 2)), (y + ((getHeight() - text.getHeight()) / 2)));
    }

    public void setTextPosition(float x, float y)
    {
        text.setPosition(x, y);
    }

    public void write(String text)
    {
        this.text.write(text);
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        text.draw(batch);
    }
}
