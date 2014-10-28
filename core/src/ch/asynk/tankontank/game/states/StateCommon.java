package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
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
    protected static Unit activeUnit;
    protected static Unit selectedUnit;

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
        selectedUnit = null;
        activeUnit = null;
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
        downHex = map.getHexAt(x, y);
        if (downHex == null) return false;
        return !downHex.isOffMap();
    }

    public boolean upInMap(float x, float y)
    {
        upHex = map.getHexAt(x, y);
        if (upHex == null) return false;
        return !upHex.isOffMap();
    }

    protected boolean hasUnit()
    {
        return (selectedUnit != null);
    }

    protected void selectHexAndUnit(Hex hex)
    {
        selectedHex = hex;
        selectedUnit = selectedHex.getUnit();
        map.selectHex(selectedHex, true);
        if (selectedUnit != null)
            isEnemy = ctrl.player.isEnemy(selectedUnit);
        else
            isEnemy = false;
        System.err.println("  select " + selectedHex + selectedUnit + (isEnemy ? " enemy " : " friend "));
    }

    protected void showPossibleTargetsMovesAssists(Unit pawn)
    {
        if (ctrl.cfg.showMoves && pawn.canMove()) map.possibleMoves.show();
        if (ctrl.cfg.showTargets && pawn.canAttack()) map.possibleTargets.show();
        if (ctrl.cfg.showMoveAssists && pawn.canMove()) map.moveAssists.show();
    }

    protected void hidePossibleTargetsMovesAssists()
    {
        map.possibleMoves.hide();
        map.possibleTargets.hide();
        map.moveAssists.hide();
    }
}
