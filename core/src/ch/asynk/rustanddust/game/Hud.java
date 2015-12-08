package ch.asynk.rustanddust.game;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Animation;
import ch.asynk.rustanddust.game.State.StateType;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.ui.Msg;
import ch.asynk.rustanddust.ui.OkCancel;
import ch.asynk.rustanddust.ui.Widget;
import ch.asynk.rustanddust.game.hud.PlayerInfo;
import ch.asynk.rustanddust.game.hud.ActionButtons;
import ch.asynk.rustanddust.game.hud.StatisticsPanel;
import ch.asynk.rustanddust.game.hud.EngagementPanel;

import ch.asynk.rustanddust.RustAndDust;

public class Hud implements Disposable, Animation
{
    public static final float OFFSET = 10f;
    public static final float NOTIFY_DURATION = 2f;

    private final RustAndDust game;
    private final Ctrl ctrl;

    private Object hit;

    public PlayerInfo playerInfo;
    public ActionButtons actionButtons;

    private Msg msg;
    private StatisticsPanel stats;
    private EngagementPanel engagement;
    private OkCancel okCancel;
    private LinkedList<Widget> dialogs = new LinkedList<Widget>();

    public enum OkCancelAction
    {
        EXIT_BOARD,
        ABORT_TURN,
        END_TURN,
        END_DEPLOYMENT,
    }
    private OkCancelAction okCancelAction;

    public Hud(final Ctrl ctrl, final RustAndDust game)
    {
        this.game = game;
        this.ctrl = ctrl;

        TextureAtlas hudAtlas = game.factory.hudAtlas;
        playerInfo = new PlayerInfo(ctrl, game.font, game.uiAtlas, hudAtlas);
        actionButtons = new ActionButtons(ctrl, game.uiAtlas, hudAtlas);
        actionButtons.hide();
        msg = new Msg(game.font, game.uiAtlas);
        okCancel = new OkCancel(game.font, game.uiAtlas);
        stats = new StatisticsPanel(game.font, game.uiAtlas);
        engagement = new EngagementPanel(game.font, game.uiAtlas, hudAtlas);
    }

    @Override
    public void dispose()
    {
        playerInfo.dispose();
        actionButtons.dispose();
        msg.dispose();
        okCancel.dispose();
        engagement.dispose();
        stats.dispose();
    }

    public void resize(int left, int bottom, int width, int height)
    {
        Position.update(left, bottom, width, height);
        playerInfo.updatePosition();
        actionButtons.updatePosition();
        msg.updatePosition();
        stats.updatePosition();
        engagement.updatePosition();
        okCancel.updatePosition();
    }

    public void update()
    {
        Position position = ctrl.battle.getHudPosition();
        playerInfo.update(ctrl.battle.getPlayer(), position);
        actionButtons.update(position.horizontalMirror());
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
            game.font.draw(batch, String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), 80, 25);
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

    public boolean drag(float x, float y, int dx, int dy)
    {
        return playerInfo.drag(x, y, dx, dy);
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
        else if (dialog == engagement)
            ctrl.engagementPanelClosed();

        if (dialogs.size() > 0)
            dialogs.getFirst().visible = true;
        else
            ctrl.blockMap = false;
    }

    private void closeOkCancel()
    {
        boolean ok = okCancel.ok;

        switch(okCancelAction) {
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

    public void notifyDeploymentDone()
    {
        this.okCancelAction = OkCancelAction.END_TURN;
        okCancel.show("Deployment Phase completed.");
        okCancel.noCancel();
        pushDialog(okCancel);
    }

    public void notifyNoMoreAP()
    {
        this.okCancelAction = OkCancelAction.END_TURN;
        okCancel.show("No more Action Point left.");
        okCancel.noCancel();
        pushDialog(okCancel);
    }

    public void askExitBoard()
    {
        this.okCancelAction = OkCancelAction.EXIT_BOARD;
        okCancel.show("Do you want this unit to escape the battle field ?");
        pushDialog(okCancel);
    }

    public void askEndOfTurn()
    {
        this.okCancelAction = OkCancelAction.ABORT_TURN;
        okCancel.show("You still have Action Points left.\nEnd your Turn anyway ?");
        pushDialog(okCancel);
    }

    public void askEndDeployment()
    {
        this.okCancelAction = OkCancelAction.END_DEPLOYMENT;
        okCancel.show("Deployment unit count reached.\nEnd Deployment phase ?");
        pushDialog(okCancel);
    }

    public void engagementSummary(Engagement e)
    {
        engagement.show(e, Position.BOTTOM_CENTER, game.config.fxVolume);
        pushDialog(engagement);
    }

    public void victory(Player winner, Player loser)
    {
        stats.show(winner, loser, Position.MIDDLE_CENTER);
        pushDialog(stats);
    }
}
