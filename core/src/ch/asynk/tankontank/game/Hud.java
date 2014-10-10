package ch.asynk.tankontank.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Image;

import ch.asynk.tankontank.TankOnTank;

class Button implements Disposable
{

    public int idx;
    public boolean visible;
    private Image images [];
    private Image image;
    private Rectangle rect;

    private static final int OFF = 0;
    private static final int ON = 1;
    private static final int DOWN = 2;

    public Button(TextureAtlas atlas, String base)
    {
        this.idx = OFF;
        this.visible = false;
        this.images = new Image[3];
        this.images[OFF] = new Image(atlas.findRegion(base + "-off"));
        this.images[ON] = new Image(atlas.findRegion(base + "-on"));
        this.images[DOWN] = new Image(atlas.findRegion(base + "-down"));

        this.rect = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void dispose()
    {
        for (Image image : images)
            image.dispose();
    }

    public void hide()
    {
        idx = OFF;
        visible = false;
    }

    public void setOff()
    {
        idx = OFF;
    }

    public void setOn()
    {
        idx = ON;
    }

    public void setDown()
    {
        idx = DOWN;
    }

    public boolean isOn()
    {
        return (idx == ON);
    }

    public boolean isOff()
    {
        return (idx == OFF);
    }

    public boolean isDisabled()
    {
        return (idx == DOWN);
    }

    public Image getImage()
    {
        return images[idx];
    }

    public void setPosition(float x, float y)
    {
        for (Image image : images)
            image.setPosition(x, y);
        rect.set(x, y, getWidth(), getHeight());
    }

    public boolean hit(float x, float y)
    {
        if (!visible) return false;
        return rect.contains(x,y);
    }

    public float getX() { return images[0].getX(); }
    public float getY() { return images[0].getY(); }
    public float getWidth() { return images[0].getWidth(); }
    public float getHeight() { return images[0].getHeight(); }
}

public class Hud implements Disposable
{
    private static final float OFFSET = 15f;

    private final TankOnTank game;
    private final GameCtrl ctrl;

    private Image usFlag;
    private Image geFlag;
    private Image flag;

    private Button moveBtn;
    private Button rotateBtn;
    private Button attackBtn;
    private Button checkBtn;
    private Button cancelBtn;

    private Button btn;

    private Rectangle infoRect;
    private Rectangle buttonsRect;
    private float elapsed;
    private Vector2 bottomLeft;

    public Hud(final GameCtrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;
        this.bottomLeft = new Vector2((Gdx.graphics.getWidth() - OFFSET), OFFSET);

        TextureAtlas atlas = game.manager.get("data/assets.atlas", TextureAtlas.class);

        usFlag = new Image(atlas.findRegion("us-flag"));
        geFlag = new Image(atlas.findRegion("ge-flag"));
        moveBtn = new Button(atlas, "btn-move");
        rotateBtn = new Button(atlas, "btn-rotate");
        attackBtn = new Button(atlas, "btn-attack");
        checkBtn = new Button(atlas, "btn-check");
        cancelBtn = new Button(atlas, "btn-cancel");

        flag = usFlag;

        usFlag.setPosition(OFFSET, (Gdx.graphics.getHeight() - flag.getHeight() - OFFSET));
        geFlag.setPosition(flag.getX(), flag.getY());
        // TODO add counters for
        //  - Action Points
        //  - Turn

        infoRect = new Rectangle(flag.getX(), flag.getY(), flag.getWidth(), flag.getHeight());
        buttonsRect = new Rectangle(0, 0, 0, 0);

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
        checkBtn.dispose();
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
        if (moveBtn.visible) moveBtn.getImage().draw(batch);
        if (rotateBtn.visible) rotateBtn.getImage().draw(batch);
        if (attackBtn.visible) attackBtn.getImage().draw(batch);
        if (checkBtn.visible) checkBtn.getImage().draw(batch);
        if (cancelBtn.visible) cancelBtn.getImage().draw(batch);
    }

    private float setButton(Button btn, float x, float y)
    {
        // btn.setOff();
        btn.visible = true;
        btn.setPosition(x, y);
        return (y + btn.getHeight() + OFFSET);
    }

    public void show(boolean rotate, boolean move, boolean attack, boolean check, boolean cancel)
    {
        float x =  (bottomLeft.x - checkBtn.getWidth());
        float y =  bottomLeft.y;

        if (move)   y = setButton(moveBtn, x, y);
        else moveBtn.hide();
        if (rotate) y = setButton(rotateBtn, x, y);
        else rotateBtn.hide();
        if (attack) y = setButton(attackBtn, x, y);
        else attackBtn.hide();
        if (cancel) y = setButton(cancelBtn, x, y);
        else cancelBtn.hide();
        if (check)  y = setButton(checkBtn, x, y);
        else checkBtn.hide();

        buttonsRect.set(x, bottomLeft.y, checkBtn.getWidth(), (y - bottomLeft.y));
    }

    public void hide()
    {
        moveBtn.hide();
        rotateBtn.hide();
        attackBtn.hide();
        checkBtn.hide();
        cancelBtn.hide();
    }

    public void setRotateOff()
    {
        rotateBtn.setOff();
    }

    public boolean touchDown(float x, float y)
    {
        if (infoRect.contains(x,y)) return true;
        if (!buttonsRect.contains(x,y)) return false;

        btn = null;

        if (moveBtn.hit(x, y))
            btn = moveBtn;
        else if (rotateBtn.hit(x, y))
            btn = rotateBtn;
        else if (attackBtn.hit(x, y))
            btn = attackBtn;
        else if (checkBtn.hit(x, y))
            btn = checkBtn;
        else if (cancelBtn.hit(x, y))
            btn = cancelBtn;

        if (btn != null)
            btn.setDown();

        return true;
    }

    public boolean touchUp(float x, float y)
    {
        if (btn != null)
            btn.setOn();

        if (infoRect.contains(x,y)) return true;
        if (!buttonsRect.contains(x,y)) return false;

        if (btn == moveBtn)
            ctrl.setState(GameState.State.MOVE);
        else if (btn == rotateBtn)
            ctrl.setState(GameState.State.ROTATE);
        else if (btn == attackBtn)
            // TODO ctrl.setState(GameState.State.ATTACK);
            System.out.println(" ATTACK not implemented yet");
        else if (btn == checkBtn)
            ctrl.done();
        else if (btn == cancelBtn)
            ctrl.abort();

        btn = null;

        return true;
    }
}
