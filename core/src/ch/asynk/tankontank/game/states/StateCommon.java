package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.State;

import ch.asynk.tankontank.TankOnTank;

public abstract class StateCommon implements State
{
    protected static Ctrl ctrl;
    protected static Map map;

    protected static Hex selectedHex = null;
    protected static Hex downHex = null;
    protected static Hex upHex = null;
    protected static Hex to = null;

    protected boolean isEnemy;
    protected static Unit activeUnit;
    protected static Unit selectedUnit;

    protected StateCommon()
    {
    }

    public StateCommon(Ctrl ctrl, Map map)
    {
        this.ctrl = ctrl;
        this.map = map;
    }

    @Override
    public boolean downInMap(float x, float y)
    {
        downHex = map.getHexAt(x, y);
        return (downHex != null);
    }

    @Override
    public boolean upInMap(float x, float y)
    {
        upHex = map.getHexAt(x, y);
        return (upHex != null);
    }

    protected boolean hasUnit()
    {
        return (selectedUnit != null);
    }

    protected void showPossibilities(Unit unit)
    {
        if (ctrl.cfg.showMoves && unit.canMove()) map.showPossibleMoves();
        if (ctrl.cfg.showTargets && unit.canEngage()) map.showPossibleTargets();
        if (ctrl.cfg.showMoveAssists && unit.canMove()) map.showMoveableUnits();
        unit.enableOverlay(Unit.MOVE, false);
    }

    protected void hidePossibilities()
    {
        map.hidePossibleMoves();
        map.hidePossibleTargets();
        map.hideMoveableUnits();
    }
}
