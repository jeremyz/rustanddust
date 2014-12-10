package ch.asynk.tankontank.game;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.game.State.StateType;
import ch.asynk.tankontank.game.hud.Position;
import ch.asynk.tankontank.game.hud.Msg;
import ch.asynk.tankontank.game.hud.PlayerInfo;
import ch.asynk.tankontank.game.hud.ActionButtons;
import ch.asynk.tankontank.game.hud.OkCancel;
import ch.asynk.tankontank.game.hud.Statistics;
import ch.asynk.tankontank.game.hud.Engagement;
import ch.asynk.tankontank.game.hud.Widget;

import ch.asynk.tankontank.TankOnTank;

public class Hud implements Disposable, Animation
{
    public static final float OFFSET = 10f;
    public static final float NOTIFY_DURATION = 2f;

    private final TankOnTank game;
    private final Ctrl ctrl;

    private Object hit;
    private BitmapFont font;

    public PlayerInfo playerInfo;
    public ActionButtons actionButtons;

    private Msg msg;
    private Statistics stats;
    private Engagement engagement;
    private OkCancel okCancel;
    private LinkedList<Widget> dialogs = new LinkedList<Widget>();

    public Hud(final Ctrl ctrl, final TankOnTank game)
    {
        this.game = game;
        this.ctrl = ctrl;

        TextureAtlas atlas = game.factory.hudAtlas;
        font = new BitmapFont(Gdx.files.internal("skin/veteran.fnt"), atlas.findRegion("veteran-white"));
        playerInfo = new PlayerInfo(ctrl, font, atlas, 5f);
        actionButtons = new ActionButtons(ctrl, atlas, 5f);
        actionButtons.hide();
        msg = new Msg(font, atlas, 10f);
        okCancel = new OkCancel(font, atlas, 10f);
        stats = new Statistics(font, atlas, 10f);
        engagement = new Engagement(font, atlas);
    }

    @Override
    public void dispose()
    {
        font.dispose();
        playerInfo.dispose();
        actionButtons.dispose();
        msg.dispose();
        okCancel.dispose();
        engagement.dispose();
        stats.dispose();
    }

    public void update()
    {
        Position position = ctrl.battle.getHudPosition(ctrl.player);
        playerInfo.update(ctrl.player, position);
        actionButtons.setPosition(position.horizontalMirror());
    }

    @Override
    public boolean animate(float delta)
    {
        msg.animate(delta);
        playerInfo.animate(delta);
        engagement.animate(delta);
        return false;
    }

    public void draw(Batch batch, boolean debug)
    {
        draw(batch);
        if (debug)
            font.draw(batch, String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), 80, 25);
    }

    @Override
    public void draw(Batch batch)
    {
        playerInfo.draw(batch);
        actionButtons.draw(batch);
        msg.draw(batch);
        okCancel.draw(batch);
        engagement.draw(batch);
        stats.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        playerInfo.drawDebug(debugShapes);
        actionButtons.drawDebug(debugShapes);
        msg.drawDebug(debugShapes);
        okCancel.drawDebug(debugShapes);
        engagement.drawDebug(debugShapes);
        stats.drawDebug(debugShapes);
    }

    public void pushNotify(String s)
    {
        notify(s, NOTIFY_DURATION, Position.TOP_CENTER, true);
    }

    public void notify(String s)
    {
        notify(s, NOTIFY_DURATION, Position.TOP_CENTER, false);
    }

    public void notify(String s, float duration, Position position, boolean push)
    {
        if (push) msg.pushWrite(s, duration, position);
        else msg.write(s, duration, position);
    }

    public boolean touchDown(float x, float y)
    {
        hit = null;

        if (dialogs.size() > 0) {
            Widget dialog = dialogs.getFirst();
            if (dialog.hit(x, y)) {
                hit = dialog;
                return true;
            }
            return false;
        }

        if (ctrl.isInAnimation())
            return false;

        if (hit == null) {
            if (actionButtons.touchDown(x, y))
                hit = actionButtons;
            else if (playerInfo.touchDown(x, y))
                hit = playerInfo;
        }

        return (hit != null);
    }

    public boolean touchUp(float x, float y)
    {
        if (hit == null)
            return false;

        if (dialogs.size() > 0) {
            Widget dialog = dialogs.getFirst();
            if (hit == dialog) {
                if (dialog.hit(x, y))
                    closeDialog();
                hit = null;
            }
        } else {
            if (hit == actionButtons) {
                actionButtons.touchUp(x, y);
            }
            else if (hit == playerInfo) {
                playerInfo.touchUp(x, y);
            }

            hit = null;
        }

        return true;
    }

    private void closeDialog()
    {
        Widget dialog = dialogs.removeFirst();
        dialog.visible = false;

        if (dialog == okCancel)
            closeOkCancel();
        else if (dialog == stats)
            ctrl.endGame();
        else if (dialog == engagement) {
            ctrl.animationDone();
        }

        if (dialogs.size() > 0)
            dialogs.getFirst().visible = true;
        else
            ctrl.blockMap = false;
    }

    private void closeOkCancel()
    {
        boolean ok = okCancel.ok;

        switch(okCancel.action) {
            case EXIT_BOARD:
                ctrl.exitBoard(ok);
                break;
            case END_TURN:
                if (ok)
                    ctrl.endPlayerTurn(false);
                break;
            case ABORT_TURN:
                if (ok)
                    ctrl.endPlayerTurn(true);
                break;
            case END_DEPLOYMENT:
                if (ok)
                    ctrl.endDeployment();
                break;
        }
    }

    public boolean dialogActive()
    {
        return (dialogs.size() > 0);
    }

    private void pushDialog(Widget dialog)
    {
        ctrl.blockMap = true;
        if (dialogs.size() != 0)
            dialog.visible = false;
        dialogs.addLast(dialog);
    }

    public void notifyEndOfTurn()
    {
        okCancel.show("You have no more Action Points left.", OkCancel.Action.END_TURN);
        okCancel.noCancel();
        pushDialog(okCancel);
    }

    public void askExitBoard()
    {
        okCancel.show("Do you want this unit to escape the battle fierd ?", OkCancel.Action.EXIT_BOARD);
        pushDialog(okCancel);
    }

    public void askEndOfTurn()
    {
        okCancel.show("You still have Action Points left.\nEnd your Turn anyway ?", OkCancel.Action.ABORT_TURN);
        pushDialog(okCancel);
    }

    public void askEndDeployment()
    {
        okCancel.show("Deployment unit count reached.\nEnd Deployment phase ?", OkCancel.Action.END_DEPLOYMENT);
        pushDialog(okCancel);
    }

    public void engagementSummary(Map.Engagement e, float volume)
    {
        engagement.show(e, Position.BOTTOM_CENTER, volume);
        pushDialog(engagement);
    }

    public void victory(Player winner, Player loser)
    {
        stats.show(winner, loser, Position.MIDDLE_CENTER);
        pushDialog(stats);
    }
}
