package ch.asynk.tankontank.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Image;

import ch.asynk.tankontank.game.State.StateType;
import ch.asynk.tankontank.game.hud.Msg;
import ch.asynk.tankontank.game.hud.ActionButtons;
import ch.asynk.tankontank.game.hud.LabelImage;
import ch.asynk.tankontank.game.hud.UnitDock;
import ch.asynk.tankontank.game.hud.OkCancel;
import ch.asynk.tankontank.game.hud.Statistics;
import ch.asynk.tankontank.game.hud.Position;

import ch.asynk.tankontank.TankOnTank;

public class Hud implements Disposable
{
    public static final float OFFSET = 10f;
    public static final float PADDING = 5f;

    private final TankOnTank game;
    private final Ctrl ctrl;

    private Msg msg;

    private Object hit;

    private Image flag;
    private Image usFlag;
    private Image geFlag;
    private LabelImage turns;
    private LabelImage aps;
    private LabelImage reinforcement;
    private UnitDock unitDock;

    public ActionButtons actionButtons;

    private BitmapFont font;

    private Statistics stats;
    private OkCancel okCancel;
    private DialogAction dialogAction;

    enum DialogAction
    {
        END_TURN,
        END_GAME
    }

    public Hud(final Ctrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;

        font = game.skin.getFont("default-font");
        TextureAtlas atlas = game.factory.hudAtlas;

        actionButtons = new ActionButtons(ctrl, atlas.findRegion("disabled"), atlas, 5f);
        actionButtons.hide();

        usFlag = new Image(atlas.findRegion("us-flag"));
        geFlag = new Image(atlas.findRegion("ge-flag"));
        turns = new LabelImage(atlas.findRegion("turns"), font, 5f);
        aps = new LabelImage(atlas.findRegion("aps"), font, 5f);
        reinforcement = new LabelImage(atlas.findRegion("reinforcement"), font, 5f);
        unitDock = new UnitDock(ctrl, atlas.findRegion("disabled"), atlas.findRegion("reinforcement-selected"), 10f);

        msg = new Msg(font, atlas.findRegion("disabled"), 10f);
        okCancel = new OkCancel(font, atlas.findRegion("disabled"), atlas, 10f);
        stats = new Statistics(font, atlas.findRegion("disabled"), atlas, 10f);

        float x = OFFSET;
        float y = (Gdx.graphics.getHeight() - OFFSET);
        usFlag.setPosition(x, (y - usFlag.getHeight()));
        geFlag.setPosition(x, (y - geFlag.getHeight()));
        turns.setPosition((usFlag.getX() + usFlag.getWidth() + 10), usFlag.getY());
        aps.setPosition((turns.getX() + turns.getWidth() + 10), turns.getY());
        aps.setLabelPosition(Position.TOP_RIGHT);
        reinforcement.setPosition(x, usFlag.getY() - reinforcement.getHeight() - 0);
        reinforcement.setLabelPosition(Position.TOP_LEFT);
        unitDock.setTopLeft(OFFSET, reinforcement.getY() - 5);
    }

    @Override
    public void dispose()
    {
        font.dispose();

        actionButtons.dispose();

        usFlag.dispose();
        geFlag.dispose();
        turns.dispose();
        aps.dispose();
        reinforcement.dispose();
        unitDock.dispose();

        msg.dispose();
        okCancel.dispose();
        stats.dispose();
    }

    public void changeState(StateType from, StateType to)
    {
        if (to != StateType.ENTRY);
            unitDock.hide();

        if ((to == StateType.SELECT) || (to == StateType.ENTRY))
            reinforcement.blocked = false;
        else
            reinforcement.blocked = true;
    }

    public void update()
    {
        unitDock.hide();
        turns.write("" + ctrl.player.getTurn());
        aps.write("" + ctrl.player.getAp());
        int r = ctrl.player.getReinforcement().size();
        if (r == 0) {
            reinforcement.visible = false;
        } else {
            reinforcement.visible = true;
            reinforcement.write("" + r);
        }

        if (ctrl.player.getFaction() == Army.GE)
            flag = geFlag;
        else
            flag = usFlag;
    }

    public void animate(float delta)
    {
        msg.animate(delta);
        unitDock.animate(delta);
    }

    public void draw(Batch batch)
    {
        flag.draw(batch);
        turns.draw(batch);
        aps.draw(batch);
        reinforcement.draw(batch);
        unitDock.draw(batch);

        actionButtons.draw(batch);

        msg.draw(batch);
        okCancel.draw(batch);
        stats.draw(batch);
    }

    public void drawDebug(ShapeRenderer debugShapes)
    {
        flag.drawDebug(debugShapes);
        turns.drawDebug(debugShapes);
        aps.drawDebug(debugShapes);
        reinforcement.drawDebug(debugShapes);
        unitDock.drawDebug(debugShapes);

        actionButtons.drawDebug(debugShapes);

        msg.drawDebug(debugShapes);
        okCancel.drawDebug(debugShapes);
        stats.drawDebug(debugShapes);
    }

    public Unit getDockUnit()
    {
        return (Unit) unitDock.selectedPawn;
    }

    public void pushNotify(String s)
    {
        notify(s, 1, Position.MIDDLE_CENTER, true);
    }

    public void notify(String s)
    {
        notify(s, 1, Position.MIDDLE_CENTER, false);
    }

    public void notify(String s, float duration, Position position, boolean push)
    {
        if (push) msg.pushWrite(s, duration, position);
        else msg.write(s, 1, position);
    }

    public void hideUnitDock()
    {
        unitDock.hide();
    }

    public boolean touchDown(float x, float y)
    {
        hit = null;

        if (actionButtons.touchDown(x, y))
            hit = actionButtons;
        else if (turns.hit(x,y))
            hit = turns;
        else if (unitDock.hit(x, y))
            hit = unitDock;
        else if (reinforcement.hit(x, y))
            hit = reinforcement;
        else if (okCancel.hit(x, y))
            hit = okCancel;
        else if (stats.hit(x, y))
            hit = stats;
        else
            return false;

        return true;
    }

    public boolean touchUp(float x, float y)
    {
        if (hit == null)
            return false;

        if (hit == actionButtons)
            actionButtons.touchUp(x, y);
        else if ((hit == turns) && turns.hit(x, y))
            askEndTurn();
        else if ((hit == reinforcement) && reinforcement.hit(x, y))
            unitDock.toggle();
        else if ((hit == unitDock) && unitDock.hit(x, y))
            ctrl.setState(StateType.ENTRY);
        else if ((hit == okCancel) && okCancel.hit(x, y))
            closeDialog();
        else if ((hit == stats) && stats.hit(x, y))
            closeDialog();

        hit = null;

        return true;
    }

    private void closeDialog()
    {
        switch(dialogAction)
        {
            case END_TURN:
                if (okCancel.ok)
                    ctrl.endPlayerTurn();
                okCancel.visible = false;
                break;
            case END_GAME:
                stats.visible = false;
                ctrl.endGame();
                break;
        }
        ctrl.blockMap = false;
    }

    private void askEndTurn()
    {
        ctrl.blockMap = true;
        dialogAction = DialogAction.END_TURN;
        okCancel.show("You still have Action Points left.\nEnd your Turn anyway ?", Position.MIDDLE_CENTER);
    }

    public void victory(Player winner, Player loser)
    {
        ctrl.blockMap = true;
        dialogAction = DialogAction.END_GAME;
        stats.show(winner, loser, Position.MIDDLE_CENTER);
    }
}
