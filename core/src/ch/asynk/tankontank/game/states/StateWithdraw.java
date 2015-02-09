package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Zone;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;

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

        ctrl.setAnimationCount(1);
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
        if (map.pathBuilder.to == null)
            map.pathBuilder.build(hex);

        Hex exitHex = (Hex) map.pathBuilder.to;
        if (!exitZone.contains(exitHex))
            throw new RuntimeException(String.format("%s not in exitZone", exitHex));

        map.pathBuilder.setExit(exitZone.orientation);

        unit.hideMoveable();
        if (to != null)
            map.hidePath(to);
        map.hidePossibleMoves();
        map.unselectHex(hex);

        if (map.exitBoard(unit) > 0)
            return StateType.MOVE;
        return StateType.DONE;
    }
}