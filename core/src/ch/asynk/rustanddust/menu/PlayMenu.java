package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Button;
import ch.asynk.rustanddust.ui.List;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.ui.Scrollable;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.util.GameRecord;

public class PlayMenu extends Patch implements MenuCtrl.Panel
{
    public static int PADDING = 40;
    public static int TITLE_PADDING = 30;

    private final RustAndDust game;

    private Label title;
    private Label gameMode;
    private Label gameModeValue;
    private float gameModeWidth;
    private boolean notImplemented;
    private Scrollable list;
    protected Bg cancelBtn;
    protected Button newBtn;
    protected Button playBtn;
    protected Button deleteBtn;

    public PlayMenu(RustAndDust game)
    {
        super(game.bgPatch);
        this.game = game;
        this.gameMode = new Label(game.font);
        this.gameMode.write("Game mode : ");
        this.gameModeValue = new Label(game.font, 0f, Position.ABSOLUTE);
        this.cancelBtn = new Bg(game.getUiRegion(game.UI_CANCEL));
        this.newBtn = new Button("New", game.font, game.bgPatch, 20f);
        this.playBtn = new Button("Play", game.font, game.bgPatch, 20f);
        this.deleteBtn = new Button("Delete", game.font, game.bgPatch, 20f);
        this.title = new Label(game.font);
        this.title.write("- Play");
        this.list = new Scrollable(new List(game, 10f), game.framePatch);
        this.padding = PADDING;
        this.gameModeValue.write(game.config.gameMode.getLongest().s);
        this.gameModeWidth = gameModeValue.getWidth();
    }

    private List getList()
    {
        return (List) this.list.getChild();
    }

    @Override
    public MenuCtrl.MenuType postAnswer(boolean ok)
    {
        if (!notImplemented && ok) {
            game.db.deleteGame(GameRecord.remove(getList().getIdx()).id);
            getList().unselect();
            showBtns(false);
            if (GameRecord.list.size() <= 0)
                return MenuCtrl.MenuType.NEW_GAME;
        }
        return MenuCtrl.MenuType.NONE;
    }

    @Override
    public String getAsk()
    {
        if (notImplemented)
            return String.format("'%s' Game Mode not implemented yet.", game.config.gameMode.s);
        else
            return "Permanently delete this game ?";
    }

    @Override
    public MenuCtrl.MenuType prepare()
    {
        loadGames();
        computePosition();
        return MenuCtrl.MenuType.PLAY;
    }

    @Override
    public void computePosition()
    {
        float h = (title.getHeight() + TITLE_PADDING);
        h += list.getBestHeight();
        h += (2 * padding);
        h = Math.min(h, Gdx.graphics.getHeight() - 60);

        float w = title.getWidth() + PADDING + gameModeWidth + 5 + gameMode.getWidth();
        if (list.getWidth() > w) w = list.getWidth();
        if (list.getBestWidth() > w) w = list.getBestWidth();
        w += (2 * padding);
        w = Math.min(w, Gdx.graphics.getWidth() - 60);

        float x = position.getX(w);
        float y = position.getY(h);

        setPosition(x, y, w, h);

        setBottomLeft(cancelBtn);
        setBottomRight(newBtn);
        playBtn.setPosition(newBtn.getX() - playBtn.getWidth() - 5, newBtn.getY());
        deleteBtn.setPosition(playBtn.getX() - deleteBtn.getWidth() - 5, newBtn.getY());
        showBtns(false);

        y += padding;
        x += padding;

        list.setPosition(x, y, (getWidth() - 2 * padding), (getHeight() - 2 * padding - TITLE_PADDING - title.getHeight()));

        y += list.getHeight() + TITLE_PADDING;
        title.setPosition(x, y);

        this.gameModeValue.write(game.config.gameMode.s);
        gameModeValue.setPosition(getX() + w - PADDING - gameModeWidth, y);
        gameMode.setPosition(gameModeValue.getX() - 5 - gameMode.getWidth(), y);
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

        if (gameMode.hit(x, y) || gameModeValue.hit(x, y)) {
            game.playType();
            cycleGameMode();
        } else if (newBtn.hit(x, y)) {
            game.playEnter();
            return tryNew();
        } else if (cancelBtn.hit(x, y)) {
            game.playType();
            return MenuCtrl.MenuType.MAIN;
        } else if (newBtn.hit(x, y)) {
            game.playEnter();
            return MenuCtrl.MenuType.NEW_GAME;
        } else if (deleteBtn.hit(x, y)) {
            game.playType();
            return MenuCtrl.MenuType.OKKO;
        } else if (playBtn.hit(x, y)) {
            setConfig();
            return MenuCtrl.MenuType.RESUME;
        } else if (list.hit(x, y)) {
            if (i != getList().getIdx()) {
                game.playType();
                showBtns(getList().getIdx() != null);
            }
            // show buttons only if can play
            // if (i != getList().getIdx()) {
            //     game.playType();
            //     GameRecord r = (GameRecord) getList().getSelected();
            //     showBtns((r != null) && (r.canPlay()));
            // }
            return MenuCtrl.MenuType.NONE;
        }

        return MenuCtrl.MenuType.NONE;
    }

    private void loadGames()
    {
        game.db.loadGames(game.config.gameMode.i);
        game.config.gameId = game.db.NO_RECORD;

        getList().setItems(4, GameRecord.list);
    }

    private void cycleGameMode()
    {
        game.config.gameMode = game.config.gameMode.next();
        float fx = gameModeValue.getX();
        float fy = gameModeValue.getY();
        gameModeValue.write(game.config.gameMode.s);
        gameModeValue.setPosition(fx, fy);
        showBtns(false);
        loadGames();
    }

    private void setConfig()
    {
        GameRecord g = GameRecord.get(getList().getIdx());
        game.config.gameId = g.id;
        game.config.battle = game.factory.getBattle(g.battle);
    }

    private MenuCtrl.MenuType tryNew()
    {
        notImplemented = !game.config.gameModeImplemented();
        if (!notImplemented)
             return MenuCtrl.MenuType.NEW_GAME;
        return MenuCtrl.MenuType.OK;
    }

    private void showBtns(boolean show)
    {
        deleteBtn.visible = show;
        playBtn.visible = show;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        list.dispose();
        title.dispose();
        gameMode.dispose();
        gameModeValue.dispose();
        newBtn.dispose();
        playBtn.dispose();
        deleteBtn.dispose();
        cancelBtn.dispose();
        GameRecord.clearList();
    }

    @Override
    public void draw(Batch batch)
    {
        super.draw(batch);
        list.draw(batch);
        title.draw(batch);
        gameMode.draw(batch);
        gameModeValue.draw(batch);
        newBtn.draw(batch);
        playBtn.draw(batch);
        deleteBtn.draw(batch);
        cancelBtn.draw(batch);
    }
}
