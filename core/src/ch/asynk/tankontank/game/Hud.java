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

    private Button moveBtn;
    private Button rotateBtn;
    private Button attackBtn;
    private Button cancelBtn;

    private Rectangle rect;
    private float elapsed;

    public Hud(final GameCtrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;

        TextureAtlas atlas = game.manager.get("data/assets.atlas", TextureAtlas.class);

        usFlag = new Image(atlas.findRegion("us-flag"));
        geFlag = new Image(atlas.findRegion("ge-flag"));
        moveBtn = new Button(atlas, "btn-move");
        rotateBtn = new Button(atlas, "btn-rotate");
        attackBtn = new Button(atlas, "btn-attack");
        cancelBtn = new Button(atlas, "btn-cancel");

        flag = usFlag;

        int left = Gdx.graphics.getWidth() - 5;
        usFlag.setPosition((left - flag.getWidth()), (Gdx.graphics.getHeight() - flag.getHeight() - 5));
        geFlag.setPosition(flag.getX(), flag.getY());
        moveBtn.setPosition((left - moveBtn.getWidth()), ( flag.getY() - moveBtn.getHeight() - 5));
        rotateBtn.setPosition((left - rotateBtn.getWidth()), ( moveBtn.getY() - rotateBtn.getHeight() - 5));
        attackBtn.setPosition((left - attackBtn.getWidth()), ( rotateBtn.getY() - attackBtn.getHeight() - 5));
        cancelBtn.setPosition((left - cancelBtn.getWidth()), ( attackBtn.getY() - cancelBtn.getHeight() - 5));
        cancelBtn.disable();

        rect = new Rectangle(cancelBtn.getX(), cancelBtn.getY(), flag.getWidth(),
                (flag.getY() + flag.getHeight() - cancelBtn.getY()));

        elapsed = 0f;
    }

    @Override
    public void dispose()
    {
        usFlag.dispose();
        geFlag.dispose();
        moveBtn.dispose();
        rotateBtn.dispose();
        attackBtn.dispose();
        cancelBtn.dispose();
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
        moveBtn.getImage().draw(batch);
        rotateBtn.getImage().draw(batch);
        attackBtn.getImage().draw(batch);
        cancelBtn.getImage().draw(batch);
    }

    public void reset()
    {
        moveBtn.setOff();
        rotateBtn.setOff();
        attackBtn.setOff();
        cancelBtn.disable();
    }

    public void disableCancel()
    {
        cancelBtn.disable();
    }

    public boolean touchDown(float x, float y)
    {
        if (!rect.contains(x,y)) return false;

        if (cancelBtn.hit(x, y)) {
            cancelBtn.setOn();
        }

        return true;
    }

    public boolean touchUp(float x, float y)
    {
        if (!rect.contains(x,y)) return false;

        if (!ctrl.isInAction()) {
            if (moveBtn.hit(x, y)) {
                switchTo(GameState.State.MOVE);
            } else if (rotateBtn.hit(x, y)) {
                switchTo(GameState.State.ROTATE);
            } else if (attackBtn.hit(x, y)) {
                // switchTo(GameState.State.ATTACK);
            }
        }
        if (cancelBtn.hit(x, y)) {
            reset();
            ctrl.abort();
        }

        return true;
    }

    private void switchTo(GameState.State state)
    {
        switch(state) {
            case MOVE:
                moveBtn.setOn();
                rotateBtn.disable();
                attackBtn.disable();
                break;
            case ROTATE:
                moveBtn.disable();
                rotateBtn.setOn();
                attackBtn.disable();
                break;
        }
        cancelBtn.setOff();

        ctrl.setState(state);
    }
}
