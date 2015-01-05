package ch.asynk.tankontank.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.ui.Label;
import ch.asynk.tankontank.ui.Bg;
import ch.asynk.tankontank.ui.Patch;

import ch.asynk.tankontank.TankOnTank;

public class OptionsMenu extends Patch
{
    public static int PADDING = 40;
    public static int OK_PADDING = 10;
    public static int TITLE_PADDING = 30;
    public static int VSPACING = 20;
    public static int HSPACING = 30;
    public static String CHECK = "#";

    private final TankOnTank game;
    private final BitmapFont font;

    private String [] checkStrings = {
        "Debug",
        "Use Reqular Pawns",
        "Must Validate",
        "Can Cancel",
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
    private boolean [] checkValues;
    protected Bg okBtn;

    public OptionsMenu(TankOnTank game, BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
        this.game = game;
        this.font = font;
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.title = new Label(font);
        this.title.write("- Options");
        this.fxVolume = new Label(font);
        this.fxVolume.write("Fx Volume");
        this.fxVolumeValue = new Label(font);
        this.fxVolumeValue.write(fxStrings[(int) (game.config.fxVolume * 10)]);
        this.checkValues = new boolean[checkStrings.length];
        this.checkLabels = new Label[checkStrings.length];
        for (int i = 0; i < checkLabels.length; i++) {
            Label l = new Label(font);
            l.write(checkStrings[i]);
            this.checkLabels[i] = l;
        }
        getValues();
        checkDy = font.getMultiLineBounds(CHECK).height;

        this.visible = false;
    }

    private void getValues()
    {
        checkValues[7] = game.config.showMoves;
        checkValues[6] = game.config.showTargets;
        checkValues[5] = game.config.showMoveAssists;
        checkValues[4] = game.config.showEnemyPossibilities;
        checkValues[3] = game.config.canCancel;
        checkValues[2] = game.config.mustValidate;
        checkValues[1] = game.config.regularPawns;
        checkValues[0] = game.config.debug;
    }

    private void apply()
    {
        game.config.showMoves = checkValues[7];
        game.config.showTargets = checkValues[6];
        game.config.showMoveAssists = checkValues[5];
        game.config.showEnemyPossibilities = checkValues[4];
        game.config.canCancel = checkValues[3];
        game.config.mustValidate = checkValues[2];
        game.config.regularPawns = checkValues[1];
        game.config.debug = checkValues[0];
    }

    private void cycleFxVolume()
    {
            int i = (int) (game.config.fxVolume * 10) + 1;
            if (i > 10) i = 0;
            float fx = fxVolumeValue.getX();
            float fy = fxVolumeValue.getY();
            fxVolumeValue.write(fxStrings[i]);
            fxVolumeValue.setPosition(fx, fy);
            game.config.fxVolume = (i / 10f);
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

        okBtn.setPosition((x + w - okBtn.getWidth() + OK_PADDING), (y - OK_PADDING));

        y += PADDING;
        x += PADDING + HSPACING;
        float dy = (VSPACING + title.getHeight());

        fxVolume.setPosition(x, y);
        fxVolumeValue.setPosition((x + fxVolume.getWidth() + 10), y);
        y += dy;
        for (int i = 0; i < checkLabels.length; i++) {
            checkLabels[i].setPosition(x, y);
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
            apply();
            return true;
        } else if (fxVolume.hit(x, y)) {
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