package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.gfx.Drawable;
import ch.asynk.rustanddust.ui.OkCancel;

public class MenuCtrl implements Disposable, Drawable
{
    enum MenuType
    {
        MAIN(0),
        OPTIONS(1),
        TUTORIALS(2),
        PLAY(3),
        NEW_GAME(4),
        RESUME(5),
        NONE(6),
        OK(64),
        OKKO(65),
        BEGIN(66),
        EXIT(666);
        public int i;
        MenuType(int i) { this.i = i; }
    }

    interface Panel extends Disposable, Drawable
    {
        public MenuType prepare();
        public void computePosition();
        public MenuType touch(float x, float y);
        public String getAsk();
        public MenuType postAnswer(boolean ok);
        public boolean drag(float x, float y, int dx, int dy);
    }

    private final RustAndDust game;

    public boolean visible;
    private Panel []panels;
    private MenuType current;
    private OkCancel okCancel;

    public MenuCtrl(final RustAndDust game)
    {
        this.game = game;
        this.panels = new Panel[MenuType.NONE.i];
        this.panels[MenuType.MAIN.i] = new MainMenu(game);
        this.panels[MenuType.OPTIONS.i] = new OptionsMenu(game);
        this.panels[MenuType.TUTORIALS.i] = new TutorialsMenu(game);
        this.panels[MenuType.PLAY.i] = new PlayMenu(game);
        this.panels[MenuType.NEW_GAME.i] = new NewGameMenu(game);
        this.panels[MenuType.RESUME.i] = new ResumeMenu(game);
        this.okCancel = new OkCancel(game.font, game.bgPatch, game.getUiRegion(game.UI_OK), game.getUiRegion(game.UI_CANCEL));

        this.current = MenuType.MAIN;

        this.okCancel.visible = false;
        this.visible = true;
    }

    public boolean drag(float x, float y, int dx, int dy)
    {
        if (current == MenuType.PLAY)
            panels[current.i].drag(x, y, dx, dy);
        return true;
    }

    public boolean touch(float x, float y)
    {
        MenuType next = MenuType.NONE;

        if (okCancel.hit(x, y)) {
            if (okCancel.ok)
                game.playEnter();
            else
                game.playType();
            visible = true;
            okCancel.visible = false;
            return changePanel(panels[current.i].postAnswer(okCancel.ok));
        }

        next = panels[current.i].touch(x, y);

        if (next == MenuType.BEGIN) return true;

        if (next == MenuType.EXIT) {
            // TODO clean shutdown
            Gdx.app.exit();
            return false;
        }

        if ((next == MenuType.OK) || (next == MenuType.OKKO)) {
            okCancel.visible = true;
            okCancel.show(panels[current.i].getAsk());
            if (next == MenuType.OK)
                okCancel.noCancel();
            visible = false;
            return false;
        }

        return changePanel(next);
    }

    private boolean changePanel(MenuType next)
    {
        if (next == MenuType.BEGIN) return true;

        if (next != MenuType.NONE) {
            while(current != next) {
                current = next;
                next = panels[next.i].prepare();
                if (next == MenuType.BEGIN) return true;
            }
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
        okCancel.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        okCancel.draw(batch);
        if (visible)
            panels[current.i].draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        okCancel.drawDebug(debugShapes);
        if (visible)
            panels[current.i].drawDebug(debugShapes);
    }
}
