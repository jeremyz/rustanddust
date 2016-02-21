package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.RustAndDust;

public class TutorialsMenu extends Patch implements MenuCtrl.Panel
{
    public static int PADDING = 40;
    public static int TITLE_PADDING = 30;

    private Label title;
    private Label msg;
    protected Bg okBtn;

    public TutorialsMenu(RustAndDust game)
    {
        super(game.bgPatch);
        this.okBtn = new Bg(game.getUiRegion(game.UI_OK));
        this.title = new Label("- Tutorials", game.font);
        this.msg = new Label("Not implemented yet.\nPlease Visit:\nhttp://rustanddust.ch", game.font);
    }

    @Override
    public boolean prepare() { return true; }

    @Override
    public void computePosition()
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

        setBottomRight(okBtn);

        y += PADDING;
        x += PADDING;

        msg.setPosition(x, y);

        y += (msg.getHeight() + TITLE_PADDING);
        title.setPosition(x, y);
    }

    @Override
    public MenuCtrl.MenuType touch(float x, float y)
    {
        if (rect.contains(x, y) || okBtn.hit(x, y))
            return MenuCtrl.MenuType.MAIN;

        return MenuCtrl.MenuType.NONE;
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
        super.draw(batch);
        title.draw(batch);
        msg.draw(batch);
        okBtn.draw(batch);
    }
}
