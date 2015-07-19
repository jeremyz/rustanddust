package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Animation;
import ch.asynk.rustanddust.engine.gfx.Drawable;

import ch.asynk.rustanddust.game.State.StateType;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Hud;
import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.ui.LabelImage;
import ch.asynk.rustanddust.ui.Position;

public class PlayerInfo implements Disposable, Drawable, Animation
{
    public static int PADDING = 5;

    private final Ctrl ctrl;

    private Object hit;

    private Sprite flag;
    private Sprite usFlag;
    private Sprite geFlag;
    private LabelImage turns;
    private LabelImage aps;
    private LabelImage reinforcement;
    public UnitDock unitDock;
    private Position position;

    public PlayerInfo(Ctrl ctrl, BitmapFont font, TextureAtlas uiAtlas, TextureAtlas hudAtlas)
    {
        this.ctrl = ctrl;
        this.position = Position.MIDDLE_CENTER;
        usFlag = new Sprite(hudAtlas.findRegion("us-flag"));
        geFlag = new Sprite(hudAtlas.findRegion("ge-flag"));
        turns = new LabelImage(hudAtlas.findRegion("turns"), font, 5f);
        aps = new LabelImage(hudAtlas.findRegion("aps"), font, 5f);
        reinforcement = new LabelImage(hudAtlas.findRegion("reinforcement"), font, 5f);
        unitDock = new UnitDock(ctrl, uiAtlas.findRegion("disabled"), hudAtlas.findRegion("reinforcement-selected"), 10f);
    }

    @Override
    public void dispose()
    {
        turns.dispose();
        aps.dispose();
        reinforcement.dispose();
        unitDock.dispose();
    }

    public void updatePosition()
    {
        float dx = (position.getX(usFlag.getWidth()) - usFlag.getX());
        float dy = (position.getY(usFlag.getHeight()) - usFlag.getY());
        usFlag.translate(dx, dy);
        geFlag.translate(dx, dy);
        turns.translate(dx, dy);
        aps.translate(dx, dy);
        reinforcement.translate(dx, dy);
        unitDock.translate(dx, dy);
    }

    public void setPosition(Position position)
    {
        if (this.position == position)
            return;
        this.position = position;

        float width = (usFlag.getWidth() + turns.getWidth() + aps.getWidth() + (2 * PADDING));
        float height = (usFlag.getHeight() + reinforcement.getHeight() + (1 * PADDING));
        float x = position.getX(width);
        float y = position.getY(height);

        if (position.isLeft()) {
            reinforcement.setPosition(x, y);
            y += (reinforcement.getHeight() + PADDING);
            usFlag.setPosition(x, y);
            geFlag.setPosition(x, y);
            x += (usFlag.getWidth() + PADDING);
            turns.setPosition(x, y);
            x += (turns.getWidth() + PADDING);
            aps.setPosition(x, y);
        } else {
            x = (x + width);
            reinforcement.setPosition((x - reinforcement.getWidth()), y);
            y += (reinforcement.getHeight() + PADDING);
            x -= usFlag.getWidth();
            usFlag.setPosition(x, y);
            geFlag.setPosition(x, y);
            x -= (turns.getWidth() + PADDING);
            turns.setPosition(x, y);
            x -= (aps.getWidth() + PADDING);
            aps.setPosition(x, y);
        }
        aps.setLabelPosition(Position.TOP_RIGHT);
        turns.setLabelPosition(Position.MIDDLE_CENTER);
        reinforcement.setLabelPosition(Position.TOP_LEFT);
        unitDock.setPosition(position, reinforcement.getY() - PADDING);
    }

    public void update(Player player, Position position)
    {
        unitDock.hide();
        turns.write(String.format("%d", player.getCurrentTurn()));
        aps.write(String.format("%d", player.getAp()));
        int r = player.reinforcement();
        if (r == 0) {
            reinforcement.visible = false;
        } else {
            reinforcement.visible = true;
            reinforcement.write(String.format("%d",  r));
        }

        if (player.is(Army.GE))
            flag = geFlag;
        else
            flag = usFlag;

        setPosition(position);
    }

    public void blockEndOfTurn(boolean blocked)
    {
        turns.blocked = blocked;
    }

    public boolean touchDown(float x, float y)
    {
        hit = null;

        if (reinforcement.hit(x, y))
            hit = reinforcement;
        else if (unitDock.hit(x, y))
            hit = unitDock;
        else if (turns.hit(x,y))
            hit = turns;

        return (hit != null);
    }

    public boolean touchUp(float x, float y)
    {
        if (hit == null)
            return false;

        if (hit == turns) {
            if (turns.hit(x, y))
                ctrl.hud.askEndOfTurn();
        }
        else if (hit == reinforcement) {
            if (reinforcement.hit(x, y))
                ctrl.reinforcementHit();
        }
        else if (hit == unitDock) {
            if (unitDock.hit(x, y)) {
                ctrl.hud.notify(unitDock.select(x, y).toString());
                ctrl.stateTouchUp();
            }
        }

        hit = null;

        return true;
    }

    @Override
    public boolean animate(float delta)
    {
        unitDock.animate(delta);
        return false;
    }

    @Override
    public void draw(Batch batch)
    {
        flag.draw(batch);
        turns.draw(batch);
        aps.draw(batch);
        reinforcement.draw(batch);
        unitDock.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        turns.drawDebug(debugShapes);
        aps.drawDebug(debugShapes);
        reinforcement.drawDebug(debugShapes);
        unitDock.drawDebug(debugShapes);
        debugShapes.rect(flag.getX(), flag.getY(), flag.getWidth(), flag.getHeight());
    }
}
