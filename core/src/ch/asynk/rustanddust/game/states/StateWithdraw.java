package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Zone;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;

public class StateWithdraw extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        ctrl.hud.askExitBoard();
    }

    @Override
    public void leave(StateType nextState)
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

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
    }

    private StateType withdraw(Unit unit)
    {
        Zone exitZone = ctrl.battle.getExitZone(unit);
        Hex hex = unit.getHex();

        // rotation
        if (map.pathsTo() == null)
            map.pathsBuild(hex);

        Hex exitHex = (Hex) map.pathsTo();
        if (!exitZone.contains(exitHex))
            throw new RuntimeException(String.format("%s not in exitZone", exitHex));

        map.pathsSetExit(exitZone.orientation);

        unit.hideActiveable();
        if (to != null)
            map.pathHide(to);
        map.movesHide();
        map.hexUnselect(hex);

        if (map.exitBoard(unit) > 0)
            return StateType.MOVE;
        return StateType.DONE;
    }
}
