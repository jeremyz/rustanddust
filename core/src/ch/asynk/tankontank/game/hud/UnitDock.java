package ch.asynk.tankontank.game.hud;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.game.Ctrl;

public class UnitDock extends Bg
{
    private static final float PADDING = 5f;
    private static final float SCALE = 0.4f;
    private static final float STEP = 5f;
    private final Ctrl ctrl;

    public float x;
    public float y;
    public float to;
    public boolean visible;
    public boolean show;
    public boolean done;
    private Matrix4 prevTransform;
    private Matrix4 nextTransform;

    public UnitDock(Ctrl ctrl, TextureRegion region)
    {
        super(region);
        this.ctrl = ctrl;
        this.visible = false;
        this.done = true;
        this.prevTransform = new Matrix4();
        this.nextTransform = new Matrix4();
    }

    public void setTopLeft(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    public void toggle()
    {
        if (visible) hide();
        else show();
    }

    public void hide()
    {
        if (!visible) return;
        to = -(rect.x + rect.width);
        show = false;
        done = false;
    }

    // FIXME Iterator might not be the best way to go
    public void show()
    {
        if (done) {
            int count = ctrl.player.reinforcementCount();
            Pawn pawn = ctrl.player.reinforcementIterator().next();

            rect.width = pawn.getWidth() + (2 * PADDING);
            rect.height = ((pawn.getHeight() * count) + (PADDING * (count + 1)));
            rect.x = - rect.width;
            rect.y = y - rect.height;
        }

        to = x;
        show = true;
        done = false;
        visible = true;
    }

    // FIXME why not use transformation to animate ?
    public void animate(float delta)
    {
        if (!visible) return;
        if (done) return;

        if (show) {
            if (rect.x < to)
                rect.x += STEP;
            else {
                rect.x = to;
                done = true;
            }
        } else {
            if (rect.x > to)
                rect.x -= STEP;
            else {
                rect.x = to;
                done = true;
                visible = false;

            }
        }

        Iterator<Pawn> pawns = ctrl.player.reinforcementIterator();
        float x = rect.x + PADDING;
        float y = rect.y + rect.height;
        while (pawns.hasNext()) {
            Pawn pawn = pawns.next();
            y -= (pawn.getHeight() + PADDING);
            pawn.setPosition(x, y, 90f);
        }
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;

        nextTransform.idt();
        nextTransform.translate(rect.x, (rect.y + rect.height), 0).scale(SCALE, SCALE, 0).translate(-rect.x, - (rect.y + rect.height), 0);

        prevTransform.set(batch.getTransformMatrix());
        batch.setTransformMatrix(nextTransform);

        super.draw(batch);
        Iterator<Pawn> pawns = ctrl.player.reinforcementIterator();
        while (pawns.hasNext())
            pawns.next().draw(batch);

        batch.setTransformMatrix(prevTransform);
    }
}
