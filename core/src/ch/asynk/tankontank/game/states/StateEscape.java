package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Zone;
import ch.asynk.tankontank.game.Unit;

public class StateEscape extends StateCommon
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
        ctrl.setAfterAnimationState(escape(activeUnit));
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

    private StateType escape(Unit unit)
    {
        // ctrl.hud.notify("Escape " + unit);

        Zone exitZone = ctrl.battle.getExitZone(unit);

        if (map.possiblePaths.size() == 1)
            map.possiblePaths.setExit(exitZone.orientation);
        else
            map.possiblePaths.build(map.getAdjTileAt(unit.getTile(), exitZone.orientation));
        map.possiblePaths.orientation = exitZone.orientation;

        unit.hideMoveable();
        map.hidePath(to);
        map.hidePossibleMoves();
        map.unselectHex(unit.getHex());

        map.leaveBoard(unit);
        ctrl.player.unitEscape(unit);

        if (map.moveUnit(activeUnit) > 0)
            return StateType.MOVE;
        return StateType.DONE;
    }
}
