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
    protected static Pawn activePawn;
    protected static Pawn selectedPawn;

    protected static GridPoint2 selectedHex = new GridPoint2(-1, -1);
    protected static GridPoint2 downHex = new GridPoint2(-1, -1);
    protected static GridPoint2 upHex = new GridPoint2(-1, -1);
    protected static GridPoint2 from = new GridPoint2(-1, -1);
    protected static GridPoint2 to = new GridPoint2(-1, -1);

    protected static GameState.State nextState = GameState.State.SELECT;

    protected GameStateCommon()
    {
    }

    public GameStateCommon(GameCtrl ctrl, Map map)
    {
        this.ctrl = ctrl;
        this.map = map;
    }

    @Override
    public GameState.State getNextState()
    {
        return nextState;
    }

    @Override
    public void setNextState(GameState.State state)
    {
        nextState = state;
    }

    @Override
    public void abort()
    {
        clearAndGoToSelect();
    }

    @Override
    public void done()
    {
        clearAndGoToSelect();
    }

    private void clearAndGoToSelect()
    {
        unselectHex(selectedHex);
        selectedHex.set(-1, -1);
        selectedPawn = null;
        ctrl.hud.hide();
        ctrl.setState(State.SELECT);
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
        selectedHex.set(point);
        // TODO : is an enemy or not ?
        selectedPawn = map.getTopPawnAt(selectedHex);
        System.err.println("setHexAndPawn : " + selectedHex.x + ";" + selectedHex.y + " " + selectedPawn);
    }

    protected boolean hasPawn()
    {
        return (selectedPawn != null);
    }

    protected void unselectHex(GridPoint2 hex)
    {
        map.enableOverlayOn(hex, Hex.SELECT, false);
    }

    protected void selectHex(GridPoint2 hex)
    {
        map.enableOverlayOn(hex, Hex.SELECT, true);
    }

    protected void showAssist(GridPoint2 hex, boolean enable)
    {
        map.enableOverlayOn(hex, Hex.ASSIST, enable);
    }

    protected void reselectHex()
    {
        if (selectedHex.x != -1) unselectHex(selectedHex);
        setHexAndPawn(downHex);
        selectHex(selectedHex);
    }

    protected boolean sameHexes(GridPoint2 a, GridPoint2 b)
    {
        return ((a.x == b.x) && (a.y == b.y));
    }

    protected void hidePossibleTargetsMovesAssists()
    {
        map.showPossibleMoves(false);
        map.showPossibleTargets(false);
        map.showMoveAssists(false);
    }

    protected void showPossibleTargetsMovesAssists(Pawn pawn)
    {
        if (ctrl.cfg.showMoves && pawn.canMove()) map.showPossibleMoves(true);
        if (ctrl.cfg.showTargets && pawn.canAttack()) map.showPossibleTargets(true);
        if (ctrl.cfg.showMoveAssists && pawn.canMove()) map.showMoveAssists(true);
    }
}
