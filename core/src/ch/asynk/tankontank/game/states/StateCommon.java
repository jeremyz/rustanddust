package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.State;
import ch.asynk.tankontank.game.State.StateType;

public abstract class StateCommon implements State
{
    protected static Ctrl ctrl;
    protected static Map map;
    protected static Pawn activePawn;
    protected static Pawn selectedPawn;

    protected static GridPoint2 selectedHex = new GridPoint2(-1, -1);
    protected static GridPoint2 downHex = new GridPoint2(-1, -1);
    protected static GridPoint2 upHex = new GridPoint2(-1, -1);
    protected static GridPoint2 from = new GridPoint2(-1, -1);
    protected static GridPoint2 to = new GridPoint2(-1, -1);

    protected boolean isEnemy;

    protected static StateType nextState = StateType.SELECT;

    protected StateCommon()
    {
    }

    public StateCommon(Ctrl ctrl, Map map)
    {
        this.ctrl = ctrl;
        this.map = map;
    }

    @Override
    public StateType getNextState()
    {
        return nextState;
    }

    @Override
    public void setNextState(StateType state)
    {
        nextState = state;
    }

    @Override
    public void abort()
    {
        goToNextState();
    }

    @Override
    public void done()
    {
        goToNextState();
    }

    public void clearAll()
    {
        from.set(-1, -1);
        to.set(-1, -1);
        selectedHex.set(-1, -1);
        selectedPawn = null;
        activePawn = null;
    }

    private void goToNextState()
    {
        ctrl.hud.hide();
        StateType next = nextState;
        nextState = StateType.SELECT;
        ctrl.setState(next, (next == StateType.SELECT));
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

    protected boolean hasPawn()
    {
        return (selectedPawn != null);
    }

    protected boolean sameHexes(GridPoint2 a, GridPoint2 b)
    {
        return ((a.x == b.x) && (a.y == b.y));
    }

    protected void selectHexAndPawn(GridPoint2 point)
    {
        selectedHex.set(point);
        selectedPawn = map.getTopPawnAt(selectedHex);
        map.selectHex(selectedHex, true);
        if (selectedPawn != null)
            isEnemy = ctrl.player().isEnemy(selectedPawn);
        else
            isEnemy = false;
        System.err.println("  select (" + selectedHex.x + ";" + selectedHex.y + ") "  + selectedPawn + (isEnemy ? " enemy " : " friend "));
    }

    protected void showPossibleTargetsMovesAssists(Pawn pawn)
    {
        if (ctrl.cfg.showMoves && pawn.canMove()) map.showPossibleMoves(true);
        if (ctrl.cfg.showTargets && pawn.canAttack()) map.showPossibleTargets(true);
        if (ctrl.cfg.showMoveAssists && pawn.canMove()) map.showMoveAssists(true);
    }
}
