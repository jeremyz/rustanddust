package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.RustAndDust;

public class StateWithdraw extends StateCommon
{
    @Override
    public void enterFrom(StateType prevState)
    {
        ctrl.hud.askExitBoard();
    }

    @Override
    public void leaveFor(StateType nextState)
    {
    }

    @Override
    public StateType abort()
    {
        return StateType.MOVE;
    }

    @Override
    public StateType execute()
    {
        if (activeUnit == null)
            activeUnit = selectedUnit;

        ctrl.setAfterAnimationState(withdraw(activeUnit));
        return StateType.ANIMATION;
    }

    private StateType withdraw(Unit unit)
    {
        Hex hex = unit.getHex();

        // rotation
        if (map.pathsTo() == null)
            map.pathsBuild(hex);

        Hex exitHex = (Hex) map.pathsTo();
        if (!unit.exitZone.contains(exitHex))
            throw new RuntimeException(String.format("%s not in exitZone", exitHex));

        if (map.pathsChooseExit(unit.exitZone.orientation) > 1)
            RustAndDust.debug("ERROR: Withdraw pathsSize() == " + map.pathsSize());

        unit.hideActiveable();
        if (to != null)
            map.pathHide(to);
        map.movesHide();
        map.hexUnselect(hex);

        if (map.exitBoard(unit)) {
            if (map.unitsActivableSize() > 0)
                return StateType.MOVE;
        } else
            RustAndDust.debug("exit failed");
        return StateType.DONE;
    }
}
