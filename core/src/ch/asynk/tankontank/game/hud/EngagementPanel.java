package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.game.Engagement;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.DiceAnimation;
import ch.asynk.tankontank.ui.Bg;
import ch.asynk.tankontank.ui.Label;
import ch.asynk.tankontank.ui.Patch;
import ch.asynk.tankontank.ui.Position;

public class EngagementPanel extends Patch implements Animation
{
    private enum State { ROLL1, MOVE, ROLL2, RESULT };

    public static int FLAG_HEIGHT = 24;
    public static int OK_OFFSET = 10;
    public static int PADDING = 20;
    public static int VSPACING = 10;
    public static int HSPACING = 5;
    public static float MOVE_STEP = 2f;

    private State state;
    private boolean reroll;
    private float rerollY;
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

    public EngagementPanel(BitmapFont font, TextureAtlas uiAtlas, TextureAtlas hudAtlas)
    {
        super(uiAtlas.createPatch("typewriter"));
        usFlag = new Sprite(hudAtlas.findRegion("us-flag"));
        geFlag = new Sprite(hudAtlas.findRegion("ge-flag"));
        attackImg = new Sprite(hudAtlas.findRegion("attack"));
        defenseImg = new Sprite(hudAtlas.findRegion("defense"));
        this.attack = new Label(font);
        this.defense = new Label(font);
        this.attackR = new Label(font);
        this.defenseR = new Label(font);
        this.okBtn = new Bg(uiAtlas.findRegion("ok"));
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

    public void show(Engagement e, Position position, float volume)
    {
        DiceAnimation.initSound(volume);
        attack.write(String.format(" + %d + %d =", e.unitCount, e.flankBonus));
        if (e.weatherDefense == 0)
            defense.write(String.format("%d + %d =", e.unitDefense, e.terrainDefense));
        else
            defense.write(String.format("%d + %d + %d =", e.unitDefense, e.terrainDefense, e.weatherDefense));
        attackR.write(String.format(" %2d", e.attackSum));
        defenseR.write(String.format(" %2d", e.defenseSum));
        if (e.success)
            winner = ((e.attackerArmy == Army.US) ? usFlag : geFlag);
        else
            winner = ((e.attackerArmy == Army.US) ? geFlag : usFlag);

        this.position = position;
        placeElements();

        state = State.ROLL1;
        reroll = (e.d3 != 0);

        d1Animation.set(e.d1);
        d2Animation.set(e.d2);
        if (reroll) {
            d3Animation.set(e.d3);
            d4Animation.set(e.d4);
        }

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
        d3Animation.setPosition(x, y);
        x += (d1Animation.getWidth() + HSPACING);
        d2Animation.setPosition(x, (y));
        d4Animation.setPosition(x, y);
        x += (d1Animation.getWidth() + HSPACING);
        y = (y + (attackImg.getHeight() / 2f) - (attack.getHeight() / 2f));
        attack.setPosition(x, y);
        attackR.setPosition(defenseR.getX(), y);

        rerollY = (d1Animation.getY() + d1Animation.getHeight() + VSPACING);
    }

    @Override
    public boolean hit(float x, float y)
    {
        return rect.contains(x, y);
    }

    @Override
    public boolean animate(float delta)
    {
        if (!visible) return true;
        if (state == State.ROLL1) {
            d1Animation.animate(delta);
            d2Animation.animate(delta);
            if (d1Animation.isDone() && d2Animation.isDone()) {
                if (reroll)
                    state = State.MOVE;
                else
                    state = State.RESULT;
            }
        }

        if (state == State.MOVE) {
            float y = (d1Animation.getY() + MOVE_STEP);
            if (y >= rerollY) {
                y = rerollY;
                state = State.ROLL2;
            }
            setPosition(getX(), getY(), getWidth(), (y + d1Animation.getHeight() + VSPACING - getY()));
            d1Animation.setPosition(d1Animation.getX(), y);
            d2Animation.setPosition(d2Animation.getX(), y);
        }

        if (state == State.ROLL2) {
            if (d1Animation.getY() < rerollY) {
                d1Animation.setPosition(d1Animation.getX(), (d1Animation.getY() + d1Animation.getHeight() + VSPACING));
                d2Animation.setPosition(d2Animation.getX(), (d2Animation.getY() + d2Animation.getHeight() + VSPACING));
            } else {
                d3Animation.animate(delta);
                d4Animation.animate(delta);
                if (d3Animation.isDone() && d4Animation.isDone())
                    state = State.RESULT;
            }
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
        if ((state == State.ROLL2) || (reroll && (state == State.RESULT))) {
            d3Animation.draw(batch);
            d4Animation.draw(batch);
        }
        attack.draw(batch);
        defenseImg.draw(batch);
        defense.draw(batch);
        defenseR.draw(batch);
        okBtn.draw(batch);
        if (state == State.RESULT) {
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
