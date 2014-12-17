package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.DiceAnimation;

public class Engagement extends Patch implements Animation
{
    public static int FLAG_HEIGHT = 24;
    public static int OK_OFFSET = 10;
    public static int PADDING = 20;
    public static int VSPACING = 10;
    public static int HSPACING = 5;
    public static float REROLL_DELAY = 0.3f;

    private boolean reroll;
    private boolean roll2;
    private float delay;
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
    private DiceAnimation d3Animation;
    private DiceAnimation d4Animation;

    public Engagement(BitmapFont font, TextureAtlas atlas)
    {
        super(atlas.createPatch("typewriter"));
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
        this.d1Animation = new DiceAnimation();
        this.d2Animation = new DiceAnimation();
        this.d3Animation = new DiceAnimation();
        this.d4Animation = new DiceAnimation();
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
        winner.translate(dx, dy);
        attackImg.translate(dx, dy);
        defenseImg.translate(dx, dy);
        attack.translate(dx, dy);
        defense.translate(dx, dy);
        attackR.translate(dx, dy);
        defenseR.translate(dx, dy);
        okBtn.translate(dx, dy);
        d1Animation.translate(dx, dy);
        d2Animation.translate(dx, dy);
        d3Animation.translate(dx, dy);
        d4Animation.translate(dx, dy);
    }

    public void show(Map.Engagement e, Position position, float volume)
    {
        DiceAnimation.initSound(volume);
        attack.write(String.format(" + %d + %d =", e.unitCount, e.flankBonus));
        if (e.weatherDefense == 0)
            defense.write(String.format("%d + %d =", e.unitDefense, e.terrainDefense));
        else
            defense.write(String.format("%d + %d + %d =", e.unitDefense, e.terrainDefense, e.weatherDefense));
        attackR.write(String.format(" %2d", e.attack));
        defenseR.write(String.format(" %2d", e.defense));
        if (e.success)
            winner = ((e.attacker == Army.US) ? usFlag : geFlag);
        else
            winner = ((e.attacker == Army.US) ? geFlag : usFlag);


        reroll = (e.d3 != 0);
        delay = 0f;
        d1Animation.set(e.d1);
        d2Animation.set(e.d2);
        if (reroll) {
            d3Animation.set(e.d3);
            d4Animation.set(e.d4);
        }

        this.position = position;
        placeElements();

        visible = true;
    }

    private void placeElements()
    {
        float w = attackR.getWidth();
        float w2 = defenseR.getWidth();
        if (w2 > w)
            w = w2;
        float height = (okBtn.getHeight() + attackImg.getHeight() + defenseImg.getHeight() + (2 * VSPACING) + (2 * PADDING));
        float width = (attackImg.getWidth() + (2 * d1Animation.getWidth()) + attack.getWidth() + w + (4 * HSPACING) + (2 * PADDING));
        float x = position.getX(width);
        float y = position.getY(height);
        setPosition(x, y, width, height);

        okBtn.setPosition((x + width - okBtn.getWidth() + OK_OFFSET), (y - OK_OFFSET));

        x = getX() + PADDING;
        y = getY() + PADDING;
        winner.setPosition((getX() + (width / 2f) - (winner.getWidth() / 2f)), y);
        y += (winner.getHeight() + VSPACING);

        defenseImg.setPosition(x, y);
        y = (y + (defenseImg.getHeight() / 2f) - (defense.getHeight() / 2f));
        defenseR.setPosition((getX() + width - w - PADDING), y);
        // x += (defenseImg.getWidth() + HSPACING);
        defense.setPosition((defenseR.getX() - defense.getWidth() - HSPACING), y);

        x = getX() + PADDING;
        y += defenseImg.getHeight() + VSPACING;
        attackImg.setPosition(x, y);
        x += (attackImg.getWidth() + HSPACING);
        d1Animation.setPosition(x, y);
        x += (d1Animation.getWidth() + HSPACING);
        d2Animation.setPosition(x, (y));
        x += (d1Animation.getWidth() + HSPACING);
        y = (y + (attackImg.getHeight() / 2f) - (attack.getHeight() / 2f));
        attack.setPosition(x, y);
        attackR.setPosition(defenseR.getX(), y);

        if (reroll) {
            d3Animation.setPosition(x, y);
            d4Animation.setPosition(x, y);
        }
    }

    private void reroll()
    {
        // hud.notify("Ace re-roll");
        roll2 = true;
        float h = (getHeight() + d1Animation.getHeight() + VSPACING);
        setPosition(getX(), getY(), getWidth(), h);
        d3Animation.setPosition(d1Animation.getX(), d1Animation.getY());
        d4Animation.setPosition(d2Animation.getX(), d2Animation.getY());
        d1Animation.setPosition(d1Animation.getX(), (d1Animation.getY() + d1Animation.getHeight() + VSPACING));
        d2Animation.setPosition(d2Animation.getX(), (d2Animation.getY() + d2Animation.getHeight() + VSPACING));
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
        if (!roll2) {
            d1Animation.animate(delta);
            d2Animation.animate(delta);
        } else {
            if (delay < REROLL_DELAY) {
                delay += delta;
                return false;
            }
            d3Animation.animate(delta);
            d4Animation.animate(delta);
        }
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
        d3Animation.dispose();
        d4Animation.dispose();
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
        if (roll2) {
            d3Animation.draw(batch);
            d4Animation.draw(batch);
        }
        attack.draw(batch);
        defenseImg.draw(batch);
        defense.draw(batch);
        defenseR.draw(batch);
        okBtn.draw(batch);
        if (d1Animation.isDone() && d2Animation.isDone()) {
            if (reroll) {
                if (!roll2)
                    reroll();
                if (d3Animation.isDone() && d4Animation.isDone()) {
                    attackR.draw(batch);
                    winner.draw(batch);
                }
            } else {
                attackR.draw(batch);
                winner.draw(batch);
            }
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
