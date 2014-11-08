package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class LabelButton extends Button
{
    private Label label;

    public LabelButton(TextureAtlas atlas, String base, BitmapFont font, String text)
    {
        super(atlas, base);
        this.label = new Label(font, text);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        label.dispose();
    }

    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        setLabelPosition((x + ((getWidth() - label.getWidth()) / 2)), (y + ((getHeight() - label.getHeight()) / 2)));
    }

    public void setLabelPosition(float x, float y)
    {
        label.setPosition(x, y);
    }

    public void write(String text)
    {
        this.label.write(text);
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        label.draw(batch);
    }
}
