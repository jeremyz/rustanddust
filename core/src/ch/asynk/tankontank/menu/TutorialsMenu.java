package ch.asynk.tankontank.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.ui.Label;
import ch.asynk.tankontank.ui.Bg;
import ch.asynk.tankontank.ui.Patch;
import ch.asynk.tankontank.TankOnTank;

public class TutorialsMenu extends Patch
{
    public static int PADDING = 40;
    public static int OK_PADDING = 10;
    public static int TITLE_PADDING = 30;
    public static int VSPACING = 20;

    private final TankOnTank game;
    private final BitmapFont font;

    private Label title;
    private Label msg;
    protected Bg okBtn;

    public TutorialsMenu(TankOnTank game, BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
        this.game = game;
        this.font = font;
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.title = new Label(font);
        this.title.write("- Tutorials");
        this.msg = new Label(font);
        this.msg.write("Not implemented yet.");

        this.visible = false;
    }

    public void setPosition()
    {
        float h = (title.getHeight() + TITLE_PADDING + (2 * PADDING));
        h += msg.getHeight();

        float w = title.getWidth();
        if (msg.getWidth() > w)
            w = msg.getWidth();
        w += (2 * PADDING);

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        okBtn.setPosition((x + w - okBtn.getWidth() + OK_PADDING), (y - OK_PADDING));

        y += PADDING;
        x += PADDING;

        msg.setPosition(x, y);

        y += (msg.getHeight() + TITLE_PADDING);
        title.setPosition(x, y);
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (!visible) return false;

        if (okBtn.hit(x, y))
            return true;

        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        title.dispose();
        msg.dispose();
        okBtn.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        msg.draw(batch);
        okBtn.draw(batch);
    }
}
