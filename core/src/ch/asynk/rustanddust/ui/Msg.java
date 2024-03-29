package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Msg extends Patch
{
    private LabelStack label;

    public Msg(BitmapFont font, NinePatch patch, float fontSize)
    {
        super(patch);
        label = new LabelStack(font, fontSize);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        label.dispose();
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
        label.translate(dx, dy);
    }

    public void write(String text, float duration)
    {
        label.write(text, duration);
        resize();
    }

    public void write(String text, float duration, Position position)
    {
        this.position = position;
        label.write(text, duration, position);
        resize();
    }

    public void pushWrite(String text, float duration, Position position)
    {
        this.position = position;
        label.pushWrite(text, duration, position);
        resize();
    }

    private void resize()
    {
        setPosition(label.getX(), label.getY(), label.getWidth(), label.getHeight());
    }

    public boolean animate(float delta)
    {
        if (label.animate(delta))
            resize();
        return true;
    }

    @Override
    public void draw(Batch batch)
    {
        if (!label.visible) return;
        super.draw(batch);
        label.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!label.visible) return;
        super.drawDebug(shapes);
        label.drawDebug(shapes);
    }
}
