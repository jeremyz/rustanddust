package ch.asynk.rustanddust.game;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.engine.gfx.Animation;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.ui.Bg;
import ch.asynk.rustanddust.ui.Msg;
import ch.asynk.rustanddust.ui.OkCancel;
import ch.asynk.rustanddust.ui.Widget;
import ch.asynk.rustanddust.game.hud.PlayerInfo;
import ch.asynk.rustanddust.game.hud.ActionButtons;
import ch.asynk.rustanddust.game.hud.StatisticsPanel;
import ch.asynk.rustanddust.game.hud.EngagementPanel;
import ch.asynk.rustanddust.game.hud.OptionsPanel;

import ch.asynk.rustanddust.RustAndDust;

public class Hud implements Disposable, Animation
{
    public static final float OFFSET = 10f;
    public static final float NOTIFY_DURATION = 2f;
    private static final float CLOSE_DELAY = 0.8f;

    private final RustAndDust game;
    private final Ctrl ctrl;

    public PlayerInfo playerInfo;
    public ActionButtons actionButtons;

    private float delay;
    private boolean delayOn;
    private Msg msg;
    private Bg optionsBtn;
    private OptionsPanel optionsPanel;
    private StatisticsPanel stats;
    private EngagementPanel engagement;
    private OkCancel okCancel;
    private ArrayDeque<Widget> dialogs = new ArrayDeque<Widget>();

    public enum OkCancelAction
    {
        EXIT_BOARD,
        ABORT_TURN,
        END_TURN,
        END_DEPLOYMENT,
        QUIT_BATTLE,
    }
    private OkCancelAction okCancelAction;

    public Hud(final RustAndDust game)
    {
        this.game = game;
        this.ctrl = game.ctrl;
        this.delayOn = false;

        TextureAtlas hudAtlas = game.factory.hudAtlas;
        playerInfo = new PlayerInfo(game);
        actionButtons = new ActionButtons(game);
        actionButtons.hide();
        msg = new Msg(game.font, game.bgPatch, 20f);
        okCancel = new OkCancel(game.font, game.bgPatch, game.factory.getHudRegion(game.factory.ACT_DONE), game.factory.getHudRegion(game.factory.ACT_ABORT));
        optionsBtn = new Bg(game.factory.getHudRegion(game.factory.ACT_OPTIONS));
        optionsPanel = new OptionsPanel(game);
        stats = new StatisticsPanel(game);
        engagement = new EngagementPanel(game);
    }

    @Override
    public void dispose()
    {
        playerInfo.dispose();
        actionButtons.dispose();
        msg.dispose();
        okCancel.dispose();
        optionsBtn.dispose();
        optionsPanel.dispose();
        stats.dispose();
        engagement.dispose();
    }

    public void resize(int left, int bottom, int width, int height)
    {
        Position.update(left, bottom, width, height);
        playerInfo.updatePosition();
        actionButtons.updatePosition();
        msg.updatePosition();
        okCancel.updatePosition();
        optionsBtn.setPosition(ctrl.battle.getHudPosition().verticalMirror().horizontalMirror());
        optionsPanel.updatePosition();
        stats.updatePosition();
        engagement.updatePosition();
    }

    public void update()
    {
        Position position = ctrl.battle.getHudPosition();
        playerInfo.update(ctrl.battle.getPlayer(), position);
        actionButtons.update(position.horizontalMirror());
        optionsBtn.setPosition(position.verticalMirror().horizontalMirror());
    }

    @Override
    public boolean animate(float delta)
    {
        if (delayOn) {
            delay -= delta;
            if (delay < 0f) {
                delayOver();
            }
        }
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
        optionsBtn.draw(batch);
        optionsPanel.draw(batch);
        stats.draw(batch);
        engagement.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        playerInfo.drawDebug(debugShapes);
        actionButtons.drawDebug(debugShapes);
        msg.drawDebug(debugShapes);
        okCancel.drawDebug(debugShapes);
        optionsBtn.drawDebug(debugShapes);
        optionsPanel.drawDebug(debugShapes);
        stats.drawDebug(debugShapes);
        engagement.drawDebug(debugShapes);
    }

    public void pushNotify(String s)
    {
        notify(s, NOTIFY_DURATION, Position.MIDDLE_CENTER, true);
    }

    public void notify(String s)
    {
        notify(s, NOTIFY_DURATION, Position.MIDDLE_CENTER, false);
    }

    public void notify(String s, Position pos)
    {
        notify(s, NOTIFY_DURATION, pos, false);
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

    public boolean hit(float x, float y, boolean isInAnimation)
    {
        if (optionsBtn.hit(x, y)) {
            toggleOptionsPanel();
            return true;
        }

        if (dialogs.size() > 0) {
            Widget dialog = dialogs.peek();
            if (dialog.hit(x, y)) {
                closeDialog();
                return true;
            }
            return false;
        }

        if (isInAnimation)
            return false;

        if (actionButtons.hit(x, y))
            return true;
        else if (playerInfo.hit(x, y))
            return true;

        return false;
    }

    private void closeDialog()
    {
        Widget dialog = dialogs.pop();
        dialog.visible = false;

        if (dialog == okCancel)
            closeOkCancel();
        else if (dialog == stats)
            ctrl.endGame();

        if (dialogs.size() > 0)
            dialogs.peek().visible = true;
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
            case QUIT_BATTLE:
                if (ok) {
                    closeDialog();
                    ctrl.endGame();
                }
                break;

        }
    }

    public void notifyAnimationsEnd()
    {
        delay = CLOSE_DELAY;
        delayOn = true;
    }

    private void delayOver()
    {
        delayOn = false;
        Widget dialog = dialogs.peek();
        if (dialog == engagement)
            closeDialog();
    }

    public boolean dialogActive()
    {
        return (dialogs.size() > 0);
    }

    private void pushDialog(Widget dialog)
    {
        ctrl.blockMap = true;
        if (dialogs.size() != 0)
            dialogs.peek().visible = false;
        dialogs.push(dialog);
    }

    private void toggleOptionsPanel()
    {
        if (dialogs.size() > 0) {
            if (dialogs.peek() == optionsPanel) {
                optionsPanel.close();
                closeDialog();
            }
        } else {
            optionsPanel.show();
            pushDialog(optionsPanel);
        }
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

    public void askQuitBattle()
    {
        this.okCancelAction = OkCancelAction.QUIT_BATTLE;
        okCancel.show("Quit this battle ?");
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
