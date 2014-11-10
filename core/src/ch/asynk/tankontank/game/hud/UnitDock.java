package ch.asynk.tankontank.game.hud;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Ctrl;

public class UnitDock extends Bg
{
    private static final float SCALE = 0.4f;
    private static final float STEP = 5f;
    private final Ctrl ctrl;

    private float padding;
    private float y;
    private float to;
    private float dx;
    private float step;
    private Position position;
    private boolean show;
    private boolean done;
    public Pawn selectedPawn;
    private Sprite selected;
    private List<Pawn> pawns;
    private Vector3 point;
    private Matrix4 saved;
    private Matrix4 transform;
    private Rectangle scaledRect;

    public UnitDock(Ctrl ctrl, TextureRegion bg, TextureRegion selected, float padding)
    {
        super(bg);
        this.ctrl = ctrl;
        this.padding = padding;
        this.done = true;
        this.point = new Vector3();
        this.saved = new Matrix4();
        this.transform = new Matrix4();
        this.scaledRect = new Rectangle();
        this.selected = new Sprite(selected);
        this.visible = false;
    }

    public void setPosition(Position position, float y)
    {
        this.position = position;
        this.y = y;
        this.step = (position.isLeft() ? STEP : -STEP);
        this.done = true;
        this.visible = false;
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
        to = rect.x;
        show = false;
        done = false;
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (visible && scaledRect.contains(x, y)) {
            int i = (int) ((scaledRect.y + scaledRect.height - y) / (scaledRect.height / pawns.size()));
            selectedPawn = pawns.get(i);
            ctrl.hud.notify(selectedPawn.toString());
            return true;
        }
        selectedPawn = null;
        return false;
    }

    public void show()
    {
        float x = position.getX(rect.width * SCALE);
        if (done) {
            pawns = ctrl.player.getReinforcement();
            rect.width = pawns.get(0).getWidth() + (2 * padding);
            rect.height = ((pawns.get(0).getHeight() * pawns.size()) + ((pawns.size() + 1) * padding));
            rect.x = (position.isLeft() ? (0 - (rect.width * SCALE)) : (x + (rect.width * SCALE)));
            rect.y = y - rect.height;
            // position units here
            float px = rect.x;
            float py = rect.y + rect.height;
            float ph = pawns.get(0).getHeight();
            for (Pawn pawn : pawns) {
                py -= (ph + padding);
                // pawn.setPosition(px, py, Orientation.SOUTH.r());
                pawn.centerOn((px + (rect.width / 2)), py + (ph / 2));
                pawn.setRotation(Orientation.SOUTH.r());
            }
        }

        selectedPawn = null;
        to = x;
        show = true;
        done = false;
        visible = true;
        dx = 0f;
    }

    public void animate(float delta)
    {
        if (!visible) return;
        if (done) return;

        float x = (rect.x + dx);
        if (show) {
            if ((position.isLeft() && (x < to)) || (!position.isLeft() && x > to))
                dx += step;
            else {
                dx = (to - rect.x);
                done = true;
            }
        } else {
            if ((position.isLeft() && (x > to)) || (!position.isLeft() && x < to))
                dx -= step;
            else {
                dx = (to - rect.x);
                done = true;
                visible = false;
            }
        }

        transform.idt();
        transform.translate((rect.x + dx), (rect.y + rect.height), 0).scale(SCALE, SCALE, 0).translate(-rect.x, - (rect.y + rect.height), 0);
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
        for (Pawn pawn : pawns) {
            pawn.draw(batch);
            if (pawn == selectedPawn) {
                selected.setCenter((pawn.getX() + (pawn.getWidth() / 2)), (pawn.getY() + (pawn.getHeight() / 2)));
                selected.draw(batch);
            }
        }

        batch.setTransformMatrix(saved);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;

        saved.set(shapes.getTransformMatrix());
        shapes.setTransformMatrix(transform);

        shapes.rect(rect.x, rect.y, rect.width, rect.height);

        shapes.setTransformMatrix(saved);
    }
}
