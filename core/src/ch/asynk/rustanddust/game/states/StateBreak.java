package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateBreak extends StateCommon
{
    private Orientation o = Orientation.KEEP;

    @Override
    public void enterFrom(StateType prevState)
    {
        activeUnit = null;
        ctrl.hud.actionButtons.show(Buttons.DONE.b);
        ctrl.hud.notify("Break Through possible");
        map.unitsActivableShow();
    }

    @Override
    public void leaveFor(StateType nextState)
    {
        map.unitsActivableHide();
        map.hexMoveHide(to);
        map.hexDirectionsHide(to);
        if (activeUnit != null) map.hexMoveHide(activeUnit.getHex());
    }

    @Override
    public StateType abort()
    {
        return StateType.ABORT;
    }

    @Override
    public StateType execute()
    {
        return StateType.DONE;
    }

    @Override
    public void touch(Hex hex)
    {
        // TODO : cancel preview move before showing rotation
        if (activeUnit == null) {
            Unit unit = hex.getUnit();
            if (map.unitsActivableContains(unit)) {
                activeUnit = unit;
                map.hexMoveShow(hex);
                map.hexMoveShow(to);
                map.hexDirectionsShow(to);
                map.unitsActivableHide();
            }
        } else {
            o = Orientation.fromAdj(to, hex);

            if (o == Orientation.KEEP) return;

            doRotation(o);
            ctrl.setState(StateType.ANIMATION);
        }
    }

    private void doRotation(Orientation o)
    {
        if (activeUnit == null) return;

        map.pathsInit(activeUnit);
        map.pathsBuild(to);
        map.pathsChooseShortest();
        map.pathsSetOrientation(o);
        map.moveUnit(activeUnit);
        ctrl.setAfterAnimationState(StateType.DONE);
    }
}
