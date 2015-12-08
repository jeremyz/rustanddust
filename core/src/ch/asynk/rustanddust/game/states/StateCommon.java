package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.State;
import ch.asynk.rustanddust.game.Config;

import ch.asynk.rustanddust.RustAndDust;

public abstract class StateCommon implements State
{
    protected static Config cfg;
    protected static Ctrl ctrl;
    protected static Map map;

    protected static Hex selectedHex = null;
    protected static Hex downHex = null;
    protected static Hex upHex = null;
    protected static Hex to = null;

    protected boolean isEnemy;
    protected static Unit activeUnit;
    protected static Unit selectedUnit;

    public static void set(RustAndDust game)
    {
        ctrl = game.ctrl;
        cfg = game.config;
        map = game.ctrl.map;
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
        if (cfg.showMoves && unit.canMove()) map.movesShow();
        if (cfg.showTargets && unit.canEngage()) map.unitsTargetShow();
        if (cfg.showMoveAssists && unit.canMove()) map.unitsMoveableShow();
        unit.enableOverlay(Unit.MOVE, false);
    }

    protected void hidePossibilities()
    {
        map.movesHide();
        map.unitsTargetHide();
        map.unitsMoveableHide();
    }
}
