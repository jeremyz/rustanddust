package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.ui.Position;

public class OptionsPanel extends Patch
{
    private String [] checkStrings = {
    };

    public static int PADDING = 30;
    public static int OPT_PADDING = 10;
    public static int TITLE_PADDING = 5;
    public static int LABEL_PADDING = 10;
    public static int HSPACING = 30;
    public static String CHECK = "#";

    private float checkDy;
    private final BitmapFont font;
    private RustAndDust game;
    private Label title;
    private int fxVolumeIdx;
    private Label fxVolume;
    private Label fxVolumeValue;
    private Label objectives;
    private ObjectivesPanel objectivesPanel;
    private Label quit;
    private Label [] checkLabels;
    private boolean [] checkValues;

    public OptionsPanel(RustAndDust game)
    {
        super(game.bgPatch);
        this.game = game;
        this.font = game.font;
        this.title = new Label("- Options", game.font, LABEL_PADDING);
        this.fxVolume = new Label("Fx Volume", game.font, LABEL_PADDING);
        this.fxVolumeValue = new Label(game.font, LABEL_PADDING);
        this.objectives = new Label("Battle Objectives", game.font, LABEL_PADDING);
        this.objectivesPanel = new ObjectivesPanel(game);
        this.quit = new Label("Quit Battle", game.font, LABEL_PADDING);
        this.visible = false;
        this.checkValues = new boolean[checkStrings.length];
        this.checkLabels = new Label[checkStrings.length];
        for (int i = 0; i < checkLabels.length; i++) {
            Label l = new Label(checkStrings[i], game.font, LABEL_PADDING);
            this.checkLabels[i] = l;
        }
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, CHECK);
        checkDy = layout.height + 5;
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
        objectivesPanel.updatePosition();
    }

    public void show()
    {
        show(Position.MIDDLE_CENTER);
    }

    public void show(Position position)
    {
        fxVolumeValue.write(game.config.fxStrings[0]);
        float h = (title.getHeight() + TITLE_PADDING + fxVolumeValue.getHeight());
        for (int i = 0; i < checkLabels.length; i++)
            h += checkLabels[i].getHeight();
        h += objectives.getHeight();
        h += quit.getHeight();
        h += (2 * PADDING);

        float w = (objectives.getWidth());
        for (int i = 0; i < checkLabels.length; i++) {
            float t = checkLabels[i].getWidth();
            if (t > w)
                w = t;
        }
        w += HSPACING + OPT_PADDING + (2 * PADDING);

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        y += PADDING;
        x += PADDING + HSPACING;

        objectives.setPosition(x, y);
        y += fxVolume.getHeight();

        fxVolume.setPosition(x, y);
        fxVolumeValue.setPosition((x + fxVolume.getWidth() + OPT_PADDING), y);
        y += fxVolume.getHeight();

        for (int i = 0; i < checkLabels.length; i++) {
            checkLabels[i].setPosition(x, y);
            y += checkLabels[i].getHeight();
        }

        quit.setPosition(x,y);
        y += quit.getHeight();

        y += TITLE_PADDING;
        x -= PADDING;
        title.setPosition(x, y);

        getValues();

        visible = true;
    }

    private void getValues()
    {
        fxVolumeIdx = (int) (game.config.fxVolume * 10);
        fxVolumeValue.write(game.config.fxStrings[fxVolumeIdx], fxVolumeValue.getX(), fxVolumeValue.getY());
    }

    private void cycleFxVolume()
    {
        fxVolumeIdx += 1;
        if (fxVolumeIdx > 10) fxVolumeIdx = 0;
        fxVolumeValue.write(game.config.fxStrings[fxVolumeIdx], fxVolumeValue.getX(), fxVolumeValue.getY());
        game.config.fxVolume = (fxVolumeIdx / 10.0f);
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (objectivesPanel.hit(x, y)) {
            objectivesPanel.visible = false;
            this.visible = true;
            return false;
        }

        if (!visible) return false;

        if (fxVolume.hit(x, y) || fxVolumeValue.hit(x, y)) {
            cycleFxVolume();
        } else if (quit.hit(x, y)) {
            game.ctrl.hud.askQuitBattle();
            return false;
        } else if (objectives.hit(x, y)) {
            this.visible = false;
            objectivesPanel.show(game.config.battle);
            return false;
        } else {
            for (int i = 0; i < checkLabels.length; i++) {
                if (checkLabels[i].hit(x, y))
                    checkValues[i] =! checkValues[i];
            }
        }

        if (!super.hit(x,y)) {
            apply();
            return true;
        }

        return false;
    }

    public void apply()
    {
    }

    public void close()
    {
        apply();
        objectivesPanel.visible = false;
        this.visible = false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        title.dispose();
        objectives.dispose();
        fxVolume.dispose();
        fxVolumeValue.dispose();
        quit.dispose();
        for (int i = 0; i < checkLabels.length; i++)
            checkLabels[i].dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        objectivesPanel.draw(batch);
        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        objectives.draw(batch);
        fxVolume.draw(batch);
        fxVolumeValue.draw(batch);
        quit.draw(batch);
        for (int i = 0; i < checkLabels.length; i++) {
            Label l = checkLabels[i];
            l.draw(batch);
            if (checkValues[i])
                font.draw(batch, CHECK, (l.getX() - HSPACING) , l.getY() + checkDy);
        }
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        objectivesPanel.drawDebug(shapes);
        if (!visible) return;
        super.drawDebug(shapes);
        title.drawDebug(shapes);
        objectives.drawDebug(shapes);
        fxVolume.drawDebug(shapes);
        fxVolumeValue.drawDebug(shapes);
        quit.drawDebug(shapes);
    }
}
