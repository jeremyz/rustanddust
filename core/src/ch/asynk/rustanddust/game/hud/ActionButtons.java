package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.State.StateType;
import ch.asynk.rustanddust.ui.Widget;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Position;

public class ActionButtons extends Widget
{
    public static int PADDING = 5;

    private final Ctrl ctrl;

    public enum Buttons {
        NONE(-1, 0),
        PROMOTE(0, 1),
        DONE(1, 2),
        ABORT(2, 4),
        LAST(3, 0);

        Buttons(int i, int b)
        {
            this.i = i;
            this.b = b;
        }

        public int i;
        public int b;
    }

    private Sprite bg;
    private int idx;
    private Bg buttons [];
    private StateType states [];

    public ActionButtons(RustAndDust game)
    {
        this.bg = new Sprite(game.factory.getHudRegion(game.factory.DISABLED));
        this.ctrl = game.ctrl;
        this.visible = false;
        this.position = Position.BOTTOM_RIGHT;
        this.idx = Buttons.NONE.i;


        this.buttons = new Bg[Buttons.LAST.i];
        this.buttons[Buttons.DONE.i] = new Bg(game.factory.getHudRegion(game.factory.ACT_DONE));
        this.buttons[Buttons.ABORT.i] = new Bg(game.factory.getHudRegion(game.factory.ACT_ABORT));
        this.buttons[Buttons.PROMOTE.i] = new Bg(game.factory.getHudRegion(game.factory.ACT_PROMOTE));

        this.states = new StateType[Buttons.LAST.i];
        this.states[Buttons.DONE.i] = StateType.DONE;
        this.states[Buttons.ABORT.i] = StateType.ABORT;
        this.states[Buttons.PROMOTE.i] = StateType.PROMOTE;
    }

    @Override
    public void dispose()
    {
        for (int i = 0; i < Buttons.LAST.i; i++)
            buttons[i].dispose();
    }

    public void update(Position position)
    {
        setPosition(position);
        updatePosition();
    }

    public void updatePosition()
    {
        if (!visible) return;
        float dx = (position.getX(rect.width) - rect.x);
        float dy = (position.getY(rect.height) - rect.y);
        translate(dx, dy);
        for (int i = 0; i < Buttons.LAST.i; i++)
            buttons[i].translate(dx, dy);
    }

    public void hide()
    {
        for (int i = 0; i < Buttons.LAST.i; i++)
            buttons[i].visible = false;
        this.visible = false;
    }

    private float setButton(Bg btn, float x, float y)
    {
        btn.visible = true;
        btn.setPosition(x, y);
        return (y + btn.getHeight() + PADDING);
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

        rect.width = (buttons[0].getWidth() + (2 * PADDING));
        rect.height = ((buttons[0].getHeight() * count) + ((count + 1) * PADDING));
        rect.x =  position.getX(rect.width);
        rect.y =  position.getY(rect.height);

        float x = (rect.x + PADDING);
        float y = (rect.y + PADDING);

        b = 1;
        for (int i = 0; i < Buttons.LAST.i; i++) {
            if ((bits & b) == b)
                y = setButton(buttons[i], x, y);
            else
                buttons[i].visible = false;
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
        }

        idx = Buttons.NONE.i;

        return ret;
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        batch.draw(bg, rect.x, rect.y, rect.width, rect.height);
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
