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
import ch.asynk.tankontank.game.hud.Position;

import ch.asynk.tankontank.TankOnTank;

public class Hud implements Disposable
{
    private static final float OFFSET =10f;
    private static final float PADDING = 5f;

    private final TankOnTank game;
    private final Ctrl ctrl;

    private Bg actionsBg;
    private Msg msg;

    private Button btn;
    public Button moveBtn;
    public Button rotateBtn;
    public Button promoteBtn;
    public Button attackBtn;
    public Button checkBtn;
    public Button cancelBtn;

    private Image flag;
    private Image usFlag;
    private Image geFlag;
    private Msg status;
    private Image reinforcement;


    private Vector2 corner;

    public Hud(final Ctrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;
        this.corner = new Vector2((Gdx.graphics.getWidth() - OFFSET), OFFSET);

        TextureAtlas atlas = game.factory.hudAtlas;

        moveBtn = new Button(atlas, "btn-move");
        rotateBtn = new Button(atlas, "btn-rotate");
        promoteBtn = new Button(atlas, "btn-promote");
        attackBtn = new Button(atlas, "btn-attack");
        checkBtn = new Button(atlas, "btn-check");
        cancelBtn = new Button(atlas, "btn-cancel");

        actionsBg = new Bg(atlas.findRegion("disabled"));
        msg = new Msg(game.skin.getFont("default-font"), atlas.findRegion("disabled"));

        usFlag = new Image(atlas.findRegion("us-flag"));
        geFlag = new Image(atlas.findRegion("ge-flag"));
        status = new Msg(game.skin.getFont("default-font"), atlas.findRegion("disabled"));
        reinforcement= new Image(atlas.findRegion("reinforcement"));

        float x = OFFSET;
        float y = (Gdx.graphics.getHeight() - OFFSET);
        usFlag.setPosition(x, (y - usFlag.getHeight()));
        geFlag.setPosition(x, (y - geFlag.getHeight()));
        status.setTopLeft((x + usFlag.getWidth() + 10), y, 10);
        reinforcement.setPosition(x, usFlag.getY() - reinforcement.getHeight() - 0);
    }

    public void update()
    {
        status.write(ctrl.player.getStatus(), 0);
        if (ctrl.player.getFaction() == Army.GE)
            flag = geFlag;
        else
            flag = usFlag;
        // TODO update reinforcement status
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
        actionsBg.dispose();
        msg.dispose();

        usFlag.dispose();
        geFlag.dispose();
        status.dispose();
        reinforcement.dispose();
    }

    public void animate(float delta)
    {
        msg.animate(delta);
    }

    public void draw(Batch batch)
    {
        flag.draw(batch);
        status.draw(batch);
        reinforcement.draw(batch);

        actionsBg.draw(batch);
        if (moveBtn.visible) moveBtn.getImage().draw(batch);
        if (rotateBtn.visible) rotateBtn.getImage().draw(batch);
        if (promoteBtn.visible) promoteBtn.getImage().draw(batch);
        if (attackBtn.visible) attackBtn.getImage().draw(batch);
        if (checkBtn.visible) checkBtn.getImage().draw(batch);
        if (cancelBtn.visible) cancelBtn.getImage().draw(batch);
        msg.draw(batch);
    }

    public void pushNotify(String s)
    {
        notify(s, 1, Position.MIDDLE_CENTER, true);
    }

    public void notify(String s)
    {
        notify(s, 1, Position.MIDDLE_CENTER, false);
    }

    public void notify(String s, float duration, Position position, boolean push)
    {
        if (push) msg.pushWrite(s, duration, position);
        else msg.write(s, 1, position);
    }

    private float setButton(Button btn, float x, float y)
    {
        // btn.setOff();
        btn.visible = true;
        btn.setPosition(x, y);
        return (y + btn.getHeight() + PADDING);
    }

    public void show(boolean promote, boolean rotate, boolean move, boolean attack, boolean check, boolean cancel)
    {
        float x =  (corner.x - checkBtn.getWidth());
        float y =  corner.y;

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

        actionsBg.set((x - PADDING), (corner.y - PADDING), (checkBtn.getWidth() + (2 * PADDING)), (y - corner.y));
    }

    public void hide()
    {
        moveBtn.hide();
        rotateBtn.hide();
        promoteBtn.hide();
        attackBtn.hide();
        checkBtn.hide();
        cancelBtn.hide();
        actionsBg.set(0, 0, 0, 0);
    }

    public boolean touchDown(float x, float y)
    {
        if (flag.contains(x,y)) return true;
        if (reinforcement.contains(x,y)) return true;
        if (!actionsBg.contains(x,y)) return false;

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

        if (flag.contains(x,y)) {
            ctrl.endPlayerTurn();
            return true;
        }

        if (reinforcement.contains(x,y)) {
            System.err.println("TODO reinforcement");
            return true;
        }

        if (!actionsBg.contains(x,y)) return false;

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
        else if (btn == cancelBtn) {
            notify("Action canceled");
            ctrl.abort();
        }

        btn = null;

        return true;
    }
}
