package ch.asynk.tankontank.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.game.hud.Bg;
import ch.asynk.tankontank.game.hud.Button;
import ch.asynk.tankontank.game.hud.Msg;

import ch.asynk.tankontank.TankOnTank;

public class Hud implements Disposable
{
    private static final float OFFSET = 15f;

    private final TankOnTank game;
    private final Ctrl ctrl;

    private Image flag;

    private Bg bg;
    public Button moveBtn;
    public Button rotateBtn;
    public Button promoteBtn;
    public Button attackBtn;
    public Button checkBtn;
    public Button cancelBtn;

    private Button btn;
    private Msg msg;

    private Rectangle infoRect;
    private Vector2 bottomLeft;

    public Hud(final Ctrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;
        this.bottomLeft = new Vector2((Gdx.graphics.getWidth() - OFFSET), OFFSET);

        TextureAtlas atlas = game.manager.get("data/assets.atlas", TextureAtlas.class);

        moveBtn = new Button(atlas, "btn-move");
        rotateBtn = new Button(atlas, "btn-rotate");
        promoteBtn = new Button(atlas, "btn-promote");
        attackBtn = new Button(atlas, "btn-attack");
        checkBtn = new Button(atlas, "btn-check");
        cancelBtn = new Button(atlas, "btn-cancel");

        bg = new Bg(atlas.findRegion("disabled"));
        msg = new Msg(game.skin.getFont("default-font"), atlas.findRegion("disabled"));

        updatePlayer();

        flag.setPosition(OFFSET, (Gdx.graphics.getHeight() - flag.getHeight() - OFFSET));
        // TODO add counters for
        //  - Action Points
        //  - Turn

        infoRect = new Rectangle(flag.getX(), flag.getY(), flag.getWidth(), flag.getHeight());
        msg.write("YO! It's hello_world Bitch !", 200, 200, 2, 10);
    }

    @Override
    public void dispose()
    {
        moveBtn.dispose();
        rotateBtn.dispose();
        promoteBtn.dispose();
        attackBtn.dispose();
        checkBtn.dispose();
        cancelBtn.dispose();
        bg.dispose();
        msg.dispose();
    }

    public void animate(float delta)
    {
        msg.animate(delta);
    }

    public void draw(Batch batch)
    {
        flag.draw(batch);
        bg.draw(batch);
        if (moveBtn.visible) moveBtn.getImage().draw(batch);
        if (rotateBtn.visible) rotateBtn.getImage().draw(batch);
        if (promoteBtn.visible) promoteBtn.getImage().draw(batch);
        if (attackBtn.visible) attackBtn.getImage().draw(batch);
        if (checkBtn.visible) checkBtn.getImage().draw(batch);
        if (cancelBtn.visible) cancelBtn.getImage().draw(batch);
        msg.draw(batch);
    }

    public void updatePlayer()
    {
        flag = ctrl.currentPlayer().getFlag();
        flag.setPosition(OFFSET, (Gdx.graphics.getHeight() - flag.getHeight() - OFFSET));
    }

    private float setButton(Button btn, float x, float y)
    {
        // btn.setOff();
        btn.visible = true;
        btn.setPosition(x, y);
        return (y + btn.getHeight() + OFFSET);
    }

    public void show(boolean promote, boolean rotate, boolean move, boolean attack, boolean check, boolean cancel)
    {
        float x =  (bottomLeft.x - checkBtn.getWidth());
        float y =  bottomLeft.y;

        if (move)   y = setButton(moveBtn, x, y);
        else moveBtn.hide();
        if (rotate) y = setButton(rotateBtn, x, y);
        else rotateBtn.hide();
        if (attack) y = setButton(attackBtn, x, y);
        else attackBtn.hide();
        if (promote) y = setButton(promoteBtn, x, y);
        else promoteBtn.hide();
        if (cancel) y = setButton(cancelBtn, x, y);
        else cancelBtn.hide();
        if (check)  y = setButton(checkBtn, x, y);
        else checkBtn.hide();

        bg.set(x, bottomLeft.y, checkBtn.getWidth(), (y - bottomLeft.y));
    }

    public void hide()
    {
        moveBtn.hide();
        rotateBtn.hide();
        promoteBtn.hide();
        attackBtn.hide();
        checkBtn.hide();
        cancelBtn.hide();
        bg.set(0, 0, 0, 0);
    }

    public boolean touchDown(float x, float y)
    {
        if (infoRect.contains(x,y)) return true;
        if (!bg.contains(x,y)) return false;

        btn = null;

        if (moveBtn.hit(x, y))
            btn = moveBtn;
        else if (rotateBtn.hit(x, y))
            btn = rotateBtn;
        else if (promoteBtn.hit(x, y))
            btn = promoteBtn;
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

        if (infoRect.contains(x,y)) {
            ctrl.endTurn();
            return true;
        }
        if (!bg.contains(x,y)) return false;

        if (btn == moveBtn)
            ctrl.setState(State.StateType.MOVE);
        else if (btn == rotateBtn)
            ctrl.setState(State.StateType.ROTATE);
        else if (btn == promoteBtn)
            ctrl.setState(State.StateType.PROMOTE);
        else if (btn == attackBtn)
            ctrl.setState(State.StateType.ATTACK);
        else if (btn == checkBtn)
            ctrl.done();
        else if (btn == cancelBtn)
            ctrl.abort();

        btn = null;

        return true;
    }
}
