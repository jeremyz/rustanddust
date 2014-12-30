package ch.asynk.tankontank.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.ui.Label;
import ch.asynk.tankontank.ui.Patch;

public class MainMenu extends Patch
{
    public static int PADDING = 40;
    public static int VSPACING = 20;

    private Label options;
    private Label scenarios;
    private Label tutorial;
    private Label exit;

    public enum Menu {
        OPTIONS,
        TUTORIALS,
        SCENARIOS,
        NONE
    };
    private Menu menu;

    public MainMenu(BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
        this.options = new Label(font);
        this.scenarios = new Label(font);
        this.tutorial = new Label(font);
        this.exit = new Label(font);
        this.menu = Menu.NONE;

        options.write("Options");
        scenarios.write("Scenarios");
        tutorial.write("Tutorial");
        exit.write("Quit");
    }

    public Menu getMenu()
    {
        return menu;
    }

    public void setPosition()
    {
        float h = ((4 * tutorial.getHeight()) + (2 * PADDING) + (3 * VSPACING));
        float w = (scenarios.getWidth() + (2 * PADDING));
        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        y += PADDING;
        x += PADDING;
        float dy = (VSPACING + tutorial.getHeight());

        exit.setPosition(x, y);
        y += dy;
        options.setPosition(x, y);
        y += dy;
        tutorial.setPosition(x, y);
        y += dy;
        scenarios.setPosition(x, y);
    }

    @Override
    public boolean hit(float x, float y)
    {
        boolean ret = false;
        menu = Menu.NONE;

        if (!visible) return ret;

        if (scenarios.hit(x, y)) {
            menu = Menu.SCENARIOS;
            ret = true;
        } else if (tutorial.hit(x, y)) {
            menu = Menu.TUTORIALS;
            ret = true;
        } else if (options.hit(x, y)) {
            menu = Menu.OPTIONS;
            ret = true;
        } else if (exit.hit(x, y)) {
            Gdx.app.exit();
        }

        return ret;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        scenarios.dispose();
        tutorial.dispose();
        options.dispose();
        exit.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        scenarios.draw(batch);
        tutorial.draw(batch);
        options.draw(batch);
        exit.draw(batch);
    }
}
