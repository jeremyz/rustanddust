package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.hud.ObjectivesPanel;

public class NewGameMenu extends Patch implements MenuCtrl.Panel
{
    public static int PADDING = 50;
    public static int TITLE_PADDING = 35;
    public static int VSPACING = 30;

    private final RustAndDust game;

    private Label title;
    private Label gameMode;
    private Label gameModeValue;
    private float gameModeWidth;
    private int battleIdx;
    private Label battle;
    private Label battleValue;
    private Label objectives;
    private ObjectivesPanel objectivesPanel;
    protected Bg okBtn;
    protected Bg cancelBtn;
    private boolean notImplemented;

    public NewGameMenu(RustAndDust game)
    {
        super(game.bgPatch);
        this.game = game;
        this.title = new Label(game.font);
        this.title.write("- New Game");
        this.gameMode = new Label(game.font);
        this.gameMode.write("Game mode : ");
        this.gameModeValue = new Label(game.font);
        this.okBtn = new Bg(game.getUiRegion(game.UI_OK));
        this.cancelBtn = new Bg(game.getUiRegion(game.UI_CANCEL));
        this.battle = new Label(game.font);
        this.battle.write("Scenario : ");
        this.battleValue = new Label(game.font);
        this.objectives = new Label(game.font);
        this.objectives.write("Battle Objectives");
        this.objectivesPanel = new ObjectivesPanel(game);

        if (game.config.battle == null) {
            battleIdx = 0;
            game.config.battle = game.factory.battles[0];
        } else {
            for (int i = 0; i < game.factory.battles.length; i++) {
                if (game.config.battle == game.factory.battles[i]) {
                    battleIdx = i;
                    break;
                }
            }
        }
        battleValue.write(game.config.battle.getName());

        float w = 0;
        for (int i = game.config.gameMode.i; ;) {
            gameModeValue.write(game.config.gameMode.s);
            if (w < gameModeValue.getWidth())
                w = gameModeValue.getWidth();
            game.config.gameMode = game.config.gameMode.next();
            if (i == game.config.gameMode.i) break;
        }
        this.gameModeValue.write(game.config.gameMode.s);
        this.gameModeWidth = w + 10 + gameMode.getWidth();
    }

    @Override
    public MenuCtrl.MenuType postAnswer(boolean ok)
    {
        if (ok && !notImplemented) return MenuCtrl.MenuType.BEGIN;
        return MenuCtrl.MenuType.NONE;
    }

    @Override
    public String getAsk()
    {
        if (notImplemented)
            return String.format("'%s' Game Mode not implemented yet.", game.config.gameMode.s);
        else
            return String.format("Resume '%s' ?", game.config.battle.toString());
    }

    @Override
    public void computePosition()
    {
        float h = (title.getHeight() + TITLE_PADDING + (2 * PADDING));
        h += (gameMode.getHeight() + VSPACING);
        h += (battle.getHeight());
        h += (objectives.getHeight());

        float w = gameModeWidth + (2 * PADDING);

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        setBottomRight(okBtn);
        setBottomLeft(cancelBtn);

        y += PADDING;
        x += PADDING;
        float dy = (VSPACING + battle.getHeight());

        objectives.setPosition(x, y);
        y += dy;
        battle.setPosition(x, y);
        battleValue.setPosition((x + battle.getWidth() + 10), y);
        y += dy;
        gameMode.setPosition(x, y);
        gameModeValue.setPosition((x + gameMode.getWidth() + 10), y);
        y += dy;

        y += (TITLE_PADDING - VSPACING);
        title.setPosition(x, y);
    }

    @Override
    public boolean drag(float x, float y, int dx, int dy) { return true; }

    @Override
    public MenuCtrl.MenuType touch(float x, float y)
    {
        if (objectivesPanel.hit(x, y)) {
            game.playType();
            this.visible = true;
            objectivesPanel.visible = false;
            return MenuCtrl.MenuType.NONE;
        }

        if (!visible) return MenuCtrl.MenuType.NONE;

        if (okBtn.hit(x, y)) {
            game.playEnter();
            return tryLaunch();
        } else if (cancelBtn.hit(x, y)) {
            game.playType();
            return MenuCtrl.MenuType.MAIN;
        } else if (gameMode.hit(x, y) || gameModeValue.hit(x, y)) {
            game.playType();
            cycleGameMode();
        } else if (battle.hit(x, y) || battleValue.hit(x, y)) {
            game.playType();
            cycleBattle();
        } else if (objectives.hit(x, y)) {
            game.playType();
            this.visible = false;
            objectivesPanel.show(game.config.battle);
        }

        return MenuCtrl.MenuType.NONE;
    }

    private MenuCtrl.MenuType tryLaunch()
    {
        if (!game.config.gameModeImplemented()) {
            notImplemented = true;
            return MenuCtrl.MenuType.OK;
        }

        game.config.gameId = game.db.getGameId(game.backend.getOpponentId(), game.config.battle.getId(), game.config.gameMode.i);
        if (game.config.gameId != game.db.NO_RECORD) {
            notImplemented = false;
            return MenuCtrl.MenuType.OKKO;
        }

        return MenuCtrl.MenuType.BEGIN;
    }

    private void cycleGameMode()
    {
        game.config.gameMode = game.config.gameMode.next();
        float fx = gameModeValue.getX();
        float fy = gameModeValue.getY();
        gameModeValue.write(game.config.gameMode.s);
        gameModeValue.setPosition(fx, fy);
    }

    private void cycleBattle()
    {
        battleIdx += 1;
        if (battleIdx >= game.factory.battles.length)
            battleIdx = 0;
        game.config.battle = game.factory.battles[battleIdx];
        float fx = battleValue.getX();
        float fy = battleValue.getY();
        battleValue.write(game.config.battle.getName());
        battleValue.setPosition(fx, fy);
    }

    @Override
    public MenuCtrl.MenuType prepare() { return MenuCtrl.MenuType.NEW_GAME; }

    @Override
    public void dispose()
    {
        super.dispose();
        title.dispose();
        gameMode.dispose();
        gameModeValue.dispose();
        objectives.dispose();
        objectivesPanel.dispose();
        battle.dispose();
        battleValue.dispose();
        okBtn.dispose();
        cancelBtn.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        objectivesPanel.draw(batch);

        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        gameMode.draw(batch);
        gameModeValue.draw(batch);
        objectives.draw(batch);
        battle.draw(batch);
        battleValue.draw(batch);
        okBtn.draw(batch);
        cancelBtn.draw(batch);
    }
}
