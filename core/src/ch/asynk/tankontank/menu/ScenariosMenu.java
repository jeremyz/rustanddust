package ch.asynk.tankontank.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.ui.Label;
import ch.asynk.tankontank.ui.Bg;
import ch.asynk.tankontank.ui.Patch;
import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.Battle;

public class ScenariosMenu extends Patch
{
    public static int PADDING = 40;
    public static int OK_PADDING = 10;
    public static int TITLE_PADDING = 30;
    public static int VSPACING = 5;
    public static int HSPACING = 30;
    public static String CHECK = "#";

    private final TankOnTank game;
    private final BitmapFont font;

    private float checkDy;
    private Label title;
    protected Bg okBtn;
    private Label [] battleLabels;

    public ScenariosMenu(TankOnTank game, BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
        this.game = game;
        this.font = font;
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.title = new Label(font);
        this.title.write("- Scenarios");
        this.battleLabels = new Label[game.factory.battles.length];
        for (int i = 0; i < battleLabels.length; i++) {
            Label l = new Label(font, 8f);
            l.write(game.factory.battles[i].getName());
            battleLabels[i] = l;
        }
        checkDy = font.getMultiLineBounds(CHECK).height + 9;

        this.visible = false;
    }

    public void setPosition()
    {
        float h = (title.getHeight() + TITLE_PADDING + ((battleLabels.length - 1) * VSPACING) + (2 * PADDING));
        for (int i = 0; i < battleLabels.length; i++)
            h += battleLabels[i].getHeight();

        float w = title.getWidth();
        for (int i = 0; i < battleLabels.length; i++) {
            float t = battleLabels[i].getWidth();
            if (t > w)
                w = t;
        }
        w += (2 * PADDING) + HSPACING;

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        okBtn.setPosition((x + w - okBtn.getWidth() + OK_PADDING), (y - OK_PADDING));

        y += PADDING;
        x += PADDING + HSPACING;
        float dy = (VSPACING + battleLabels[0].getHeight());

        for (int i = (battleLabels.length - 1); i > -1; i--) {
            battleLabels[i].setPosition(x, y);
            y += dy;
        }
        y += (TITLE_PADDING - VSPACING);
        title.setPosition(x, y);
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (!visible) return false;

        if (okBtn.hit(x, y)) {
            return true;
        } else {
            for (int i = 0; i <battleLabels.length; i++) {
                if (battleLabels[i].hit(x, y)) {
                    if (game.config.battle == game.factory.battles[i])
                        game.config.battle = null;
                    else
                        game.config.battle = game.factory.battles[i];
                }
            }
        }

        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        title.dispose();
        okBtn.dispose();
        for (int i = 0; i < battleLabels.length; i++)
            battleLabels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        okBtn.draw(batch);
        for (int i = 0; i < battleLabels.length; i++) {
            Label l = battleLabels[i];
            l.draw(batch);
            if (game.config.battle == game.factory.battles[i])
                font.draw(batch, CHECK, (l.getX() - HSPACING) , l.getY() + checkDy);
        }
    }
}
