package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.ui.Bg;
import ch.asynk.tankontank.ui.Label;
import ch.asynk.tankontank.ui.Patch;
import ch.asynk.tankontank.ui.Position;

public class StatisticsPanel extends Patch
{
    public static int OK_OFFSET = 10;
    public static int PADDING = 20;
    public static int VSPACING = 10;
    public static int HSPACING = 10;

    private Label title;
    private Label header;
    private Label stats1;
    private Label stats2;
    private Bg okBtn;

    public StatisticsPanel(BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
        this.title = new Label(font);
        this.header = new Label(font);
        this.stats1 = new Label(font);
        this.stats2 = new Label(font);
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.visible = false;
        this.header.write("\nActions\nUnits Left\nUnits Withrawed\nCasualties\nWon Attacks\nLost Attacks");
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
        title.translate(dx, dy);
        header.translate(dx, dy);
        stats1.translate(dx, dy);
        stats2.translate(dx, dy);
        okBtn.translate(dx, dy);
    }

    public void show(Player winner, Player loser, Position position)
    {
        title.write(winner.getName() + " player won the battle in " + winner.getTurnDone() + " turns.");
        stats1.write(winner.getStats());
        stats2.write(loser.getStats());

        float height = (title.getHeight() + header.getHeight() + (2 * PADDING) + (1 * VSPACING));
        float width = (header.getWidth() + stats1.getWidth() + stats2.getWidth() + (2 * PADDING) + (4 * HSPACING));
        float w2 = (title.getWidth() + (2 * PADDING));
        if (w2 > width) width = w2;
        float x = position.getX(width);
        float y = position.getY(height);
        setPosition(x, y, width, height);

        okBtn.setPosition((x + width - okBtn.getWidth() + OK_OFFSET), (y - OK_OFFSET));

        y += PADDING;
        x += PADDING;
        header.setPosition(x, y);
        stats1.setPosition((x + header.getWidth() + (2 * HSPACING)), y);
        stats2.setPosition((stats1.getX() + stats1.getWidth() + (2 * HSPACING)), y);
        y += (header.getHeight() + VSPACING);
        title.setPosition(x, y);
        visible = true;
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (okBtn.hit(x, y))
            return true;
        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        title.dispose();
        header.dispose();
        stats1.dispose();
        stats2.dispose();
        okBtn.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        title.draw(batch);
        header.draw(batch);
        stats1.draw(batch);
        stats2.draw(batch);
        okBtn.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        title.drawDebug(shapes);
        header.drawDebug(shapes);
        stats1.drawDebug(shapes);
        stats2.drawDebug(shapes);
        okBtn.drawDebug(shapes);
    }
}
