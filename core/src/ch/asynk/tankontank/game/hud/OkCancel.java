package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Image;

public class OkCancel extends Bg
{
    public boolean ok;
    public float padding;
    protected Label label;
    protected Image okBtn;
    protected Image cancelBtn;

    public OkCancel(BitmapFont font, TextureRegion region, TextureAtlas atlas, float padding)
    {
        super(region);
        this.label = new Label(font);
        this.okBtn = new Image(atlas.findRegion("ok"));
        this.cancelBtn = new Image(atlas.findRegion("cancel"));
        this.visible = false;
        this.padding = padding;
    }

    public void show(String msg, Position position)
    {
        label.write(msg);

        float height = (label.getHeight() + (4 * padding) + okBtn.getHeight());
        float width = (label.getWidth() + (2 * padding));
        float w2 = ((3 * padding) + okBtn.getWidth() + cancelBtn.getWidth());
        if (w2 > width)
            width = w2;
        float x = position.getX(width);
        float y = position.getY(height);
        set(x, y, width, height);
        okBtn.setPosition((x + width - okBtn.getWidth() - padding), (y + padding));
        cancelBtn.setPosition((okBtn.getX() - cancelBtn.getWidth() - padding), okBtn.getY());
        label.setPosition((x + padding), (y + okBtn.getHeight() + (2 * padding)));
        visible = true;
        ok = false;
    }

    public boolean hit(float x, float y)
    {
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
