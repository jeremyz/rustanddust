package ch.asynk.tankontank.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Image;

import ch.asynk.tankontank.TankOnTank;

class Button implements Disposable
{

    int idx;
    private Image images [];
    private Image image;

    private static final int OFF = 0;
    private static final int ON = 1;
    private static final int DISABLED = 2;

    public Button(TextureAtlas atlas, String base)
    {
        this.idx = OFF;
        this.images = new Image[3];
        this.images[OFF] = new Image(atlas.findRegion(base + "-off"));
        this.images[ON] = new Image(atlas.findRegion(base + "-on"));
        this.images[DISABLED] = new Image(atlas.findRegion(base + "-disabled"));
    }

    public void setOff()
    {
        idx = OFF;
    }

    public void setOn()
    {
        idx = ON;
    }

    public void disable()
    {
        idx = DISABLED;
    }

    public Image getImage()
    {
        return images[idx];
    }

    public boolean isDisabled()
    {
        return (idx == DISABLED);
    }

    public void setPosition(float x, float y)
    {
        images[OFF].setPosition(x, y);
        images[ON].setPosition(x, y);
        images[DISABLED].setPosition(x, y);
    }

    public boolean hit(float x, float y)
    {
        return ((x > images[0].getX()) && (x < images[0].getX() + images[0].getWidth()) && (y > images[0].getY()) && (y < images[0].getY() + images[0].getHeight()));
    }

    @Override
    public void dispose()
    {
        images[OFF].dispose();
        images[ON].dispose();
        images[DISABLED].dispose();
    }

    public float getX() { return images[0].getX(); }
    public float getY() { return images[0].getY(); }
    public float getWidth() { return images[0].getWidth(); }
    public float getHeight() { return images[0].getHeight(); }
}

public class Hud implements Disposable
{
    private final TankOnTank game;
    private final GameCtrl ctrl;

    private Image usFlag;
    private Image geFlag;
    private Image flag;

    private Button moveAct;
    private Button rotateAct;
    private Button attackAct;
    private Button cancelAct;

    private Rectangle rect;
    private float elapsed;

    public Hud(final GameCtrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;

        TextureAtlas atlas = game.manager.get("data/assets.atlas", TextureAtlas.class);

        usFlag = new Image(atlas.findRegion("us-flag"));
        geFlag = new Image(atlas.findRegion("ge-flag"));
        moveAct = new Button(atlas, "btn-move");
        rotateAct = new Button(atlas, "btn-rotate");
        attackAct = new Button(atlas, "btn-attack");
        cancelAct = new Button(atlas, "btn-cancel");

        flag = usFlag;

        int left = Gdx.graphics.getWidth() - 5;
        usFlag.setPosition((left - flag.getWidth()), (Gdx.graphics.getHeight() - flag.getHeight() - 5));
        geFlag.setPosition(flag.getX(), flag.getY());
        moveAct.setPosition((left - moveAct.getWidth()), ( flag.getY() - moveAct.getHeight() - 5));
        rotateAct.setPosition((left - rotateAct.getWidth()), ( moveAct.getY() - rotateAct.getHeight() - 5));
        attackAct.setPosition((left - attackAct.getWidth()), ( rotateAct.getY() - attackAct.getHeight() - 5));
        cancelAct.setPosition((left - cancelAct.getWidth()), ( attackAct.getY() - cancelAct.getHeight() - 5));
        cancelAct.disable();

        rect = new Rectangle(cancelAct.getX(), cancelAct.getY(), flag.getWidth(),
                (flag.getY() + flag.getHeight() - cancelAct.getY()));

        elapsed = 0f;
    }

    @Override
    public void dispose()
    {
        usFlag.dispose();
        geFlag.dispose();
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
            flag = ((flag == usFlag) ? geFlag : usFlag);
        }
    }

    public void draw(Batch batch)
    {
        flag.draw(batch);
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
        cancelAct.disable();
    }

    public void disableCancel()
    {
        cancelAct.disable();
    }

    public boolean touchDown(float x, float y)
    {
        if (!rect.contains(x,y)) return false;

        if (cancelAct.hit(x, y)) {
            cancelAct.setOn();
        }

        return true;
    }

    public boolean touchUp(float x, float y)
    {
        if (!rect.contains(x,y)) return false;

        if (!ctrl.isInAction()) {
            if (moveAct.hit(x, y)) {
                switchTo(GameState.State.MOVE);
            } else if (rotateAct.hit(x, y)) {
                switchTo(GameState.State.ROTATE);
            } else if (attackAct.hit(x, y)) {
                // switchTo(GameState.State.ATTACK);
            }
        }
        if (cancelAct.hit(x, y)) {
            reset();
            ctrl.abort();
        }

        return true;
    }

    private void switchTo(GameState.State state)
    {
        switch(state) {
            case MOVE:
                moveAct.setOn();
                rotateAct.disable();
                attackAct.disable();
                break;
            case ROTATE:
                moveAct.disable();
                rotateAct.setOn();
                attackAct.disable();
                break;
        }
        cancelAct.setOff();

        ctrl.setState(state);
    }
}
