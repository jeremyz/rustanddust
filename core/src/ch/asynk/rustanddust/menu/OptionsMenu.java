package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Patch;

import ch.asynk.rustanddust.RustAndDust;

public class OptionsMenu extends Patch
{
    public static int PADDING = 30;
    public static int OPT_PADDING = 10;
    public static int TITLE_PADDING = 20;
    public static int VSPACING = 5;
    public static int HSPACING = 30;
    public static String CHECK = "#";

    private final RustAndDust game;
    private final BitmapFont font;

    private String [] checkStrings = {
        "Debug",
        "Show Enemy Possibilities",
        "Show Moves Assists",
        "Show Targets",
        "Show Moves",
    };
    private String [] fxStrings = { "OFF", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "ON" };

    private float checkDy;
    private Label title;
    private Label fxVolume;
    private Label fxVolumeValue;
    private Label [] checkLabels;
    private int fxVolumeIdx;
    private boolean [] checkValues;
    protected Bg okBtn;
    protected Bg cancelBtn;

    public OptionsMenu(RustAndDust game)
    {
        super(game.ninePatch);
        this.game = game;
        this.font = game.font;
        this.okBtn = new Bg(game.getUiRegion(game.UI_OK));
        this.cancelBtn = new Bg(game.getUiRegion(game.UI_CANCEL));
        this.title = new Label(font);
        this.title.write("- Options");
        this.fxVolume = new Label(font);
        this.fxVolume.write("Fx Volume");
        this.fxVolumeValue = new Label(font);
        this.checkValues = new boolean[checkStrings.length];
        this.checkLabels = new Label[checkStrings.length];
        for (int i = 0; i < checkLabels.length; i++) {
            Label l = new Label(font, 5f);
            l.write(checkStrings[i]);
            this.checkLabels[i] = l;
        }
        getValues();
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, CHECK);
        checkDy = layout.height + 5;

        this.visible = false;
    }

    private void getValues()
    {
        checkValues[4] = game.config.showMoves;
        checkValues[3] = game.config.showTargets;
        checkValues[2] = game.config.showMoveAssists;
        checkValues[1] = game.config.showEnemyPossibilities;
        checkValues[0] = game.config.debug;
        fxVolumeIdx = (int) (game.config.fxVolume * 10);
        fxVolumeValue.write(fxStrings[fxVolumeIdx], fxVolumeValue.getX(), fxVolumeValue.getY());
    }

    private boolean apply()
    {
        game.config.showMoves = checkValues[4];
        game.config.showTargets = checkValues[3];
        game.config.showMoveAssists = checkValues[2];
        game.config.showEnemyPossibilities = checkValues[1];
        game.config.debug = checkValues[0];
        game.config.fxVolume = (fxVolumeIdx / 10.0f);
        return true;
    }

    private void cycleFxVolume()
    {
        fxVolumeIdx += 1;
        if (fxVolumeIdx > 10) fxVolumeIdx = 0;
        fxVolumeValue.write(fxStrings[fxVolumeIdx], fxVolumeValue.getX(), fxVolumeValue.getY());
    }

    public void setPosition()
    {
        float h = (title.getHeight() + TITLE_PADDING + ((checkLabels.length - 1) * VSPACING) + (2 * PADDING));
        for (int i = 0; i < checkLabels.length; i++)
            h += checkLabels[i].getHeight();
        h += (fxVolume.getHeight() + VSPACING);

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

        setBtnRight(okBtn);
        setBtnLeft(cancelBtn);

        y += PADDING;
        x += PADDING + HSPACING;

        fxVolume.setPosition(x, y);
        fxVolumeValue.setPosition((x + fxVolume.getWidth() + OPT_PADDING), y);
        y += (VSPACING + fxVolume.getHeight());
        for (int i = 0; i < checkLabels.length; i++) {
            checkLabels[i].setPosition(x, y);
            y += (VSPACING + checkLabels[i].getHeight());
        }
        y += (TITLE_PADDING - VSPACING);
        x -= PADDING;
        title.setPosition(x, y);
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (!visible) return false;

        if (okBtn.hit(x, y)) {
            return apply();
        } else if (cancelBtn.hit(x, y)) {
            getValues();
            return true;
        } else if (fxVolume.hit(x, y) || fxVolumeValue.hit(x, y)) {
            cycleFxVolume();
        } else {
            for (int i = 0; i < checkLabels.length; i++) {
                if (checkLabels[i].hit(x, y))
                    checkValues[i] =! checkValues[i];
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
        cancelBtn.dispose();
        fxVolume.dispose();
        fxVolumeValue.dispose();
        for (int i = 0; i < checkLabels.length; i++)
            checkLabels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        okBtn.draw(batch);
        cancelBtn.draw(batch);
        fxVolume.draw(batch);
        fxVolumeValue.draw(batch);
        for (int i = 0; i < checkLabels.length; i++) {
            Label l = checkLabels[i];
            l.draw(batch);
            if (checkValues[i])
                font.draw(batch, CHECK, (l.getX() - HSPACING) , l.getY() + checkDy);
        }
    }
}
