package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.GameCtrl;
import ch.asynk.tankontank.game.GameState;

public abstract class GameStateCommon implements GameState
{
    protected static GameCtrl ctrl;
    protected static Map map;
    protected static Pawn pawn;
    protected static GridPoint2 hex = new GridPoint2(0, 0);

    protected static GridPoint2 downHex = new GridPoint2(-1, -1);
    protected static GridPoint2 upHex = new GridPoint2(-1, -1);

    protected static GridPoint2 from = new GridPoint2(-1, -1);
    protected static GridPoint2 to = new GridPoint2(-1, -1);

    protected GameStateCommon()
    {
    }

    public GameStateCommon(GameCtrl ctrl, Map map)
    {
        this.ctrl = ctrl;
        this.map = map;
    }

    @Override
    public void abort()
    {
        unselectHex(hex);
        hex.set(0, 0);
        pawn = null;
        ctrl.hud.hide();
        ctrl.setState(State.VIEW);
    }

    protected static boolean hexInMap(GridPoint2 hex)
    {
        if (hex.x == -1) return false;
        return !map.isOffMap(hex);
    }

    public boolean downInMap(float x, float y)
    {
        map.getHexAt(downHex, x, y);
        return hexInMap(downHex);
    }

    public boolean upInMap(float x, float y)
    {
        map.getHexAt(upHex, x, y);
        return hexInMap(upHex);
    }

    protected void setHexAndPawn(GridPoint2 point)
    {
        hex.set(point.x, point.y);
        // TODO : is an enemy or not ?
        pawn = map.getTopPawnAt(hex);
    }

    protected boolean hasPawn()
    {
        return (pawn != null);
    }

    protected void unselectHex(GridPoint2 hex)
    {
        map.enableOverlayOn(hex, Hex.SELECT, false);
    }

    protected void selectHex(GridPoint2 hex)
    {
        map.enableOverlayOn(hex, Hex.SELECT, true);
    }

    protected void reselectHex()
    {
        unselectHex(hex);
        setHexAndPawn(downHex);
        selectHex(hex);
    }

    protected boolean sameHexes(GridPoint2 a, GridPoint2 b)
    {
        return ((a.x == b.x) && (a.y == b.y));
    }

    protected void showPossibleTargetsMovesAssists(boolean enable)
    {
        if (ctrl.cfg.showMoves) map.enablePossibleMoves(enable);
        if (ctrl.cfg.showTargets) map.enablePossibleTargets(enable);
        if (ctrl.cfg.showMoveAssists) map.enableMoveAssists(enable);
    }
}
