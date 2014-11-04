package ch.asynk.tankontank.game.hud;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Ctrl;

public class UnitDock extends Bg
{
    private static final float PADDING = 10f;
    private static final float SCALE = 0.4f;
    private static final float STEP = 5f;
    private final Ctrl ctrl;

    public float x;
    public float y;
    public float to;
    public boolean visible;
    public boolean show;
    public boolean done;
    private List<Pawn> pawns;
    private Vector3 point;
    private Matrix4 saved;
    private Matrix4 transform;
    protected Rectangle scaledRect;

    public UnitDock(Ctrl ctrl, TextureRegion region)
    {
        super(region);
        this.ctrl = ctrl;
        this.visible = false;
        this.done = true;
        this.point = new Vector3();
        this.saved = new Matrix4();
        this.transform = new Matrix4();
        this.scaledRect = new Rectangle();
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

    @Override
    public boolean hit(float x, float y)
    {
        return scaledRect.contains(x, y);
    }

    public void show()
    {
        if (done) {
            pawns = ctrl.player.getReinforcement();
            rect.width = pawns.get(0).getWidth() + (2 * PADDING);
            rect.height = ((pawns.get(0).getHeight() * pawns.size()) + (PADDING * (pawns.size() + 1)));
            rect.x = - rect.width;
            rect.y = y - rect.height;
        }

        to = x;
        show = true;
        done = false;
        visible = true;
    }

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

        float x = rect.x + PADDING;
        float y = rect.y + rect.height;
        for (Pawn pawn : pawns) {
            y -= (pawn.getHeight() + PADDING);
            pawn.setPosition(x, y, Orientation.SOUTH.r());
        }

        transform.idt();
        transform.translate(rect.x, (rect.y + rect.height), 0).scale(SCALE, SCALE, 0).translate(-rect.x, - (rect.y + rect.height), 0);
        point.set(rect.x, rect.y, 0).mul(transform);
        scaledRect.x = point.x;
        scaledRect.y = point.y;
        point.set((rect.x + rect.width), (rect.y + rect.height), 0).mul(transform);
        scaledRect.width = point.x - scaledRect.x;
        scaledRect.height = point.y - scaledRect.y;
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;

        saved.set(batch.getTransformMatrix());
        batch.setTransformMatrix(transform);

        super.draw(batch);
        for (Pawn pawn : pawns)
            pawn.draw(batch);

        batch.setTransformMatrix(saved);
    }
}
