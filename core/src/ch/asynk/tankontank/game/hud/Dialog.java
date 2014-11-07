package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

import ch.asynk.tankontank.engine.gfx.Image;

public class Dialog extends Bg
{
    public boolean visible;
    public boolean ok;
    public int padding;
    private Text text;
    private Image okBtn;
    private Image cancelBtn;

    public Dialog(BitmapFont font, TextureRegion region, TextureAtlas atlas)
    {
        super(region);
        this.text = new Text(font, "hello");
        this.okBtn = new Image(atlas.findRegion("ok"));
        this.cancelBtn = new Image(atlas.findRegion("cancel"));
        this.visible = false;
        this.padding = 10;
    }

    public void show(String msg, Position position)
    {
        text.write(msg);
        TextBounds b = text.getBounds();

        float height = (b.height + (3 * padding) + okBtn.getHeight());
        float width = (b.width + (2 * padding));
        float w2 = ((3 * padding) + okBtn.getWidth() + cancelBtn.getWidth());
        if (w2 > width)
            width = w2;
        float x = position.getX(width);
        float y = position.getY(height);
        set(x, y, width, height);
        okBtn.setPosition((x + width - okBtn.getWidth() - padding), (y + padding));
        cancelBtn.setPosition((okBtn.getX() - cancelBtn.getWidth() - padding), okBtn.getY());
        text.setPosition((x + padding), (y + okBtn.getHeight() + padding));
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
        // font.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        text.draw(batch);
        okBtn.draw(batch);
        cancelBtn.draw(batch);
    }
}
