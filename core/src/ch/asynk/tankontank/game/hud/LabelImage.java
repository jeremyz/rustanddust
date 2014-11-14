package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LabelImage extends Bg
{
    private Label label;
    public Position labelPosition;

    public LabelImage(TextureRegion region, BitmapFont font)
    {
        this(region, font, 0f);
    }

    public LabelImage(TextureRegion region, BitmapFont font, float padding)
    {
        super(region);
        this.label = new Label(font, padding);
        this.labelPosition = Position.MIDDLE_CENTER;
    }

    @Override
    public void dispose()
    {
        label.dispose();
    }

    public void setLabelPosition(Position position)
    {
        labelPosition = position;
        setPosition(rect.x, rect.y);
    }

    public void setPosition(float x, float y)
    {
        set(x, y, getWidth(), getHeight());
        label.setPosition(labelPosition.getX(this, label.getWidth()), labelPosition.getY(this, label.getHeight()));
    }

    public void write(String text)
    {
        this.label.write(text);
        setPosition(getX(), getY());
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        label.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        label.drawDebug(shapes);
    }
}
