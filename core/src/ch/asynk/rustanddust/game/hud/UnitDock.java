package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.rustanddust.engine.gfx.Animation;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.UnitList;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Position;

public class UnitDock extends Bg implements Animation
{
    private static float SCALE = 1.0f;
    private static final float STEP = 5f;
    private static final float BOUNCE_SPEED = 5;
    private static final float SCISSORS_BOTTOM = 50f;
    private final Ctrl ctrl;

    private int n;
    private float y;
    private float to;
    private float dx;
    private float dy;
    private float step;
    private boolean show;
    private boolean mvtDone;
    public Unit selectedUnit;
    private Sprite selected;
    private UnitList units;
    private Vector3 point;
    private Matrix4 saved;
    private Matrix4 transform;
    private Rectangle scaledRect;
    private Rectangle scissors;

    public UnitDock(Ctrl ctrl, TextureRegion region, TextureRegion selected, float padding)
    {
        super(region);
        this.ctrl = ctrl;
        this.padding = padding;
        this.mvtDone = true;
        this.point = new Vector3();
        this.saved = new Matrix4();
        this.transform = new Matrix4();
        this.scaledRect = new Rectangle();
        this.scissors = new Rectangle();
        this.selected = new Sprite(selected);
        this.visible = false;
        this.dx = 0f;
        this.dy = 0f;
    }

    public static void setScale(float scale)
    {
        SCALE = scale;
    }

    @Override
    public void translate(float _dx, float _dy)
    {
        this.y += _dy;
        if (!visible) return;
        super.translate(_dx, _dy);
        for (Unit unit : units)
            unit.translate(_dx, _dy);
        to = position.getX(rect.width * SCALE);
        compute();
    }

    private void compute()
    {
        transform.idt();
        transform.translate((rect.x + dx), (rect.y + dy + rect.height), 0).scale(SCALE, SCALE, 0).translate(-rect.x, - (rect.y + rect.height), 0);
        point.set(rect.x, rect.y, 0).mul(transform);
        scaledRect.x = point.x;
        scaledRect.y = point.y;
        point.set((rect.x + rect.width), (rect.y + rect.height), 0).mul(transform);
        scaledRect.width = point.x - scaledRect.x;
        scaledRect.height = point.y - scaledRect.y;
        scissors.set(0, SCISSORS_BOTTOM, Gdx.graphics.getWidth(), (y - SCISSORS_BOTTOM));
    }

    public void setPosition(Position position, float y)
    {
        if (this.position == position)
            return;
        this.position = position;
        this.y = y;
        this.step = (position.isLeft() ? STEP : -STEP);
        this.mvtDone = true;
        this.visible = false;
        this.dx = 0f;
        scissors.set(0, SCISSORS_BOTTOM, Gdx.graphics.getWidth(), (y - SCISSORS_BOTTOM));
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public boolean hit(float x, float y)
    {
        return (visible && scaledRect.contains(x, y));
    }

    public void drag(int dx, int dy)
    {
        this.dy += dy;
        compute();
    }

    public Unit select(float x, float y)
    {
        int i = (int) ((scaledRect.y + scaledRect.height - y) / (scaledRect.height / units.size()));
        selectedUnit = units.get(i);
        return selectedUnit;
    }

    public void hide()
    {
        if (!visible) return;
        resize();
        to = rect.x;

        show = false;
        mvtDone = false;
        selectedUnit = null;
    }

    public void show()
    {
        if (!resize())
            return;
        if (dy != 0) {
            dy = 0;
            compute();
        }
        to = position.getX(rect.width * SCALE);

        show = true;
        mvtDone = false;
        selectedUnit = null;
        visible = true;
    }

    private boolean resize()
    {
        int count = ctrl.player.reinforcement();
        if (count == 0) {
            n = 0;
            return false;
        }
        if (count == n) return true;
        n = count;

        units = ctrl.player.reinforcement;
        rect.width = units.get(0).getWidth() + (2 * padding);
        rect.height = ((units.get(0).getHeight() * n) + ((n + 1) * padding));
        float scaledWidth = (rect.width * SCALE);
        to = position.getX(scaledWidth);
        rect.x = to + (position.isLeft() ? -scaledWidth : scaledWidth);
        rect.y = y - rect.height;

        float px = rect.x;
        float py = rect.y + rect.height;
        float ph = units.get(0).getHeight();
        for (Unit unit : units) {
            py -= (ph + padding);
            // unit.setPosition(px, py, Orientation.SOUTH.r());
            unit.centerOn((px + (rect.width / 2)), py + (ph / 2));
            unit.setRotation(position.isLeft() ? Orientation.NORTH.r() : Orientation.SOUTH.r());
        }

        return true;
    }

    @Override
    public boolean animate(float delta)
    {
        if (!visible) return true;
        if (mvtDone) return true;

        float x = (rect.x + dx);
        if (show) {
            if ((position.isLeft() && (x < to)) || (!position.isLeft() && x > to))
                dx += step;
            else {
                dx = (to - rect.x);
                mvtDone = true;
            }
        } else {
            if ((position.isLeft() && (x > to)) || (!position.isLeft() && x < to))
                dx -= step;
            else {
                dx = (to - rect.x);
                mvtDone = true;
                visible = false;
            }
        }

        compute();
        return false;
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;

        float top = scaledRect.y + scaledRect.height;
        if ((int)top != (int)y) {
            if (top < y) {
                this.dy += Math.min(BOUNCE_SPEED, (y - top));
                compute();
            } else if (scaledRect.y > SCISSORS_BOTTOM) {
                this.dy -= Math.min(BOUNCE_SPEED, (scaledRect.y - SCISSORS_BOTTOM));
                compute();
            }
        }

        saved.set(batch.getTransformMatrix());
        batch.setTransformMatrix(transform);

        // batch.flush();
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor((int)scissors.x, (int)scissors.y, (int)scissors.width, (int)scissors.height);

        super.draw(batch);
        for (Unit unit : units) {
            unit.draw(batch);
            if (unit == selectedUnit) {
                selected.setCenter((unit.getX() + (unit.getWidth() / 2)), (unit.getY() + (unit.getHeight() / 2)));
                selected.draw(batch);
            }
        }

        batch.setTransformMatrix(saved);

        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        // batch.flush();
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;

        saved.set(shapes.getTransformMatrix());
        shapes.setTransformMatrix(transform);

        shapes.rect(rect.x, rect.y, rect.width, rect.height);

        shapes.setTransformMatrix(saved);

        shapes.rect(scissors.x, scissors.y, scissors.width, scissors.height);
    }
}
