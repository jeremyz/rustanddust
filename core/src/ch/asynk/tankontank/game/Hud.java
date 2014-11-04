package ch.asynk.tankontank.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.game.hud.Msg;
import ch.asynk.tankontank.game.hud.Bg;
import ch.asynk.tankontank.game.hud.Button;
import ch.asynk.tankontank.game.hud.TextImage;
import ch.asynk.tankontank.game.hud.UnitDock;
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
    private TextImage turns;
    private TextImage aps;
    private TextImage reinforcement;
    private UnitDock unitDock;


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
        turns = new TextImage(atlas.findRegion("turns"), game.skin.getFont("default-font"), "0");
        aps = new TextImage(atlas.findRegion("aps"), game.skin.getFont("default-font"), "0");
        reinforcement = new TextImage(atlas.findRegion("reinforcement"), game.skin.getFont("default-font"), "0");
        unitDock = new UnitDock(ctrl, atlas.findRegion("disabled"));

        float x = OFFSET;
        float y = (Gdx.graphics.getHeight() - OFFSET);
        usFlag.setPosition(x, (y - usFlag.getHeight()));
        geFlag.setPosition(x, (y - geFlag.getHeight()));
        turns.setPosition((usFlag.getX() + usFlag.getWidth() + 10), usFlag.getY());
        aps.setPosition((turns.getX() + turns.getWidth() + 10), turns.getY());
        aps.setTextPosition((aps.getX() + aps.getWidth() - 15), (aps.getY() + aps.getHeight() - 20));
        reinforcement.setPosition(x, usFlag.getY() - reinforcement.getHeight() - 0);
        reinforcement.setTextPosition((reinforcement.getX() + 5), (reinforcement.getY() + reinforcement.getHeight() - 20));
        unitDock.setTopLeft(OFFSET, reinforcement.getY() - 5);
    }

    public void update()
    {
        turns.write("" + ctrl.player.getTurn());
        aps.write("" + ctrl.player.getAp());
        int r = ctrl.player.reinforcement.size();
        if (r == 0) {
            reinforcement.visible = false;
        } else {
            reinforcement.visible = true;
            reinforcement.write("" + r);
        }

        if (ctrl.player.getFaction() == Army.GE)
            flag = geFlag;
        else
            flag = usFlag;
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

        turns.dispose();
        aps.dispose();
        usFlag.dispose();
        geFlag.dispose();
        reinforcement.dispose();
    }

    public void animate(float delta)
    {
        msg.animate(delta);
        unitDock.animate(delta);
    }

    public void draw(Batch batch)
    {
        flag.draw(batch);
        turns.draw(batch);
        aps.draw(batch);
        reinforcement.draw(batch);
        unitDock.draw(batch);

        actionsBg.draw(batch);
        moveBtn.draw(batch);
        rotateBtn.draw(batch);
        promoteBtn.draw(batch);
        attackBtn.draw(batch);
        checkBtn.draw(batch);
        cancelBtn.draw(batch);
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
        btn = null;

        if (turns.hit(x,y))
            return true;
        else if (unitDock.hit(x, y))
            return true;
        else if (reinforcement.hit(x, y))
            return true;
        else if (actionsBg.hit(x,y)) {
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
        } else
            return false;

        if (btn != null)
            btn.setDown();

        return true;
    }

    public boolean touchUp(float x, float y)
    {
        if (btn != null) {
            boolean setOn = true;
            if (actionsBg.hit(x, y)) {
                if ((btn == moveBtn) && moveBtn.hit(x, y))
                    ctrl.setState(State.StateType.MOVE);
                else if ((btn == rotateBtn) && rotateBtn.hit(x, y))
                    ctrl.setState(State.StateType.ROTATE);
                else if ((btn == promoteBtn) && promoteBtn.hit(x, y))
                    ctrl.setState(State.StateType.PROMOTE);
                else if ((btn == attackBtn) && attackBtn.hit(x, y))
                    ctrl.setState(State.StateType.ATTACK);
                else if ((btn == checkBtn) && checkBtn.hit(x, y))
                    ctrl.done();
                else if ((btn == cancelBtn) && cancelBtn.hit(x, y)) {
                    notify("Action canceled");
                    ctrl.abort();
                } else
                    setOn = false;
            } else
                setOn = false;
            if (setOn) btn.setOn();
            else btn.setOff();
            btn = null;
        }
        else if (turns.hit(x, y)) {
            ctrl.endPlayerTurn();
        }
        else if (reinforcement.hit(x, y)) {
            unitDock.toggle();
        }
        else if (unitDock.hit(x, y)) {
            System.err.println("TODO unitDock touched");
        } else
            return false;

        return true;
    }
}
