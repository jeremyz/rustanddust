package ch.asynk.tankontank.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.game.State.StateType;
import ch.asynk.tankontank.game.hud.Msg;
import ch.asynk.tankontank.game.hud.Bg;
import ch.asynk.tankontank.game.hud.Button;
import ch.asynk.tankontank.game.hud.LabelImage;
import ch.asynk.tankontank.game.hud.UnitDock;
import ch.asynk.tankontank.game.hud.OkCancel;
import ch.asynk.tankontank.game.hud.Position;

import ch.asynk.tankontank.TankOnTank;

public class Hud implements Disposable
{
    public static final float OFFSET = 10f;
    public static final float PADDING = 5f;

    private final TankOnTank game;
    private final Ctrl ctrl;

    private Bg actionsBg;
    private Msg msg;

    private Button btn;
    private Object hit;
    public Button moveBtn;
    public Button rotateBtn;
    public Button promoteBtn;
    public Button attackBtn;
    public Button checkBtn;
    public Button cancelBtn;

    private Image flag;
    private Image usFlag;
    private Image geFlag;
    private LabelImage turns;
    private LabelImage aps;
    private LabelImage reinforcement;
    private UnitDock unitDock;

    private BitmapFont font;

    private OkCancel okCancel;
    private OkCancelAction okCancelAction;

    private Vector2 corner;

    enum OkCancelAction
    {
        END_TURN,
    }

    public Hud(final Ctrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;
        this.corner = new Vector2((Gdx.graphics.getWidth() - OFFSET), OFFSET);

        font = game.skin.getFont("default-font");
        TextureAtlas atlas = game.factory.hudAtlas;

        moveBtn = new Button(atlas, "btn-move");
        rotateBtn = new Button(atlas, "btn-rotate");
        promoteBtn = new Button(atlas, "btn-promote");
        attackBtn = new Button(atlas, "btn-attack");
        checkBtn = new Button(atlas, "btn-check");
        cancelBtn = new Button(atlas, "btn-cancel");
        actionsBg = new Bg(atlas.findRegion("disabled"));

        usFlag = new Image(atlas.findRegion("us-flag"));
        geFlag = new Image(atlas.findRegion("ge-flag"));
        turns = new LabelImage(atlas.findRegion("turns"), font, 5f);
        aps = new LabelImage(atlas.findRegion("aps"), font, 5f);
        reinforcement = new LabelImage(atlas.findRegion("reinforcement"), font, 5f);
        unitDock = new UnitDock(ctrl, atlas.findRegion("disabled"), atlas.findRegion("reinforcement-selected"), 10f);

        msg = new Msg(font, atlas.findRegion("disabled"), 10f);
        okCancel = new OkCancel(font, atlas.findRegion("disabled"), atlas, 10f);

        float x = OFFSET;
        float y = (Gdx.graphics.getHeight() - OFFSET);
        usFlag.setPosition(x, (y - usFlag.getHeight()));
        geFlag.setPosition(x, (y - geFlag.getHeight()));
        turns.setPosition((usFlag.getX() + usFlag.getWidth() + 10), usFlag.getY());
        aps.setPosition((turns.getX() + turns.getWidth() + 10), turns.getY());
        aps.setLabelPosition(Position.TOP_RIGHT);
        reinforcement.setPosition(x, usFlag.getY() - reinforcement.getHeight() - 0);
        reinforcement.setLabelPosition(Position.TOP_LEFT);
        unitDock.setTopLeft(OFFSET, reinforcement.getY() - 5);

        hide();
    }

    @Override
    public void dispose()
    {
        font.dispose();

        moveBtn.dispose();
        rotateBtn.dispose();
        promoteBtn.dispose();
        attackBtn.dispose();
        checkBtn.dispose();
        cancelBtn.dispose();
        actionsBg.dispose();

        usFlag.dispose();
        geFlag.dispose();
        turns.dispose();
        aps.dispose();
        reinforcement.dispose();
        unitDock.dispose();

        msg.dispose();
        okCancel.dispose();
    }

    public void changeState(StateType from, StateType to)
    {
        if (to != StateType.ENTRY);
            unitDock.hide();

        if ((to == StateType.SELECT) || (to == StateType.ENTRY))
            reinforcement.blocked = false;
        else
            reinforcement.blocked = true;
    }

    public void update()
    {
        unitDock.hide();
        turns.write("" + ctrl.player.getTurn());
        aps.write("" + ctrl.player.getAp());
        int r = ctrl.player.getReinforcement().size();
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
        okCancel.draw(batch);
    }

    public void drawDebug(ShapeRenderer debugShapes)
    {
        flag.drawDebug(debugShapes);
        turns.drawDebug(debugShapes);
        aps.drawDebug(debugShapes);
        reinforcement.drawDebug(debugShapes);
        unitDock.drawDebug(debugShapes);

        actionsBg.drawDebug(debugShapes);
        moveBtn.drawDebug(debugShapes);
        rotateBtn.drawDebug(debugShapes);
        promoteBtn.drawDebug(debugShapes);
        attackBtn.drawDebug(debugShapes);
        checkBtn.drawDebug(debugShapes);
        cancelBtn.drawDebug(debugShapes);

        msg.drawDebug(debugShapes);
        okCancel.drawDebug(debugShapes);
    }

    public Unit getDockUnit()
    {
        return (Unit) unitDock.selectedPawn;
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
        btn.setUp();
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

        actionsBg.set((x - PADDING), (corner.y - PADDING), (checkBtn.getWidth() + (2 * PADDING)), (y - corner.y + PADDING));
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

    public void hideUnitDock()
    {
        unitDock.hide();
    }

    public boolean touchDown(float x, float y)
    {
        btn = null;
        hit = null;

        if (turns.hit(x,y))
            hit = turns;
        else if (unitDock.hit(x, y))
            hit = unitDock;
        else if (reinforcement.hit(x, y))
            hit = reinforcement;
        else if (okCancel.hit(x, y))
            hit = okCancel;
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
            if (actionsBg.hit(x, y)) {
                if ((btn == moveBtn) && moveBtn.hit(x, y))
                    ctrl.setState(StateType.MOVE);
                else if ((btn == rotateBtn) && rotateBtn.hit(x, y))
                    ctrl.setState(StateType.ROTATE);
                else if ((btn == promoteBtn) && promoteBtn.hit(x, y))
                    ctrl.setState(StateType.PROMOTE);
                else if ((btn == attackBtn) && attackBtn.hit(x, y))
                    ctrl.setState(StateType.ATTACK);
                else if ((btn == checkBtn) && checkBtn.hit(x, y))
                    ctrl.done();
                else if ((btn == cancelBtn) && cancelBtn.hit(x, y)) {
                    notify("Action canceled");
                    ctrl.abort();
                } else
                    btn.setUp();
            } else
                btn.setUp();
            btn = null;
        } else if (hit != null) {
            if ((hit == turns) && turns.hit(x, y))
                askEndTurn();
            else if ((hit == reinforcement) && reinforcement.hit(x, y))
                unitDock.toggle();
            else if ((hit == unitDock) && unitDock.hit(x, y))
                ctrl.setState(StateType.ENTRY);
            else if ((hit == okCancel) && okCancel.hit(x, y))
                closeOkCancel();
            hit = null;
        } else
            return false;

        return true;
    }

    private void closeOkCancel()
    {
        ctrl.blockMap = false;
        okCancel.visible = false;
        if (okCancel.ok) {
            switch(okCancelAction)
            {
                case END_TURN:
                    ctrl.endPlayerTurn();
                    break;
            }
        }
    }

    private void askEndTurn()
    {
        ctrl.blockMap = true;
        okCancelAction = OkCancelAction.END_TURN;
        okCancel.show("You still have Action Points left.\nEnd your Turn anyway ?", Position.MIDDLE_CENTER);
    }
}
