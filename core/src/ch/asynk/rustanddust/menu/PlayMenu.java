package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.ui.OkCancel;
import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.hud.ObjectivesPanel;

public class PlayMenu extends Patch
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
    private OkCancel okCancel;

    public boolean launch;

    public PlayMenu(RustAndDust game)
    {
        super(game.bgPatch);
        this.game = game;
        this.title = new Label(game.font);
        this.title.write("- Play");
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
        this.okCancel = new OkCancel(game.font, game.bgPatch, game.getUiRegion(game.UI_OK), game.getUiRegion(game.UI_CANCEL));

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

        this.visible = false;
        this.launch = false;
    }

    public void setPosition()
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
    public boolean hit(float x, float y)
    {
        if (okCancel.hit(x, y)) {
            this.visible = true;
            okCancel.visible = false;
            return false;
        } else if (objectivesPanel.hit(x, y)) {
            this.visible = true;
            objectivesPanel.visible = false;
            return false;
        }

        if (!visible) return false;

        if (okBtn.hit(x, y)) {
            return apply();
        } else if (cancelBtn.hit(x, y)) {
            return true;
        } else if (gameMode.hit(x, y) || gameModeValue.hit(x, y)) {
            cycleGameMode();
        } else if (battle.hit(x, y) || battleValue.hit(x, y)) {
            cycleBattle();
        } else if (objectives.hit(x, y)) {
            this.visible = false;
            objectivesPanel.show(game.config.battle);
        }

        return false;
    }

    private boolean apply() {
        if (!game.config.gameModeImplemented()) {
            this.visible = false;
            okCancel.show(String.format("'%s' Game Mode not implemented yet.", game.config.gameMode.s));
            okCancel.noCancel();
            return false;
        } else
            this.launch = true;
        return true;
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
        okCancel.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        okCancel.draw(batch);
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
