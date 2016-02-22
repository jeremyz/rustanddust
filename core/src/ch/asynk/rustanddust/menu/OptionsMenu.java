package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Patch;

import ch.asynk.rustanddust.RustAndDust;

public class OptionsMenu extends Patch implements MenuCtrl.Panel
{
    public static int PADDING = 30;
    public static int OPT_PADDING = 10;
    public static int TITLE_PADDING = 10;
    public static int LABEL_PADDING = 10;
    public static int HSPACING = 30;
    public static String CHECK = "#";

    private final RustAndDust game;
    private final BitmapFont font;

    private String [] checkStrings = {
        "Debug",
        "Show Enemy Possibilities",
        "Show Move Assists",
        "Show Targets",
        "Show Moves",
    };

    private Label title;
    private Bg okBtn;
    private Bg cancelBtn;
    private Label fxVolume;
    private Label fxVolumeValue;
    private Label graphics;
    private Label graphicsValue;
    private Label [] checkLabels;

    private float checkDy;
    private int fxVolumeIdx;
    private int graphicsIdx;
    private boolean [] checkValues;

    public OptionsMenu(RustAndDust game)
    {
        super(game.bgPatch);
        this.game = game;
        this.font = game.font;
        this.title = new Label("- Options", font, LABEL_PADDING);
        this.okBtn = new Bg(game.getUiRegion(game.UI_OK));
        this.cancelBtn = new Bg(game.getUiRegion(game.UI_CANCEL));
        this.fxVolume = new Label("Fx Volume", font, LABEL_PADDING);
        this.fxVolumeValue = new Label(font, LABEL_PADDING);
        this.graphics = new Label("Graphics", font, LABEL_PADDING);
        this.graphicsValue = new Label(font, LABEL_PADDING);
        this.checkValues = new boolean[checkStrings.length];
        this.checkLabels = new Label[checkStrings.length];
        for (int i = 0; i < checkLabels.length; i++) {
            Label l = new Label(checkStrings[i], font, LABEL_PADDING);
            this.checkLabels[i] = l;
        }
        getValues();
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, CHECK);
        checkDy = layout.height + 5;
    }

    private void getValues()
    {
        checkValues[4] = game.config.showMoves;
        checkValues[3] = game.config.showTargets;
        checkValues[2] = game.config.showMoveAssists;
        checkValues[1] = game.config.showEnemyPossibilities;
        checkValues[0] = game.config.debug;
        fxVolumeIdx = (int) (game.config.fxVolume * 10);
        fxVolumeValue.write(game.config.fxStrings[fxVolumeIdx], fxVolumeValue.getX(), fxVolumeValue.getY());
        graphicsIdx = game.config.graphics.i;
        graphicsValue.write(game.config.graphics.s, graphicsValue.getX(), graphicsValue.getY());
    }

    private void apply()
    {
        game.config.showMoves = checkValues[4];
        game.config.showTargets = checkValues[3];
        game.config.showMoveAssists = checkValues[2];
        game.config.showEnemyPossibilities = checkValues[1];
        game.config.debug = checkValues[0];
        game.config.fxVolume = (fxVolumeIdx / 10.0f);
        game.config.graphics = game.config.graphics.get(graphicsIdx);
        game.db.storeConfig(game.config.unload());
    }

    private void cycleFxVolume()
    {
        fxVolumeIdx += 1;
        if (fxVolumeIdx > 10) fxVolumeIdx = 0;
        fxVolumeValue.write(game.config.fxStrings[fxVolumeIdx], fxVolumeValue.getX(), fxVolumeValue.getY());
    }

    private void cycleGraphics()
    {
        graphicsIdx = game.config.graphics.get(graphicsIdx).next().i;
        graphicsValue.write(game.config.graphics.get(graphicsIdx).s, graphicsValue.getX(), graphicsValue.getY());
    }

    @Override
    public void computePosition()
    {
        float h = (title.getHeight() + TITLE_PADDING + (2 * PADDING));
        for (int i = 0; i < checkLabels.length; i++)
            h += checkLabels[i].getHeight();
        h += graphics.getHeight();
        h += fxVolume.getHeight();

        float w = title.getWidth();
        for (int i = 0; i < checkLabels.length; i++) {
            float t = checkLabels[i].getWidth();
            if (t > w)
                w = t;
        }
        w += (2 * PADDING) + HSPACING;

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        setBottomRight(okBtn);
        setBottomLeft(cancelBtn);

        y += PADDING;
        x += PADDING + HSPACING;

        graphics.setPosition(x, y);
        graphicsValue.setPosition((x + graphics.getWidth() + OPT_PADDING), y);
        y += graphics.getHeight();
        fxVolume.setPosition(x, y);
        fxVolumeValue.setPosition((x + fxVolume.getWidth() + OPT_PADDING), y);
        y += fxVolume.getHeight();
        for (int i = 0; i < checkLabels.length; i++) {
            checkLabels[i].setPosition(x, y);
            y += checkLabels[i].getHeight();
        }
        y += TITLE_PADDING;
        x -= PADDING;
        title.setPosition(x, y);
    }

    @Override
    public void postAnswer(boolean ok) { }

    @Override
    public String getAsk() { return null; }

    @Override
    public MenuCtrl.MenuType prepare() { return MenuCtrl.MenuType.OPTIONS; }

    @Override
    public boolean drag(float x, float y, int dx, int dy) { return true; }

    @Override
    public MenuCtrl.MenuType touch(float x, float y)
    {
        if (okBtn.hit(x, y)) {
            game.enterSnd.play();
            apply();
            return MenuCtrl.MenuType.MAIN;
        } else if (cancelBtn.hit(x, y)) {
            game.typeSnd.play();
            getValues();
            return MenuCtrl.MenuType.MAIN;
        } else if (fxVolume.hit(x, y) || fxVolumeValue.hit(x, y)) {
            game.typeSnd.play();
            cycleFxVolume();
        } else if (graphics.hit(x, y) || graphicsValue.hit(x, y)) {
            game.typeSnd.play();
            cycleGraphics();
        } else {
            for (int i = 0; i < checkLabels.length; i++) {
                if (checkLabels[i].hit(x, y)) {
                    game.typeSnd.play();
                    checkValues[i] =! checkValues[i];
                }
            }
        }

        return MenuCtrl.MenuType.NONE;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        title.dispose();
        okBtn.dispose();
        cancelBtn.dispose();
        fxVolume.dispose();
        fxVolumeValue.dispose();
        graphics.dispose();
        graphicsValue.dispose();
        for (int i = 0; i < checkLabels.length; i++)
            checkLabels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        super.draw(batch);
        title.draw(batch);
        okBtn.draw(batch);
        cancelBtn.draw(batch);
        fxVolume.draw(batch);
        fxVolumeValue.draw(batch);
        graphics.draw(batch);
        graphicsValue.draw(batch);
        for (int i = 0; i < checkLabels.length; i++) {
            Label l = checkLabels[i];
            l.draw(batch);
            if (checkValues[i])
                font.draw(batch, CHECK, (l.getX() - HSPACING) , l.getY() + checkDy);
        }
    }
}
