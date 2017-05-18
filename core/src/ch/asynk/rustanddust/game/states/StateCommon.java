package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Ctrl.MsgType;
import ch.asynk.rustanddust.game.State;
import ch.asynk.rustanddust.game.Config;

public abstract class StateCommon implements State
{
    protected static Config cfg;
    protected static Ctrl ctrl;
    protected static Map map;

    protected static Hex selectedHex = null;
    protected static Hex to = null;

    private static Unit activeUnit;
    private static Unit selectedUnit;

    private void select(Unit u, boolean s)
    {
        if (u != null)
            u.select(s);
    }

    public void select(Unit unit)
    {
        select(selectedUnit, false);
        selectedUnit = unit;
        select(selectedUnit, true);
    }
    public Unit selectedUnit() { return selectedUnit; }

    public void activate(Unit unit)
    {
        select(activeUnit, false);
        activeUnit = unit;
        select(activeUnit, true);
    }
    public Unit activeUnit() { return activeUnit; }

    public static void set(RustAndDust game)
    {
        ctrl = game.ctrl;
        cfg = game.config;
        map = game.ctrl.map;
    }

    @Override
    public void touch(Hex hex) { }

    @Override
    public boolean processMsg(MsgType state, Object data)
    {
        return false;
    }
}
