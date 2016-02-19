package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Patch;
import ch.asynk.rustanddust.ui.Position;

public class StatisticsPanel extends Patch
{
    public static int PADDING = 20;
    public static int VSPACING = 10;
    public static int HSPACING = 10;

    private Label title;
    private Label header;
    private Label stats1;
    private Label stats2;
    private Bg okBtn;
    private Bg flag;
    private Bg geFlag;
    private Bg usFlag;

    public StatisticsPanel(RustAndDust game)
    {
        super(game.bgPatch);
        this.title = new Label(game.font);
        this.header = new Label(game.font);
        this.stats1 = new Label(game.font);
        this.stats2 = new Label(game.font);
        this.okBtn = new Bg(game.factory.getHudRegion(game.factory.ACT_DONE));
        this.usFlag = new Bg(game.factory.getFlag(Army.US));
        this.geFlag = new Bg(game.factory.getFlag(Army.GE));
        this.visible = false;
        this.header.write("\nActions\nUnits Left\nUnits Withrawed\nCasualties\nObjectives");
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
        flag.translate(dx, dy);
        title.translate(dx, dy);
        header.translate(dx, dy);
        stats1.translate(dx, dy);
        stats2.translate(dx, dy);
        okBtn.translate(dx, dy);
    }

    public void show(Player winner, Player loser, Position position)
    {
        flag = ((winner.army == Army.US) ? usFlag : geFlag);
        title.write("is triumphant in " + winner.getTurn() + " turns.");
        stats1.write(winner.getStats());
        stats2.write(loser.getStats());

        float height = (flag.getHeight() + header.getHeight() + (2 * PADDING) + (1 * VSPACING));
        float width = (header.getWidth() + stats1.getWidth() + stats2.getWidth() + (2 * PADDING) + (4 * HSPACING));
        float w2 = (flag.getWidth() + HSPACING + title.getWidth() + (2 * PADDING));
        if (w2 > width) width = w2;
        float x = position.getX(width);
        float y = position.getY(height);
        setPosition(x, y, width, height);

        setBottomRight(okBtn);

        y += PADDING;
        x += PADDING;
        header.setPosition(x, y);
        stats1.setPosition((x + header.getWidth() + (2 * HSPACING)), y);
        stats2.setPosition((stats1.getX() + stats1.getWidth() + (2 * HSPACING)), y);
        y += (header.getHeight() + VSPACING);
        flag.setPosition(x, y);
        title.setPosition(x + flag.getWidth() + VSPACING, y + flag.getHeight() / 3);
        visible = true;
    }

    @Override
    public boolean hit(float x, float y)
    {
        return (rect.contains(x, y) || okBtn.hit(x, y));
    }

    @Override
    public void dispose()
    {
        super.dispose();
        geFlag.dispose();
        usFlag.dispose();
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
        flag.draw(batch);
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
        flag.drawDebug(shapes);
        title.drawDebug(shapes);
        header.drawDebug(shapes);
        stats1.drawDebug(shapes);
        stats2.drawDebug(shapes);
        okBtn.drawDebug(shapes);
    }
}
