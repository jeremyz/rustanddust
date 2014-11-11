package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.TileSet;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

public class StateEntry extends StateCommon
{
    private TileSet entryPoint;

    @Override
    public void enter(boolean fromSelect)
    {
        if (selectedHex != null)
            map.unselectHex(selectedHex);
        entryPoint = null;
        selectedHex = null;
        ctrl.hud.playerInfo.unitDock.show();
    }

    @Override
    public void leave(StateType nextState)
    {
        if (entryPoint != null)
            entryPoint.enable(Hex.AREA, false);
        ctrl.hud.playerInfo.unitDock.hide();
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Unit unit = ctrl.hud.playerInfo.getDockUnit();
        if ((unit != null) && (unit != activeUnit))
            changeUnit(unit);
        else if ((entryPoint != null) && upHex.isEmpty() && entryPoint.contains(upHex))
            unitEnter(activeUnit);
        else
            ctrl.setState(StateType.SELECT);
    }

    @Override
    public void abort()
    {
        super.abort();
    }

    @Override
    public void done()
    {
        super.done();
    }

    private void changeUnit(Unit unit)
    {
        activeUnit = unit;
        if (entryPoint != null) entryPoint.enable(Hex.AREA, false);
        entryPoint = ctrl.battle.getEntryPoint(activeUnit);
        entryPoint.enable(Hex.AREA, true);
        ctrl.hud.actionButtons.show(((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
    }

    private void unitEnter(Unit unit)
    {
        selectedUnit = unit;
        selectedHex = upHex;
        map.selectHex(selectedHex);
        entryPoint.enable(Hex.AREA, false);
        ctrl.player.unitEntry(unit);
        map.enterBoard(unit, upHex, ctrl.battle.getEntryOrientation(ctrl.player));
        if (unit.getMovementPoints() > 0)
            ctrl.setState(StateType.MOVE, true);
        else
            ctrl.setState(StateType.ROTATE, true);
    }
}
