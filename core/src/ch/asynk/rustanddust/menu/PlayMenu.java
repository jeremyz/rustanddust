package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Button;
import ch.asynk.rustanddust.ui.List;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.ui.Scrollable;
import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.util.GameRecord;

public class PlayMenu extends Patch implements MenuCtrl.Panel
{
    public static int PADDING = 40;
    public static int TITLE_PADDING = 30;

    private final RustAndDust game;

    private Label title;
    private Scrollable list;
    protected Bg cancelBtn;
    protected Button newBtn;
    protected Button joinBtn;
    protected Button deleteBtn;

    public PlayMenu(RustAndDust game)
    {
        super(game.bgPatch);
        this.game = game;
        this.cancelBtn = new Bg(game.getUiRegion(game.UI_CANCEL));
        this.newBtn = new Button("New", game.font, game.bgPatch, 20f);
        this.joinBtn = new Button("Join", game.font, game.bgPatch, 20f);
        this.deleteBtn = new Button("Delete", game.font, game.bgPatch, 20f);
        this.title = new Label(game.font);
        this.title.write("- Play");
        this.list = new Scrollable(new List(game, 10f), game.framePatch);
    }

    private List getList()
    {
        return (List) this.list.getChild();
    }

    @Override
    public void postAnswer(boolean ok)
    {
        if (ok) {
            game.db.deleteGame(GameRecord.remove(getList().getIdx()));
            getList().unselect();
        }
    }

    @Override
    public String getAsk()
    {
        return "Permanently delete this game ?";
    }

    @Override
    public MenuCtrl.MenuType prepare()
    {
        game.db.loadGames();
        game.config.gameId = game.db.NO_RECORD;

        if (GameRecord.list.size() <= 0)
            return MenuCtrl.MenuType.NEW_GAME;

        getList().setItems(4, GameRecord.list);
        computePosition();
        return MenuCtrl.MenuType.PLAY;
    }

    @Override
    public void computePosition()
    {
        float h = (title.getHeight() + TITLE_PADDING);
        h += list.getHeight();
        h += (2 * PADDING);

        float w = title.getWidth();
        if (list.getWidth() > w) w = list.getWidth();
        if (list.getBestWidth() > w) w = list.getBestWidth();
        w += (2 * PADDING);

        float x = position.getX(w);
        float y = position.getY(h);

        setPosition(x, y, w, h);

        setBottomLeft(cancelBtn);
        setBottomRight(newBtn);
        joinBtn.setPosition(newBtn.getX() - joinBtn.getWidth() - 5, newBtn.getY());
        deleteBtn.setPosition(joinBtn.getX() - deleteBtn.getWidth() - 5, newBtn.getY());
        deleteBtn.visible = false;
        joinBtn.visible = false;

        y += PADDING;
        x += PADDING;

        // FIXME 200 should be relative to screen dimensions
        list.setPosition(x, y, Math.max(200, list.getBestWidth()), 200);

        y += list.getHeight() + TITLE_PADDING;
        title.setPosition(x, y);
    }

    @Override
    public boolean drag(float x, float y, int dx, int dy)
    {
        if (!list.hit(x, y)) return false;
        return list.drag(x, y, dx, dy);
    }

    @Override
    public MenuCtrl.MenuType touch(float x, float y)
    {
        Integer i = getList().getIdx();

        if (newBtn.hit(x, y)) {
            game.playEnter();
            return MenuCtrl.MenuType.NEW_GAME;
        } else if (cancelBtn.hit(x, y)) {
            game.playType();
            return MenuCtrl.MenuType.MAIN;
        } else if (newBtn.hit(x, y)) {
            game.playEnter();
            return MenuCtrl.MenuType.NEW_GAME;
        } else if (deleteBtn.hit(x, y)) {
            game.playType();
            return MenuCtrl.MenuType.OKKO;
        } else if (joinBtn.hit(x, y)) {
            game.playType();
            game.config.gameId = GameRecord.get(getList().getIdx()).g;
            return MenuCtrl.MenuType.BEGIN;
        } else if (list.hit(x, y)) {
            if (i != getList().getIdx())
                game.playType();
            if(getList().getIdx() == null) {
                deleteBtn.visible = false;
                joinBtn.visible = false;
            } else {
                deleteBtn.visible = true;
                joinBtn.visible = true;
            }
            return MenuCtrl.MenuType.NONE;
        }

        return MenuCtrl.MenuType.NONE;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        list.dispose();
        title.dispose();
        newBtn.dispose();
        joinBtn.dispose();
        deleteBtn.dispose();
        cancelBtn.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        super.draw(batch);
        list.draw(batch);
        title.draw(batch);
        newBtn.draw(batch);
        joinBtn.draw(batch);
        deleteBtn.draw(batch);
        cancelBtn.draw(batch);
    }
}
