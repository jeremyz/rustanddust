package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.ui.Position;

public class OptionsPanel extends Patch
{
    private String [] fxStrings = { "OFF", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "ON" };

    public static int PADDING = 30;
    public static int OPT_PADDING = 10;
    public static int TITLE_PADDING = 20;
    public static int VSPACING = 15;
    public static int HSPACING = 30;

    private RustAndDust game;
    private Label title;
    private int fxVolumeIdx;
    private Label fxVolume;
    private Label fxVolumeValue;
    private Label quit;

    public OptionsPanel(RustAndDust game)
    {
        super(game.ninePatch);
        this.game = game;
        this.title = new Label(game.font);
        this.title.write("- Options");
        this.fxVolume = new Label(game.font);
        this.fxVolume.write("Fx Volume");
        this.fxVolumeValue = new Label(game.font);
        this.quit = new Label(game.font);
        this.quit.write("Quit battle");
        this.visible = false;
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
    }

    public void show()
    {
        show(Position.MIDDLE_CENTER);
    }

    public void show(Position position)
    {

        fxVolumeValue.write(fxStrings[0]);
        float h = (title.getHeight() + TITLE_PADDING + fxVolumeValue.getHeight() + (2 * PADDING));
        h += (quit.getHeight() + (2 * VSPACING));
        float w = (fxVolume.getWidth() + fxVolumeValue.getWidth() + HSPACING + OPT_PADDING + (2 * PADDING));

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        y += PADDING;
        x += PADDING + HSPACING;

        fxVolume.setPosition(x, y);
        fxVolumeValue.setPosition((x + fxVolume.getWidth() + OPT_PADDING), y);
        y += (VSPACING + fxVolume.getHeight());

        y += VSPACING;
        quit.setPosition(x,y);
        y += (VSPACING + quit.getHeight());

        y += (TITLE_PADDING - VSPACING);
        x -= PADDING;
        title.setPosition(x, y);

        getValues();

        visible = true;
    }

    private void getValues()
    {
        fxVolumeIdx = (int) (game.config.fxVolume * 10);
        fxVolumeValue.write(fxStrings[fxVolumeIdx], fxVolumeValue.getX(), fxVolumeValue.getY());
    }

    private void cycleFxVolume()
    {
        fxVolumeIdx += 1;
        if (fxVolumeIdx > 10) fxVolumeIdx = 0;
        fxVolumeValue.write(fxStrings[fxVolumeIdx], fxVolumeValue.getX(), fxVolumeValue.getY());
        game.config.fxVolume = (fxVolumeIdx / 10.0f);
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (!visible) return false;

        if (fxVolume.hit(x, y) || fxVolumeValue.hit(x, y)) {
            cycleFxVolume();
        } else if (quit.hit(x, y)) {
            game.ctrl.hud.askQuitBattle();
        }

        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        fxVolume.dispose();
        fxVolumeValue.dispose();
        quit.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        fxVolume.draw(batch);
        fxVolumeValue.draw(batch);
        quit.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        title.drawDebug(shapes);
        fxVolume.drawDebug(shapes);
        fxVolumeValue.drawDebug(shapes);
        quit.drawDebug(shapes);
    }
}
