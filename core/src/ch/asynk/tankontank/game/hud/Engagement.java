package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.DiceAnimation;

public class Engagement extends Bg implements Animation
{
    public static int FLAG_HEIGHT = 24;
    public float padding;
    private Sprite usFlag;
    private Sprite geFlag;
    private Sprite winner;
    private Sprite attackImg;
    private Sprite defenseImg;
    private Label attack;
    private Label defense;
    private Label attackR;
    private Label defenseR;
    private Bg okBtn;
    private DiceAnimation d1Animation;
    private DiceAnimation d2Animation;

    public Engagement(BitmapFont font, TextureRegion region, TextureAtlas atlas, float padding)
    {
        super(region);
        usFlag = new Sprite(atlas.findRegion("us-flag"));
        geFlag = new Sprite(atlas.findRegion("ge-flag"));
        attackImg = new Sprite(atlas.findRegion("attack"));
        defenseImg = new Sprite(atlas.findRegion("defense"));
        this.attack = new Label(font);
        this.defense = new Label(font);
        this.attackR = new Label(font);
        this.defenseR = new Label(font);
        this.okBtn = new Bg(atlas.findRegion("ok"));
        this.visible = false;
        this.padding = padding;
        this.d1Animation = new DiceAnimation();
        this.d2Animation = new DiceAnimation();
    }

    public void show(Map.Engagement e, Position position, float volume)
    {
        DiceAnimation.initSound(volume);
        attack.write(String.format(" + %d + %d", e.unitCount, e.flankBonus));
        defense.write(String.format("%d + %d + %d", e.unitDefense, e.terrainDefense, e.weatherDefense));
        attackR.write(String.format("= %2d", e.attack));
        defenseR.write(String.format("= %2d", e.defense));
        if (e.success)
            winner = ((e.attacker == Army.US) ? usFlag : geFlag);
        else
            winner = ((e.attacker == Army.US) ? geFlag : usFlag);

        float resultW = attackR.getWidth();
        float w = defenseR.getWidth();
        if (w > resultW)
            resultW = w;
        float height = (okBtn.getHeight() + attackImg.getHeight() + defenseImg.getHeight() + (4 * padding));
        float width = (attackImg.getWidth() + (2 * d1Animation.getWidth()) + attack.getWidth() + resultW + (6 * padding));
        float x = position.getX(width);
        float y = position.getY(height);
        set(x, y, width, height);

        y += padding;
        okBtn.setPosition((x + width - okBtn.getWidth() - padding), y);
        winner.setPosition((getX() + ((okBtn.getX() - getX()) / 2.0f) - (winner.getWidth() / 2.0f)), y);
        x += padding;
        y += (okBtn.getHeight() + padding);

        defenseImg.setPosition(x, y);
        x += (defenseImg.getWidth() + padding);
        y = (y + (defenseImg.getHeight() / 2.0f) - (defense.getHeight() / 2.0f));
        defense.setPosition(x, y);
        defenseR.setPosition((getX() + width - resultW- padding), y);

        x = getX() + padding;
        y += defenseImg.getHeight() + padding;
        attackImg.setPosition(x, y);
        x += (attackImg.getWidth() + padding);
        d1Animation.set(e.d1, x, y);
        x += (d1Animation.getWidth() + padding);
        d2Animation.set(e.d2, x, (y));
        x += (d1Animation.getWidth() + padding);
        y = (y + (attackImg.getHeight() / 2.0f) - (attack.getHeight() / 2.0f));
        attack.setPosition(x, y);
        attackR.setPosition(defenseR.getX(), y);

        visible = true;
    }

    public boolean hit(float x, float y)
    {
        if (okBtn.hit(x, y))
            return true;
        return false;
    }

    @Override
    public boolean animate(float delta)
    {
        if (!visible) return true;
        d1Animation.animate(delta);
        d2Animation.animate(delta);
        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        attack.dispose();
        defense.dispose();
        attackR.dispose();
        defenseR.dispose();
        d1Animation.dispose();
        d2Animation.dispose();
        okBtn.dispose();
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        attackImg.draw(batch);
        d1Animation.draw(batch);
        d2Animation.draw(batch);
        attack.draw(batch);
        defenseImg.draw(batch);
        defense.draw(batch);
        defenseR.draw(batch);
        okBtn.draw(batch);
        if (d1Animation.isDone() && d2Animation.isDone()) {
            attackR.draw(batch);
            winner.draw(batch);
        }
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        attack.drawDebug(shapes);
        defense.drawDebug(shapes);
        attackR.drawDebug(shapes);
        defenseR.drawDebug(shapes);
        okBtn.drawDebug(shapes);
    }
}
