package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.game.Player;

public class Statistics extends Bg
{
    public float padding;
    protected Label title;
    protected Label header;
    protected Label stats1;
    protected Label stats2;
    protected Bg okBtn;

    public Statistics(BitmapFont font, TextureRegion region, TextureAtlas atlas, float padding)
    {
        super(region);
        this.title = new Label(font);
        this.header = new Label(font);
        this.stats1 = new Label(font);
        this.stats2 = new Label(font);
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.visible = false;
        this.padding = padding;
        this.header.write("\nActions\nUnits Left\nCasualties\nWon Attacks\nLost Attacks");
    }

    public void show(Player winner, Player loser, Position position)
    {
        title.write(winner.getName() + " player won the battle in " + winner.getTurnDone() + " turns.");
        stats1.write(winner.getStats());
        stats2.write(loser.getStats());

        float height = (title.getHeight() + header.getHeight() + okBtn.getHeight() + (4 * padding));
        float width = (header.getWidth() + stats1.getWidth() + stats2.getWidth() + (6 * padding));
        float w2 = (title.getWidth() + (2 * padding));
        if (w2 > width) width = w2;
        float x = position.getX(width);
        float y = position.getY(height);
        set(x, y, width, height);

        y += padding;
        okBtn.setPosition((x + width - okBtn.getWidth() - padding), y);
        x += padding;
        y += (okBtn.getHeight() + padding);
        header.setPosition(x, y);
        stats1.setPosition((x + header.getWidth() + (2 * padding)), y);
        stats2.setPosition((stats1.getX() + stats1.getWidth() + (2 * padding)), y);
        y += (header.getHeight() + padding);
        title.setPosition(x, y);
        visible = true;
    }

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
