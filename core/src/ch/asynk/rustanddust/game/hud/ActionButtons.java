package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Ctrl.MsgType;
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
    private Bg buttons [];
    private MsgType msgs [];

    public ActionButtons(RustAndDust game)
    {
        this.bg = new Sprite(game.factory.getHudRegion(game.factory.DISABLED));
        this.ctrl = game.ctrl;
        this.visible = false;
        this.position = Position.BOTTOM_RIGHT;

        this.buttons = new Bg[Buttons.LAST.i];
        this.buttons[Buttons.DONE.i] = new Bg(game.factory.getHudRegion(game.factory.ACT_DONE));
        this.buttons[Buttons.ABORT.i] = new Bg(game.factory.getHudRegion(game.factory.ACT_ABORT));
        this.buttons[Buttons.PROMOTE.i] = new Bg(game.factory.getHudRegion(game.factory.ACT_PROMOTE));

        this.msgs = new MsgType[Buttons.LAST.i];
        this.msgs[Buttons.DONE.i] = MsgType.OK;
        this.msgs[Buttons.ABORT.i] = MsgType.CANCEL;
        this.msgs[Buttons.PROMOTE.i] = MsgType.PROMOTE;
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

    public boolean hit(float x, float y)
    {
        if (!super.hit(x,y))
            return false;

        for (int i = 0; i < Buttons.LAST.i; i++) {
            if (buttons[i].hit(x, y)) {
                ctrl.sendMsg(msgs[i]);
                return true;
            }
        }

        return false;
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
