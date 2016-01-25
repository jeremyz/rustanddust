package ch.asynk.rustanddust.game.hud;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Animation;
import ch.asynk.rustanddust.engine.gfx.Drawable;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.LabelImage;
import ch.asynk.rustanddust.ui.Position;

public class PlayerInfo implements Disposable, Drawable, Animation
{
    public static final int PADDING = 5;

    private final Ctrl ctrl;

    private Bg flag;
    private Bg usFlag;
    private Bg geFlag;
    private LabelImage turns;
    private LabelImage aps;
    private LabelImage reinforcement;
    public UnitDock unitDock;
    private Position position;

    public PlayerInfo(RustAndDust game)
    {
        this.ctrl = game.ctrl;
        this.position = Position.MIDDLE_CENTER;
        usFlag = new Bg(game.factory.getFlag(Army.US));
        geFlag = new Bg(game.factory.getFlag(Army.GE));
        turns = new LabelImage(game.factory.getHudRegion(game.factory.HUD_TURNS), game.font, 5f);
        aps = new LabelImage(game.factory.getHudRegion(game.factory.HUD_APS), game.font, 5f);
        reinforcement = new LabelImage(game.factory.getHudRegion(game.factory.REINFORCEMENT), game.font, 5f);
        unitDock = new UnitDock(game, 10f);
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
        aps.translate(dx, dy);
        reinforcement.translate(dx, dy);
        unitDock.translate(dx, dy);
        turns.setPosition(Position.TOP_CENTER);
        turns.setLabelPosition(Position.MIDDLE_CENTER);
    }

    public void setPosition(Position position)
    {
        if (this.position == position)
            return;
        this.position = position;

        float width = (usFlag.getWidth() + aps.getWidth() + (2 * PADDING));
        float height = (usFlag.getHeight() + reinforcement.getHeight() + (1 * PADDING));
        float x = position.getX(width);
        float y = position.getY(height);

        if (position.isLeft()) {
            reinforcement.setPosition(x, y);
            y += (reinforcement.getHeight() + PADDING);
            usFlag.setPosition(x, y);
            geFlag.setPosition(x, y);
            x += (usFlag.getWidth() + PADDING);
            aps.setPosition(x, y);
        } else {
            x = (x + width);
            reinforcement.setPosition((x - reinforcement.getWidth()), y);
            y += (reinforcement.getHeight() + PADDING);
            x -= usFlag.getWidth();
            usFlag.setPosition(x, y);
            geFlag.setPosition(x, y);
            x -= (aps.getWidth() + PADDING);
            aps.setPosition(x, y);
        }
        turns.setPosition(Position.TOP_CENTER);
        turns.setLabelPosition(Position.MIDDLE_CENTER);
        aps.setLabelPosition(Position.MIDDLE_CENTER);
        reinforcement.setLabelPosition(Position.MIDDLE_CENTER);
        unitDock.setPosition(position, reinforcement.getY() - PADDING);
    }

    public void update(Player player, Position position)
    {
        unitDock.hide();
        turns.write(String.format("%d", player.getTurn()));
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

    public boolean drag(float x, float y, int dx, int dy)
    {
        if (!unitDock.hit(x, y))
            return false;
        unitDock.drag(dx, dy);
        return true;
    }

    public boolean hit(float x, float y)
    {
        if (turns.hit(x, y)) {
            ctrl.hud.askEndOfTurn();
            return true;
        }
        else if (reinforcement.hit(x, y)) {
            ctrl.reinforcementHit();
            return true;
        }
        else if (unitDock.hit(x, y)) {
            ctrl.hud.notify(unitDock.select(x, y).toString(), Position.TOP_CENTER);
            ctrl.showEntryZone();
            return true;
        }

        return false;
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
