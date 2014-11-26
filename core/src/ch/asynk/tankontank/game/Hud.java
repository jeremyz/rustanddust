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
import ch.asynk.tankontank.game.hud.Engagement;
import ch.asynk.tankontank.game.hud.Widget;

import ch.asynk.tankontank.TankOnTank;

public class Hud implements Disposable
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
    private DialogAction dialogAction;
    private Widget[] dialogs;

    enum DialogAction
    {
        EXIT_BOARD,
        ABORT_TURN,
        END_TURN,
        END_DEPLOYMENT,
        END_GAME,
        END_ENGAGEMENT,
        NONE
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
        engagement = new Engagement(font, atlas.findRegion("disabled"), atlas, 10f);
        dialogs = new Widget[] { okCancel, stats, engagement};
        dialogAction = DialogAction.NONE;
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
        engagement.draw(batch);
        stats.draw(batch);
    }

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

        for (Widget w : dialogs) {
            if (w.visible) {
                if (w.hit(x, y)) {
                    hit = w;
                    break;
                }
                return false;
            }
        }
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

        if (hit == actionButtons) {
            actionButtons.touchUp(x, y);
        }
        else if (hit == playerInfo) {
            playerInfo.touchUp(x, y);
        }
        else if (hit == okCancel) {
            if (okCancel.hit(x, y))
                closeDialog();
        }
        else if (hit == stats) {
            if (stats.hit(x, y))
                closeDialog();
        }
        else if (hit == engagement) {
            if (engagement.hit(x, y))
                closeDialog();
        }

        hit = null;

        return true;
    }

    private void closeDialog()
    {
        boolean ok = okCancel.ok;
        switch(dialogAction)
        {
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
            case END_GAME:
                stats.visible = false;
                ctrl.endGame();
                break;
            case END_ENGAGEMENT:
                engagement.visible = false;
                break;
            case NONE:
            default:
                break;
        }
        okCancel.visible = false;
        ctrl.blockMap = false;
        dialogAction = DialogAction.NONE;
    }

    private void setDialogAction(DialogAction action)
    {
        if (dialogAction != DialogAction.NONE)
            System.err.println(":::: BUG ::::  dialogAction is already set to " + dialogAction);
        dialogAction = action;
    }

    public void notifyEndOfTurn()
    {
        ctrl.blockMap = true;
        setDialogAction(DialogAction.END_TURN);
        okCancel.show("You have no more Action Points left.", Position.MIDDLE_CENTER, false);
    }

    public void askExitBoard()
    {
        ctrl.blockMap = true;
        setDialogAction(DialogAction.EXIT_BOARD);
        okCancel.show("Do you want this unit to escape the battle fierd ?", Position.MIDDLE_CENTER);
    }

    public void askEndOfTurn()
    {
        ctrl.blockMap = true;
        setDialogAction(DialogAction.ABORT_TURN);
        okCancel.show("You still have Action Points left.\nEnd your Turn anyway ?", Position.MIDDLE_CENTER);
    }

    public void askEndDeployment()
    {
        ctrl.blockMap = true;
        setDialogAction(DialogAction.END_DEPLOYMENT);
        okCancel.show("Deployment unit count reached.\nEnd Deployment phase ?", Position.MIDDLE_CENTER);
    }

    public void engagementSummary(int d1, int d2, int cnt, int flk, int def, int tdf, int wdf, String msg)
    {
        ctrl.blockMap = true;
        setDialogAction(DialogAction.END_ENGAGEMENT);
        engagement.show(d1, d2, cnt, flk, def, tdf, wdf, msg, Position.BOTTOM_CENTER);
    }

    public void victory(Player winner, Player loser)
    {
        ctrl.blockMap = true;
        setDialogAction(DialogAction.END_GAME);
        stats.show(winner, loser, Position.MIDDLE_CENTER);
    }
}
