package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.game.Battle;

public class ObjectivesPanel extends Patch
{
    public static int PADDING = 30;
    public static int TITLE_PADDING = 5;
    public static int LABEL_PADDING = 10;

    private RustAndDust game;
    private Label title;
    private Label content;

    public ObjectivesPanel(RustAndDust game)
    {
        super(game.ninePatch);
        this.game = game;
        this.title = new Label(game.font, LABEL_PADDING);
        this.content = new Label(game.font, LABEL_PADDING);
        this.visible = false;
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
    }

    public void show(Battle battle)
    {
        this.title.write(String.format(" - %s -", battle.getName()));
        this.content.write(battle.getDescription());
        show(Position.MIDDLE_CENTER);
    }

    public void show(Position position)
    {
        float h = (title.getHeight() + TITLE_PADDING);
        h += content.getHeight();
        h += (2 * PADDING);

        float w = (content.getWidth());
        w += (2 * PADDING);

        float x = position.getX(w);
        float y = position.getY(h);
        setPosition(x, y, w, h);

        y += PADDING;
        x += PADDING;

        content.setPosition(x, y);
        y += content.getHeight();

        y += TITLE_PADDING;
        title.setPosition(x, y);

        visible = true;
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (!visible) return false;

        // if (super.hit(x,y)) return true;

        return true;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        title.dispose();
        content.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        content.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        title.drawDebug(shapes);
        content.drawDebug(shapes);
    }
}
