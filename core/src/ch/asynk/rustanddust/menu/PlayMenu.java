package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.ui.OkCancel;
import ch.asynk.rustanddust.RustAndDust;

public class PlayMenu extends Patch
{
    public static int PADDING = 40;
    public static int BTN_PADDING = 10;
    public static int TITLE_PADDING = 30;
    public static int VSPACING = 20;

    private final RustAndDust game;
    private final BitmapFont font;

    private Label title;
    private Label gameMode;
    private Label gameModeValue;
    private float gameModeWidth;
    private int battleIdx;
    private Label battle;
    private Label battleValue;
    protected Bg okBtn;
    protected Bg cancelBtn;
    private OkCancel okCancel;

    public boolean launch;

    public PlayMenu(RustAndDust game, BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
        this.game = game;
        this.font = font;
        this.title = new Label(font);
        this.title.write("- Play");
        this.gameMode = new Label(font);
        this.gameMode.write("Game mode");
        this.gameModeValue = new Label(font);
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.cancelBtn = new Bg(atlas.findRegion("cancel"));
        this.battle = new Label(font);
        this.battle.write("Scenario");
        this.battleValue = new Label(font);
        this.okCancel = new OkCancel(font, atlas);

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
        for(int i = game.config.gameMode.i; ;) {
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

        float w = gameModeWidth + (2 * PADDING);

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        okBtn.setPosition((x + w - okBtn.getWidth() + BTN_PADDING), (y - BTN_PADDING));
        cancelBtn.setPosition((x - BTN_PADDING), okBtn.getY());

        y += PADDING;
        x += PADDING;
        float dy = (VSPACING + battle.getHeight());

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
        }

        return false;
    }

    private boolean apply() {
        if (!game.config.gameModeImplemented()) {
            this.visible = false;
            okCancel.show(String.format("'%s' Game Mode not implemented yet.", game.config.gameMode.s));
            okCancel.noCancel();
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

        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        gameMode.draw(batch);
        gameModeValue.draw(batch);
        battle.draw(batch);
        battleValue.draw(batch);
        okBtn.draw(batch);
        cancelBtn.draw(batch);
    }
}
