package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.RustAndDust;

public class MainMenu extends Patch implements MenuCtrl.Panel
{
    private static final int PADDING = 40;
    private static final int VSPACING = 8;

    enum Item
    {
        EXIT(0, "Exit", MenuCtrl.MenuType.EXIT),
        OPTIONS(1, "Options", MenuCtrl.MenuType.OPTIONS),
        TUTORIALS(2, "Tutorials", MenuCtrl.MenuType.TUTORIALS),
        PLAY(3, "Play", MenuCtrl.MenuType.PLAY),
        NONE(4, null, MenuCtrl.MenuType.NONE);

        static public Item get(int i)
        {
            switch(i) {
                case 0: return EXIT;
                case 1: return OPTIONS;
                case 2: return TUTORIALS;
                case 3: return PLAY;
            }
            return NONE;
        }

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

    protected Label []labels;
    private final RustAndDust game;

    public MainMenu(RustAndDust game)
    {
        super(game.bgPatch);
        this.game = game;
        this.labels = new Label[Item.NONE.i];
        for (int i = 0; i < Item.NONE.i; i++)
            labels[i] = new Label(game.font, 10);
        labels[Item.EXIT.i].write(Item.EXIT.s);
        labels[Item.OPTIONS.i].write(Item.OPTIONS.s);
        labels[Item.TUTORIALS.i].write(Item.TUTORIALS.s);
        labels[Item.PLAY.i].write(Item.PLAY.s);
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

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        y += PADDING;
        x += PADDING;

        for (int i = 0; i< Item.NONE.i; i ++) {
            labels[i].setPosition(x, y);
            y += (VSPACING + labels[i].getHeight());
        }
    }

    @Override
    public MenuCtrl.MenuType postAnswer(boolean ok) { return MenuCtrl.MenuType.NONE; }

    @Override
    public String getAsk() { return null; }

    @Override
    public MenuCtrl.MenuType prepare() { return MenuCtrl.MenuType.MAIN; }

    @Override
    public boolean drag(float x, float y, int dx, int dy) { return true; }

    @Override
    public MenuCtrl.MenuType touch(float x, float y)
    {
        int idx = -1;
        for (int i = 0; i< Item.NONE.i; i ++) {
            if (labels[i].hit(x, y)) {
                game.playType();
                return Item.get(i).t;
            }
        }

        return MenuCtrl.MenuType.NONE;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        for (int i = 0; i < Item.NONE.i; i ++)
            labels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        super.draw(batch);
        for (int i = 0; i < Item.NONE.i; i ++)
            labels[i].draw(batch);
    }
}
