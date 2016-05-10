package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Config;

public class ResumeMenu extends Patch implements MenuCtrl.Panel
{
    private static final int PADDING = 40;
    private static final int VSPACING = 8;
    private static final int TITLE_SPACING = 12;

    enum Item
    {
        RESUME(3, " * Resume", MenuCtrl.MenuType.NONE),
        REPLAY_LAST(2, " * Replay Last Actions", MenuCtrl.MenuType.NONE),
        REPLAY_BATTLE(1, " * Replay Battle", MenuCtrl.MenuType.NONE),
        CANCEL(0, " * Cancel", MenuCtrl.MenuType.PLAY),
        NONE(4, null, MenuCtrl.MenuType.NONE);

        public int i;
        public String s;
        public MenuCtrl.MenuType t;
        Item(int i, String s, MenuCtrl.MenuType t)
        {
            this.i = i;
            this.s = s;
            this.t = t;
        }
    };

    protected Label title;
    protected Label []labels;
    private final RustAndDust game;

    public ResumeMenu(RustAndDust game)
    {
        super(game.bgPatch);
        this.game = game;
        this.title = new Label(game.font, 10);
        this.title.write("- Play");
        this.labels = new Label[Item.NONE.i];
        for (int i = 0; i < Item.NONE.i; i++)
            labels[i] = new Label(game.font, 10);
        labels[Item.RESUME.i].write(Item.RESUME.s);
        labels[Item.REPLAY_LAST.i].write(Item.REPLAY_LAST.s);
        labels[Item.REPLAY_BATTLE.i].write(Item.REPLAY_BATTLE.s);
        labels[Item.CANCEL.i].write(Item.CANCEL.s);
    }

    @Override
    public void computePosition()
    {
        float h = 0f;
        float w = 0f;
        for (int i = 0; i< Item.NONE.i; i ++) {
            h += labels[i].getHeight();
            float t = labels[i].getWidth();
            if (t > w)
                w = t;
        }
        h += (2 * PADDING) + ((Item.NONE.i - 1) * VSPACING);
        w += (2 * PADDING);
        h += title.getHeight() + TITLE_SPACING;

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        y += PADDING;
        x += PADDING;

        for (int i = 0; i< Item.NONE.i; i ++) {
            labels[i].setPosition(x, y);
            y += (VSPACING + labels[i].getHeight());
        }
        y+= TITLE_SPACING;
        title.setPosition(x, y);
    }

    @Override
    public MenuCtrl.MenuType postAnswer(boolean ok) { return MenuCtrl.MenuType.NONE; }

    @Override
    public String getAsk() { return null; }

    @Override
    public MenuCtrl.MenuType prepare() { return MenuCtrl.MenuType.RESUME; }

    @Override
    public boolean drag(float x, float y, int dx, int dy) { return true; }

    @Override
    public MenuCtrl.MenuType touch(float x, float y)
    {
        int idx = -1;

        if (labels[Item.CANCEL.i].hit(x, y)) {
            game.playType();
            return Item.CANCEL.t;
        }
        else if (labels[Item.RESUME.i].hit(x, y))
            return setConfig(Config.LoadMode.RESUME);
        else if (labels[Item.REPLAY_LAST.i].hit(x, y))
            return setConfig(Config.LoadMode.REPLAY_LAST);
        else if (labels[Item.REPLAY_BATTLE.i].hit(x, y))
            return setConfig(Config.LoadMode.REPLAY_BATTLE);

            return MenuCtrl.MenuType.NONE;
        }

    private MenuCtrl.MenuType setConfig(Config.LoadMode loadMode)
    {
        game.playType();
        game.config.loadMode = loadMode;
        return MenuCtrl.MenuType.BEGIN;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        title.dispose();
        for (int i = 0; i < Item.NONE.i; i ++)
            labels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        for (int i = 0; i < Item.NONE.i; i ++)
            labels[i].draw(batch);
    }
}
