package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Button extends Patch
{
    private Label label;

    public Button(String text, BitmapFont font, NinePatch patch, float padding)
    {
        super(patch);
        label = new Label(font, padding);
        label.write(text);
        setPosition(label.getX(), label.getY(), label.getWidth(), label.getHeight());
    }

    @Override
    public void setPosition(float x, float y, float w, float h)
    {
        rect.set(x, y, w, h);
        if (label != null) label.setPosition(x, y);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        label.dispose();
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
