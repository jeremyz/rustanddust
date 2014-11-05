package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.TileSet;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Hex;

public class StateEntry extends StateCommon
{
    private float x;
    private float y;
    private float z;
    private TileSet entryPoint;

    @Override
    public void enter(boolean fromSelect)
    {
        activeUnit = ctrl.hud.getDockUnit();
        entryPoint = ctrl.battle.getEntryPoint(activeUnit);
        entryPoint.enable(Hex.AREA, true);
        ctrl.hud.show(false, false, false, false, false, ctrl.cfg.canCancel);
        x = activeUnit.getPosition().x;
        y = activeUnit.getPosition().x;
        z = activeUnit.getPosition().y;
    }

    @Override
    public void leave(StateType nextState)
    {
        entryPoint.enable(Hex.AREA, false);
        ctrl.hud.hideUnitDock();
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        if (upHex.isEmpty() && entryPoint.contains(upHex)) {
            selectedUnit = activeUnit;
            selectedHex = upHex;
            map.selectHex(selectedHex);
            entryPoint.enable(Hex.AREA, false);
            ctrl.player.unitEntry(activeUnit);
            map.enterBoard(activeUnit, upHex, ctrl.battle.getEntryOrientation(ctrl.player));
            if (activeUnit.getMovementPoints() > 0)
                ctrl.setState(StateType.MOVE, true);
            else
                ctrl.setState(StateType.ROTATE, true);
        }
    }

    @Override
    public void abort()
    {
        activeUnit.setPosition(x, y, z);
        super.abort();
    }

    @Override
    public void done()
    {
        super.done();
    }
}
