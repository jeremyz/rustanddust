package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.State;
import ch.asynk.tankontank.game.State.StateType;

public abstract class StateCommon implements State
{
    protected static Ctrl ctrl;
    protected static Map map;

    protected static Hex selectedHex = null;
    protected static Hex downHex = null;
    protected static Hex upHex = null;
    protected static Hex from = null;
    protected static Hex to = null;

    protected boolean isEnemy;
    protected static Pawn activePawn;
    protected static Pawn selectedPawn;

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
        from = null;
        to = null;
        selectedHex = null;
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

    public boolean downInMap(float x, float y)
    {
        // FIXME
        downHex = (Hex) map.getTileAt(x, y);
        if (downHex == null) return false;
        return !downHex.isOffMap();
    }

    public boolean upInMap(float x, float y)
    {
        // FIXME
        upHex = (Hex) map.getTileAt(x, y);
        if (upHex == null) return false;
        return !upHex.isOffMap();
    }

    protected boolean hasPawn()
    {
        return (selectedPawn != null);
    }

    protected void selectHexAndPawn(Hex hex)
    {
        selectedHex = hex;
        selectedPawn = selectedHex.getTopPawn();
        map.selectHex(selectedHex, true);
        if (selectedPawn != null)
            isEnemy = ctrl.player.isEnemy(selectedPawn);
        else
            isEnemy = false;
        System.err.println("  select " + selectedHex + selectedPawn + (isEnemy ? " enemy " : " friend "));
    }

    protected void showPossibleTargetsMovesAssists(Pawn pawn)
    {
        if (ctrl.cfg.showMoves && pawn.canMove()) map.showPossibleMoves(true);
        if (ctrl.cfg.showTargets && pawn.canAttack()) map.showPossibleTargets(true);
        if (ctrl.cfg.showMoveAssists && pawn.canMove()) map.showMoveAssists(true);
    }
}
