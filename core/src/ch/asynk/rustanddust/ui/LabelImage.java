package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LabelImage extends Bg
{
    private Label label;

    public LabelImage(TextureRegion region, BitmapFont font)
    {
        this(region, font, 0f);
    }

    public LabelImage(TextureRegion region, BitmapFont font, float padding)
    {
        this(region, font, padding, Position.MIDDLE_CENTER);
    }

    public LabelImage(TextureRegion region, BitmapFont font, float padding, Position position)
    {
        super(region);
        this.label = new Label(font, padding, position);
        this.label.setParent(this);
    }

    @Override
    public void dispose()
    {
        label.dispose();
    }

    @Override
    public void translate(float dx, float dy)
    {
        super.translate(dx, dy);
        label.update();
    }

    @Override
    public void update()
    {
        super.update();
        label.update();
    }

    public void setLabelPosition(Position position)
    {
        label.setPosition(position, this);
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

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        label.drawDebug(shapes);
    }
}
