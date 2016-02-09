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
        super(Items.NONE, game.font, game.bgPatch);
        this.visible = false;
    }

    public Items getMenu()
    {
        return (Items) menuItem;
    }

    @Override
    public boolean hit(float x, float y)
    {
        menuItem = Items.NONE;

        if (!visible) return false;

        if (!super.hit(x, y)) return false;

        if (menuItem == Items.EXIT)
                Gdx.app.exit();
        return true;
    }
}
