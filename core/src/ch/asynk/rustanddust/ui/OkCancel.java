package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class OkCancel extends Patch
{
    public static int PADDING = 20;
    public static int VSPACING = 10;
    public static int HSPACING = 10;

    public boolean ok;
    protected Label label;
    protected Bg okBtn;
    protected Bg cancelBtn;

    public OkCancel(BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
        this.label = new Label(font);
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.cancelBtn = new Bg(atlas.findRegion("cancel"));
        this.visible = false;
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
        label.translate(dx, dy);
        okBtn.translate(dx, dy);
        cancelBtn.translate(dx, dy);
    }

    public void show(String msg)
    {
        show(msg, Position.MIDDLE_CENTER);
    }

    public void show(String msg, Position position)
    {
        label.write(msg);

        float height = (label.getHeight() + okBtn.getHeight() + (2 * PADDING) + (2 * VSPACING));
        float width = (label.getWidth() + (2 * PADDING));
        float w2 = (okBtn.getWidth() + cancelBtn.getWidth() + (2 * PADDING) + (1 * HSPACING));
        if (w2 > width)
            width = w2;
        float x = position.getX(width);
        float y = position.getY(height);
        setPosition(x, y, width, height);

        okBtn.setPosition((x + width - (okBtn.getWidth() / 2.0f)), (y - (okBtn.getHeight() / 2.0f)));
        cancelBtn.setPosition((x - (cancelBtn.getWidth() / 2.0f)), (y - (cancelBtn.getHeight() / 2.0f)));
        label.setPosition((x + PADDING), (y + PADDING + okBtn.getHeight() + VSPACING));
        cancelBtn.visible = true;
        visible = true;
        ok = false;
    }

    public void noCancel()
    {
        cancelBtn.visible = false;
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (!cancelBtn.visible && super.hit(x, y)) {
            ok = true;
            return true;
        }
        if (okBtn.hit(x, y)) {
            ok = true;
            return true;
        } else if (cancelBtn.hit(x, y)) {
            ok = false;
            return true;
        }
        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        label.dispose();
        okBtn.dispose();
        cancelBtn.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        label.draw(batch);
        okBtn.draw(batch);
        cancelBtn.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        label.drawDebug(shapes);
        okBtn.drawDebug(shapes);
        cancelBtn.drawDebug(shapes);
    }
}
