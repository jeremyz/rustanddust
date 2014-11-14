package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.State.StateType;

public class ActionButtons extends Bg
{
    private final Ctrl ctrl;

    public enum Buttons {
        NONE(-1, 0),
        MOVE(0, 1),
        ROTATE( 1, 2),
        PROMOTE(2, 4),
        ENGAGE(3, 8),
        DONE(4, 16),
        ABORT(5, 32),
        LAST(6, 0);

        Buttons(int i, int b)
        {
            this.i = i;
            this.b = b;
        }

        public int i;
        public int b;
    }

    public float padding;
    private int idx;
    private Button buttons [];
    private StateType states [];
    private Position position;

    public ActionButtons(Ctrl ctrl, TextureRegion region, TextureAtlas atlas, float padding)
    {
        super(region);
        this.ctrl = ctrl;
        this.padding = padding;
        this.visible = false;
        this.position = Position.BOTTOM_RIGHT;
        this.idx = Buttons.NONE.i;

        this.buttons = new Button[Buttons.LAST.i];
        this.buttons[Buttons.MOVE.i] = new Button(atlas, "btn-move");
        this.buttons[Buttons.ROTATE.i] = new Button(atlas, "btn-rotate");
        this.buttons[Buttons.PROMOTE.i] = new Button(atlas, "btn-promote");
        this.buttons[Buttons.ENGAGE.i] = new Button(atlas, "btn-engage");
        this.buttons[Buttons.DONE.i] = new Button(atlas, "btn-check");
        this.buttons[Buttons.ABORT.i] = new Button(atlas, "btn-cancel");

        this.states = new StateType[Buttons.LAST.i];
        this.states[Buttons.MOVE.i] = StateType.MOVE;
        this.states[Buttons.ROTATE.i] = StateType.ROTATE;
        this.states[Buttons.PROMOTE.i] = StateType.PROMOTE;
        this.states[Buttons.ENGAGE.i] = StateType.ENGAGE;
        this.states[Buttons.DONE.i] = StateType.DONE;
        this.states[Buttons.ABORT.i] = StateType.ABORT;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        for (int i = 0; i < Buttons.LAST.i; i++)
            buttons[i].dispose();
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }

    public void setUp(Buttons b)
    {
        buttons[b.i].setUp();
    }

    public void setDown(Buttons b)
    {
        buttons[b.i].setDown();
    }

    public void setOn(Buttons b)
    {
        buttons[b.i].setOn();
    }

    public void hide()
    {
        for (int i = 0; i < Buttons.LAST.i; i++)
            buttons[i].hide();
        this.visible = false;
    }

    private float setButton(Button btn, float x, float y)
    {
        btn.visible = true;
        btn.setPosition(x, y);
        return (y + btn.getHeight() + padding);
    }

    public void show(int bits)
    {
        int b = bits;
        int count = 0;
        while (b > 0) {
            if ((b & 0x01) == 1)
                count += 1;
            b /= 2;
        }

        if (count == 0) {
            this.visible = false;
            return;
        }

        rect.width = (buttons[0].getWidth() + (2 * padding));
        rect.height = ((buttons[0].getHeight() * count) + ((count + 1) * padding));
        rect.x =  position.getX(rect.width);
        rect.y =  position.getY(rect.height);

        float x = (rect.x + padding);
        float y = (rect.y + padding);

        b = 1;
        for (int i = 0; i < Buttons.LAST.i; i++) {
            if ((bits & b) == b)
                y = setButton(buttons[i], x, y);
            else
                buttons[i].hide();
            b *= 2;
        }

        this.visible = true;
    }

    public boolean touchDown(float x, float y)
    {
        idx = Buttons.NONE.i;

        if (!super.hit(x,y))
            return false;

        for (int i = 0; i < Buttons.LAST.i; i++) {
            if (buttons[i].hit(x, y)) {
                idx = i;
                break;
            }
        }

        if (idx != Buttons.NONE.i)
            buttons[idx].setDown();

        return (idx != Buttons.NONE.i);
    }

    public boolean touchUp(float x, float y)
    {
        if (idx == Buttons.NONE.i)
            return false;

        boolean ret = false;

        if (super.hit(x,y) && buttons[idx].hit(x, y)) {
            ctrl.setState(states[idx]);
            ret = true;
        } else
            buttons[idx].setUp();

        idx = Buttons.NONE.i;

        return ret;
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        for (int i = 0; i < Buttons.LAST.i; i++)
            buttons[i].draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        for (int i = 0; i < Buttons.LAST.i; i++)
            buttons[i].drawDebug(shapes);
    }
}
