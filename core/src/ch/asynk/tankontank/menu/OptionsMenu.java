package ch.asynk.tankontank.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.ui.Label;
import ch.asynk.tankontank.ui.Bg;
import ch.asynk.tankontank.ui.Patch;
import ch.asynk.tankontank.ui.OkCancel;

import ch.asynk.tankontank.TankOnTank;

public class OptionsMenu extends Patch
{
    public static int PADDING = 40;
    public static int OK_PADDING = 10;
    public static int TITLE_PADDING = 30;
    public static int VSPACING = 5;
    public static int HSPACING = 30;
    public static String CHECK = "#";

    private final TankOnTank game;
    private final BitmapFont font;

    private String [] checkStrings = {
        "Debug",
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
    private Label graphics;
    private Label graphicsValue;
    private Label gameMode;
    private Label gameModeValue;
    private Label [] checkLabels;
    private boolean [] checkValues;
    private OkCancel okCancel;
    protected Bg okBtn;

    public OptionsMenu(TankOnTank game, BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
        this.game = game;
        this.font = font;
        this.okCancel = new OkCancel(font, atlas);
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.title = new Label(font);
        this.title.write("- Options");
        this.fxVolume = new Label(font);
        this.fxVolume.write("Fx Volume");
        this.fxVolumeValue = new Label(font);
        this.fxVolumeValue.write(fxStrings[(int) (game.config.fxVolume * 10)]);
        this.graphics = new Label(font);
        this.graphics.write("Graphics");
        this.graphicsValue = new Label(font);
        this.graphicsValue.write(game.config.graphics.s);
        this.gameMode = new Label(font);
        this.gameMode.write("Game mode");
        this.gameModeValue = new Label(font);
        this.gameModeValue.write(game.config.gameMode.s);
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
        checkValues[6] = game.config.showMoves;
        checkValues[5] = game.config.showTargets;
        checkValues[4] = game.config.showMoveAssists;
        checkValues[3] = game.config.showEnemyPossibilities;
        checkValues[2] = game.config.canCancel;
        checkValues[1] = game.config.mustValidate;
        checkValues[0] = game.config.debug;
    }

    private boolean apply()
    {
        game.config.showMoves = checkValues[6];
        game.config.showTargets = checkValues[5];
        game.config.showMoveAssists = checkValues[4];
        game.config.showEnemyPossibilities = checkValues[3];
        game.config.canCancel = checkValues[2];
        game.config.mustValidate = checkValues[1];
        game.config.debug = checkValues[0];
        if (!game.config.gameModeImplemented()) {
            this.visible = false;
            okCancel.show(String.format("'%s' Game Mode not implemented yet.", game.config.gameMode.s));
            okCancel.noCancel();
            return false;
        }
        return true;
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

    private void cycleGraphics()
    {
        game.config.graphics = game.config.graphics.next();
        float fx = graphicsValue.getX();
        float fy = graphicsValue.getY();
        graphicsValue.write(game.config.graphics.s);
        graphicsValue.setPosition(fx, fy);
    }

    private void cycleGameMode()
    {
        game.config.gameMode = game.config.gameMode.next();
        float fx = gameModeValue.getX();
        float fy = gameModeValue.getY();
        gameModeValue.write(game.config.gameMode.s);
        gameModeValue.setPosition(fx, fy);
    }

    public void setPosition()
    {
        float h = (title.getHeight() + TITLE_PADDING + ((checkLabels.length - 1) * VSPACING) + (2 * PADDING));
        for (int i = 0; i < checkLabels.length; i++)
            h += checkLabels[i].getHeight();
        h += (graphics.getHeight() + VSPACING);
        h += (gameMode.getHeight() + VSPACING);
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
        float dy = (VSPACING + checkLabels[0].getHeight());

        graphics.setPosition(x, y);
        graphicsValue.setPosition((x + graphics.getWidth() + 10), y);
        y += dy;
        gameMode.setPosition(x, y);
        gameModeValue.setPosition((x + gameMode.getWidth() + 10), y);
        y += dy;
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
        if (okCancel.hit(x, y)) {
            this.visible = true;
            okCancel.visible = false;
            return false;
        }

        if (!visible) return false;

        if (okBtn.hit(x, y)) {
            return apply();
        } else if (fxVolume.hit(x, y) || fxVolumeValue.hit(x, y)) {
            cycleFxVolume();
        } else if (graphics.hit(x, y) || graphicsValue.hit(x, y)) {
            cycleGraphics();
        } else if (gameMode.hit(x, y) || gameModeValue.hit(x, y)) {
            cycleGameMode();
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
        okCancel.dispose();
        fxVolume.dispose();
        fxVolumeValue.dispose();
        graphics.dispose();
        graphicsValue.dispose();
        gameMode.dispose();
        gameModeValue.dispose();
        for (int i = 0; i < checkLabels.length; i++)
            checkLabels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        okCancel.draw(batch);

        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        okBtn.draw(batch);
        fxVolume.draw(batch);
        fxVolumeValue.draw(batch);
        graphics.draw(batch);
        graphicsValue.draw(batch);
        gameMode.draw(batch);
        gameModeValue.draw(batch);
        for (int i = 0; i < checkLabels.length; i++) {
            Label l = checkLabels[i];
            l.draw(batch);
            if (checkValues[i])
                font.draw(batch, CHECK, (l.getX() - HSPACING) , l.getY() + checkDy);
        }
    }
}
