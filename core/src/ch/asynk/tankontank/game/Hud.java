package ch.asynk.tankontank.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Image;

import ch.asynk.tankontank.TankOnTank;

class ActionBtn implements Disposable
{
    boolean enabled;
    private Image on;
    private Image off;

    public ActionBtn(TextureAtlas atlas, String off, String on)
    {
        this.enabled = false;
        this.on = new Image(atlas.findRegion(on));
        this.off = new Image(atlas.findRegion(off));
    }

    public void toggle()
    {
        enabled = !enabled;
    }

    public void setOn()
    {
        enabled = true;
    }

    public void setOff()
    {
        enabled = false;
    }

    public Image getImage()
    {
        return (enabled ? on : off);
    }

    public void setPosition(float x, float y)
    {
        on.setPosition(x, y);
        off.setPosition(x, y);
    }

    public boolean hit(float x, float y)
    {
        return ((x > on.getX()) && (x < on.getX() + on.getWidth()) && (y > on.getY()) && (y < on.getY() + on.getHeight()));
    }

    @Override
    public void dispose()
    {
        on.dispose();
        off.dispose();
    }

    public float getX() { return on.getX(); }
    public float getY() { return on.getY(); }
    public float getWidth() { return on.getWidth(); }
    public float getHeight() { return on.getHeight(); }
}

public class Hud implements Disposable
{
    private final TankOnTank game;
    private final GameCtrl ctrl;

    private ActionBtn flagAct;
    private ActionBtn moveAct;
    private ActionBtn rotateAct;
    private ActionBtn attackAct;
    private ActionBtn cancelAct;

    private Rectangle rect;
    private float elapsed;

    public Hud(final GameCtrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;

        TextureAtlas atlas = game.manager.get("data/assets.atlas", TextureAtlas.class);

        flagAct = new ActionBtn(atlas, "us-flag", "ge-flag");
        moveAct = new ActionBtn(atlas, "act-move", "act-move-on");
        rotateAct = new ActionBtn(atlas, "act-rotate", "act-rotate-on");
        attackAct = new ActionBtn(atlas, "act-attack", "act-attack-on");
        cancelAct = new ActionBtn(atlas, "act-cancel", "act-cancel-on");

        flagAct.setPosition(5, (Gdx.graphics.getHeight() - flagAct.getHeight() - 5));
        moveAct.setPosition(flagAct.getX(), ( flagAct.getY() - moveAct.getHeight() - 5));
        rotateAct.setPosition(flagAct.getX(), ( moveAct.getY() - rotateAct.getHeight() - 5));
        attackAct.setPosition(flagAct.getX(), ( rotateAct.getY() - attackAct.getHeight() - 5));
        cancelAct.setPosition(flagAct.getX(), ( attackAct.getY() - cancelAct.getHeight() - 5));

        rect = new Rectangle(cancelAct.getX(), cancelAct.getY(), flagAct.getWidth(),
                (flagAct.getY() + flagAct.getHeight() - cancelAct.getY()));

        elapsed = 0f;
    }

    @Override
    public void dispose()
    {
        flagAct.dispose();
        moveAct.dispose();
        rotateAct.dispose();
        attackAct.dispose();
        cancelAct.dispose();
    }

    public void animate(float delta)
    {
        elapsed += delta;
        if (elapsed > 5f) {
            elapsed = 0f;
            flagAct.toggle();
        }
    }

    public void draw(Batch batch)
    {
        flagAct.getImage().draw(batch);
        moveAct.getImage().draw(batch);
        rotateAct.getImage().draw(batch);
        attackAct.getImage().draw(batch);
        cancelAct.getImage().draw(batch);
    }

    public void reset()
    {
        moveAct.setOff();
        rotateAct.setOff();
        attackAct.setOff();
        cancelAct.setOff();
    }

    public boolean touchDown(float x, float y)
    {
        if (!rect.contains(x,y)) return false;

        if (cancelAct.hit(x, y)) {
            ctrl.abort();
            cancelAct.toggle();
        }

        return true;
    }

    public boolean touchUp(float x, float y)
    {
        if (!rect.contains(x,y)) return false;

        if (moveAct.hit(x, y)) {
            moveAct.setOn();
            ctrl.setState(GameState.State.MOVE);
        } else if (rotateAct.hit(x, y)) {
            rotateAct.setOn();
            ctrl.setState(GameState.State.ROTATE);
        } else if (attackAct.hit(x, y)) {
            // TODO
        } else if (cancelAct.hit(x, y)) {
            reset();
            ctrl.abort();
        }

        return true;
    }
}
