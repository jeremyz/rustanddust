package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.Gdx;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Menu;

public class MainMenu extends Menu
{
    public enum Items implements Menu.MenuItem
    {
        EXIT(0, "Exit"),
        OPTIONS(1, "Options"),
        TUTORIALS(2, "Tutorials"),
        PLAY(3, "Play"),
        NONE(5, null);
        public int i;
        public String s;
        Items(int i, String s)
        {
            this.i = i;
            this.s = s;
        }
        public String s() { return s; }
        public int i() { return i; }
        public int last() { return NONE.i; }
        public Menu.MenuItem get(int i)
        {
            if (i == EXIT.i) return EXIT;
            else if (i == OPTIONS.i) return OPTIONS;
            else if (i == TUTORIALS.i) return TUTORIALS;
            else if (i == PLAY.i) return PLAY;
            else return NONE;
        }
    };

    public MainMenu(RustAndDust game)
    {
        super(Items.NONE, game.font, game.ninePatch);
        this.visible = true;
    }

    public Items getMenu()
    {
        return (Items) menuItem;
    }

    @Override
    public boolean hit(float x, float y)
    {
        boolean ret = false;
        menuItem = Items.NONE;

        if (!visible) return ret;

        if (label(Items.PLAY).hit(x, y)) {
            menuItem = Items.PLAY;
            ret = true;
        } else if (label(Items.TUTORIALS).hit(x, y)) {
            menuItem = Items.TUTORIALS;
            ret = true;
        } else if (label(Items.OPTIONS).hit(x, y)) {
            menuItem = Items.OPTIONS;
            ret = true;
        } else if (label(Items.EXIT).hit(x, y)) {
            Gdx.app.exit();
        }

        return ret;
    }
}
