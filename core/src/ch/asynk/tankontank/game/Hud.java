package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.game.State.StateType;
import ch.asynk.tankontank.game.hud.Position;
import ch.asynk.tankontank.game.hud.Msg;
import ch.asynk.tankontank.game.hud.PlayerInfo;
import ch.asynk.tankontank.game.hud.ActionButtons;
import ch.asynk.tankontank.game.hud.OkCancel;
import ch.asynk.tankontank.game.hud.Statistics;

import ch.asynk.tankontank.TankOnTank;

public class Hud implements Disposable
{
    public static final float OFFSET = 10f;

    private final TankOnTank game;
    private final Ctrl ctrl;

    private Object hit;
    private BitmapFont font;

    public PlayerInfo playerInfo;
    public ActionButtons actionButtons;

    private Msg msg;
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

        playerInfo = new PlayerInfo(ctrl, font, atlas, 5f);
        actionButtons = new ActionButtons(ctrl, atlas.findRegion("disabled"), atlas, 5f);
        actionButtons.hide();
        msg = new Msg(font, atlas.findRegion("disabled"), 10f);
        okCancel = new OkCancel(font, atlas.findRegion("disabled"), atlas, 10f);
        stats = new Statistics(font, atlas.findRegion("disabled"), atlas, 10f);
    }

    @Override
    public void dispose()
    {
        font.dispose();
        playerInfo.dispose();
        actionButtons.dispose();
        msg.dispose();
        okCancel.dispose();
        stats.dispose();
    }

    public void changeState(StateType from, StateType to)
    {
        if (to != StateType.ENTRY);
            playerInfo.hideUnitDock();

        if ((to == StateType.SELECT) || (to == StateType.ENTRY))
            playerInfo.blockReinforcement(false);
        else
            playerInfo.blockReinforcement(true);
    }

    public void update()
    {
        Position position = ctrl.battle.getHudPosition(ctrl.player);
        playerInfo.update(ctrl.player, position);
        actionButtons.setPosition(position.down());
    }

    public void animate(float delta)
    {
        msg.animate(delta);
        playerInfo.animate(delta);
    }

    public void draw(Batch batch)
    {
        playerInfo.draw(batch);
        actionButtons.draw(batch);
        msg.draw(batch);
        okCancel.draw(batch);
        stats.draw(batch);
    }

    public void drawDebug(ShapeRenderer debugShapes)
    {
        playerInfo.drawDebug(debugShapes);
        actionButtons.drawDebug(debugShapes);
        msg.drawDebug(debugShapes);
        okCancel.drawDebug(debugShapes);
        stats.drawDebug(debugShapes);
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

    public boolean touchDown(float x, float y)
    {
        hit = null;

        if (actionButtons.touchDown(x, y))
            hit = actionButtons;
        if (playerInfo.touchDown(x, y))
            hit = playerInfo;
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
        else if (hit == playerInfo)
            playerInfo.touchUp(x, y);
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

    public void askEndTurn()
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
