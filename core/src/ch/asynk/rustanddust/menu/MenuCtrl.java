package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.gfx.Drawable;

public class MenuCtrl implements Disposable, Drawable
{
    enum MenuType
    {
        MAIN(0),
        OPTIONS(1),
        TUTORIALS(2),
        PLAY(3),
        NONE(4),
        BEGIN(66),
        EXIT(666);
        public int i;
        MenuType(int i) { this.i = i; }
    }

    interface Panel extends Disposable, Drawable
    {
        public boolean prepare();
        public void computePosition();
        public MenuType touch(float x, float y);
    }

    public boolean visible;
    private Panel []panels;
    private MenuType current;

    public MenuCtrl(final RustAndDust game)
    {
        this.panels = new Panel[MenuType.NONE.i];
        this.panels[MenuType.MAIN.i] = new MainMenu(game);
        this.panels[MenuType.OPTIONS.i] = new OptionsMenu(game);
        this.panels[MenuType.TUTORIALS.i] = new TutorialsMenu(game);
        this.panels[MenuType.PLAY.i] = new PlayMenu(game);

        this.current = MenuType.MAIN;

        this.visible = true;
    }

    public boolean touch(float x, float y)
    {
        MenuType next = panels[current.i].touch(x, y);

        if (next == MenuType.BEGIN) return true;

        if (next == MenuType.EXIT) {
            // TODO clean shutdown
            Gdx.app.exit();
            return false;
        }

        if (next != MenuType.NONE) {
            if (panels[next.i].prepare())
                current = next;
        }

        return false;
    }

    public void computePosition()
    {
        for (int i = 0; i < MenuType.NONE.i; i++)
            this.panels[i].computePosition();
    }

    @Override
    public void dispose()
    {
        for (int i = 0; i < MenuType.NONE.i; i++)
            panels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (visible)
            panels[current.i].draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        if (visible)
            panels[current.i].drawDebug(debugShapes);
    }
}
