package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.Map;

public class Engagement extends Bg
{
    public float padding;
    private Label attack;
    private Label defense;
    private Label attackV;
    private Label defenseV;
    private Label msg;
    private Bg okBtn;

    public Engagement(BitmapFont font, TextureRegion region, TextureAtlas atlas, float padding)
    {
        super(region);
        this.attack = new Label(font);
        this.defense = new Label(font);
        this.attackV = new Label(font);
        this.defenseV = new Label(font);
        this.msg = new Label(font);
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.attack.write("Attack: \n  2D6\n  units\n  flank");
        this.defense.write("Defense: \n  defense\n  terrain \n  weather");
        this.visible = false;
        this.padding = padding;
    }

    public void show(Map.Engagement e, Position position)
    {
        attackV.write(String.format("%d\n%d + %d\n%d\n%d", e.attack, e.d1, e.d2, e.unitCount, e.flankBonus));
        defenseV.write(String.format("%d\n%d\n%d\n%d", e.defense, e.unitDefense, e.terrainDefense, e.weatherDefense));
        msg.write(e.msg);

        float height = (attack.getHeight() + okBtn.getHeight() + msg.getHeight() + (4 * padding));
        float width = (attack.getWidth() + defense.getWidth() + attackV.getWidth() + defenseV.getWidth() + (5 * padding));
        float w2 = (msg.getWidth() + (2 * padding));
        if (w2 > width) width = w2;
        float x = position.getX(width);
        float y = position.getY(height);
        set(x, y, width, height);

        x += padding;
        y += padding;
        okBtn.setPosition((x + width - okBtn.getWidth() - padding), y);
        y += (okBtn.getHeight() + padding);
        msg.setPosition(x, y);
        y += msg.getHeight() + padding;
        attack.setPosition(x, y);
        x += attack.getWidth() + padding;
        attackV.setPosition(x, y);
        x += attackV.getWidth() + padding;
        defense.setPosition(x, y);
        x += defense.getWidth() + padding;
        defenseV.setPosition(x, y);
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
        attack.dispose();
        defense.dispose();
        attackV.dispose();
        defenseV.dispose();
        msg.dispose();
        okBtn.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        attack.draw(batch);
        defense.draw(batch);
        attackV.draw(batch);
        defenseV.draw(batch);
        msg.draw(batch);
        okBtn.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        attack.drawDebug(shapes);
        defense.drawDebug(shapes);
        attackV.drawDebug(shapes);
        defenseV.drawDebug(shapes);
        msg.drawDebug(shapes);
        okBtn.drawDebug(shapes);
    }
}
