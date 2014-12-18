package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.UnitList;

public class UnitDock extends Bg implements Animation
{
    private static final float SCALE = 0.4f;
    private static final float STEP = 5f;
    private final Ctrl ctrl;

    private float y;
    private float to;
    private float dx;
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
        this.selected = new Sprite(selected);
        this.visible = false;
        this.dx = 0f;
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
        transform.idt();
        transform.translate((rect.x + dx), (rect.y + rect.height), 0).scale(SCALE, SCALE, 0).translate(-rect.x, - (rect.y + rect.height), 0);
        point.set(rect.x, rect.y, 0).mul(transform);
        scaledRect.x = point.x;
        scaledRect.y = point.y;
        point.set((rect.x + rect.width), (rect.y + rect.height), 0).mul(transform);
        scaledRect.width = point.x - scaledRect.x;
        scaledRect.height = point.y - scaledRect.y;
    }

    public void setPosition(Position position, float y)
    {
        this.position = position;
        this.y = y;
        this.step = (position.isLeft() ? STEP : -STEP);
        this.mvtDone = true;
        this.visible = false;
        this.dx = 0f;
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
        mvtDone = false;
    }

    @Override
    public boolean hit(float x, float y)
    {
        if (visible && scaledRect.contains(x, y)) {
            int i = (int) ((scaledRect.y + scaledRect.height - y) / (scaledRect.height / units.size()));
            selectedUnit = units.get(i);
            ctrl.hud.notify(selectedUnit.toString());
            return true;
        }
        selectedUnit = null;
        return false;
    }

    public void show()
    {
        int n = ctrl.player.reinforcement();
        if (n == 0) {
            visible = false;
            return;
        }

        if (mvtDone) {
            units = ctrl.player.reinforcement;
            rect.width = units.get(0).getWidth() + (2 * padding);
            rect.height = ((units.get(0).getHeight() * n) + ((n + 1) * padding));
            float scaledWidth = (rect.width * SCALE);
            to = position.getX(scaledWidth);
            rect.x = to + (position.isLeft() ? -scaledWidth : scaledWidth);
            rect.y = y - rect.height;
            // position units
            float px = rect.x;
            float py = rect.y + rect.height;
            float ph = units.get(0).getHeight();
            for (Unit unit : units) {
                py -= (ph + padding);
                // unit.setPosition(px, py, Orientation.SOUTH.r());
                unit.centerOn((px + (rect.width / 2)), py + (ph / 2));
                unit.setRotation(position.isLeft() ? Orientation.NORTH.r() : Orientation.SOUTH.r());
            }
        } else {
            to = position.getX(rect.width * SCALE);
        }

        selectedUnit = null;
        show = true;
        mvtDone = false;
        visible = true;
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

        transform.idt();
        transform.translate((rect.x + dx), (rect.y + rect.height), 0).scale(SCALE, SCALE, 0).translate(-rect.x, - (rect.y + rect.height), 0);
        point.set(rect.x, rect.y, 0).mul(transform);
        scaledRect.x = point.x;
        scaledRect.y = point.y;
        point.set((rect.x + rect.width), (rect.y + rect.height), 0).mul(transform);
        scaledRect.width = point.x - scaledRect.x;
        scaledRect.height = point.y - scaledRect.y;
        return false;
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;

        saved.set(batch.getTransformMatrix());
        batch.setTransformMatrix(transform);

        super.draw(batch);
        for (Unit unit : units) {
            unit.draw(batch);
            if (unit == selectedUnit) {
                selected.setCenter((unit.getX() + (unit.getWidth() / 2)), (unit.getY() + (unit.getHeight() / 2)));
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
