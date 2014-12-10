package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class OkCancel extends Bg
{
    public boolean ok;
    public float padding;
    protected Label label;
    protected Bg okBtn;
    protected Bg cancelBtn;
    public Action action;

    public enum Action
    {
        EXIT_BOARD,
        ABORT_TURN,
        END_TURN,
        END_DEPLOYMENT,
    }

    public OkCancel(BitmapFont font, TextureAtlas atlas, float padding)
    {
        super(atlas.findRegion("disabled"));
        this.label = new Label(font);
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.cancelBtn = new Bg(atlas.findRegion("cancel"));
        this.visible = false;
        this.padding = padding;
    }

    public void show(String msg, Action action)
    {
        show(msg, action, Position.MIDDLE_CENTER);
    }

    public void show(String msg, Action action, Position position)
    {
        this.action = action;

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

    public void noCancel()
    {
        cancelBtn.visible = false;
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
