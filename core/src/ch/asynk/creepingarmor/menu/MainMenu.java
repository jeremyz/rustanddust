package ch.asynk.creepingarmor.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.creepingarmor.ui.Menu;

public class MainMenu extends Menu
{
    public enum Items implements Menu.MenuItem
    {
        EXIT(0),
        OPTIONS(1),
        TUTORIALS(2),
        SCENARIOS(3),
        NONE(4);
        public int i;
        Items(int i)
        {
            this.i = i;
        }
        public int i() { return i; }
        public int last() { return NONE.i; }
    };

    public MainMenu(BitmapFont font, TextureAtlas atlas)
    {
        super(Items.NONE, font, atlas.createPatch("typewriter"));

        label(Items.OPTIONS).write("Options");
        label(Items.TUTORIALS).write("Tutorials");
        label(Items.SCENARIOS).write("Scenarios");
        label(Items.EXIT).write("Exit");

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

        if (label(Items.SCENARIOS).hit(x, y)) {
            menuItem = Items.SCENARIOS;
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
