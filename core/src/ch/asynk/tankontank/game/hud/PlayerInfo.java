package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// TODO replace with Sprite !!
import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.engine.gfx.Image;

import ch.asynk.tankontank.game.State.StateType;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.Hud;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Player;

public class PlayerInfo implements Disposable, Drawable
{
    private final Ctrl ctrl;

    private Object hit;

    private float padding;
    private Image flag;
    private Image usFlag;
    private Image geFlag;
    private LabelImage turns;
    private LabelImage aps;
    private LabelImage reinforcement;
    public UnitDock unitDock;

    public PlayerInfo(Ctrl ctrl, BitmapFont font, TextureAtlas atlas, float padding)
    {
        this.ctrl = ctrl;
        this.padding = padding;
        usFlag = new Image(atlas.findRegion("us-flag"));
        geFlag = new Image(atlas.findRegion("ge-flag"));
        turns = new LabelImage(atlas.findRegion("turns"), font, 5f);
        aps = new LabelImage(atlas.findRegion("aps"), font, 5f);
        reinforcement = new LabelImage(atlas.findRegion("reinforcement"), font, 5f);
        unitDock = new UnitDock(ctrl, atlas.findRegion("disabled"), atlas.findRegion("reinforcement-selected"), 10f);
    }

    @Override
    public void dispose()
    {
        usFlag.dispose();
        geFlag.dispose();
        turns.dispose();
        aps.dispose();
        reinforcement.dispose();
        unitDock.dispose();
    }

    public void setPosition(Position position)
    {
        float width = (usFlag.getWidth() + turns.getWidth() + aps.getWidth() + (2 * padding));
        float height = (usFlag.getHeight() + reinforcement.getHeight() + (1 * padding));
        float x = position.getX(width);
        float y = position.getY(height);

        if (position.isLeft()) {
            reinforcement.setPosition(x, y);
            reinforcement.setLabelPosition(Position.TOP_LEFT);
            y += (reinforcement.getHeight() + padding);
            usFlag.setPosition(x, y);
            geFlag.setPosition(x, y);
            x += (usFlag.getWidth() + padding);
            turns.setPosition(x, y);
            x += (turns.getWidth() + padding);
            aps.setPosition(x, y);
            aps.setLabelPosition(Position.TOP_RIGHT);
        } else {
            x = (x + width);
            reinforcement.setPosition((x - reinforcement.getWidth()), y);
            reinforcement.setLabelPosition(Position.TOP_LEFT);
            y += (reinforcement.getHeight() + padding);
            x -= usFlag.getWidth();
            usFlag.setPosition(x, y);
            geFlag.setPosition(x, y);
            x -= (turns.getWidth() + padding);
            turns.setPosition(x, y);
            x -= (aps.getWidth() + padding);
            aps.setPosition(x, y);
            aps.setLabelPosition(Position.TOP_RIGHT);
        }
        unitDock.setPosition(position, reinforcement.getY() - padding);
    }

    public void update(Player player, Position position)
    {
        unitDock.hide();
        turns.write("" + player.getTurn());
        aps.write("" + player.getAp());
        int r = player.getReinforcement().size();
        if (r == 0) {
            reinforcement.visible = false;
        } else {
            reinforcement.visible = true;
            reinforcement.write("" + r);
        }

        if (player.getFaction() == Army.GE)
            flag = geFlag;
        else
            flag = usFlag;

        setPosition(position);
    }

    public Unit getDockUnit()
    {
        return (Unit) unitDock.selectedPawn;
    }

    public void hideUnitDock()
    {
        unitDock.hide();
    }

    public void blockReinforcement(boolean blocked)
    {
        reinforcement.blocked = blocked;
    }

    public boolean touchDown(float x, float y)
    {
        hit = null;

        if (turns.hit(x,y))
            hit = turns;
        else if (unitDock.hit(x, y))
            hit = unitDock;
        else if (reinforcement.hit(x, y))
            hit = reinforcement;
        else
            return false;

        return true;
    }

    public boolean touchUp(float x, float y)
    {
        if (hit == null)
            return false;

        if ((hit == turns) && turns.hit(x, y))
            ctrl.hud.askEndTurn();
        else if ((hit == reinforcement) && reinforcement.hit(x, y))
            unitDock.toggle();
        else if ((hit == unitDock) && unitDock.hit(x, y))
            ctrl.setState(StateType.ENTRY);

        hit = null;

        return true;
    }

    public void animate(float delta)
    {
        unitDock.animate(delta);
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
        flag.drawDebug(debugShapes);
        turns.drawDebug(debugShapes);
        aps.drawDebug(debugShapes);
        reinforcement.drawDebug(debugShapes);
        unitDock.drawDebug(debugShapes);
    }
}
