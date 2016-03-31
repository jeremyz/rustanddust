package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;

public class StateEngage extends StateCommon
{
    @Override
    public void enterFrom(StateType prevState)
    {
        if (prevState == StateType.SELECT) {
            // activeUnit will be target
            activeUnit = null;
            if (to != null) {
                // quick fire -> replay touchUp
                touch(to);
            }
            selectedUnit.showAttack();
            map.hexSelect(selectedHex);
        }
    }

    @Override
    public void leaveFor(StateType nextState)
    {
        selectedUnit.hideAttack();
        map.unitsAssistHide();
        map.unitsTargetHide();
        map.hexUnselect(selectedHex);
        if (to != null)
            map.hexUnselect(to);
    }

    @Override
    public StateType abort()
    {
        map.unitsActivatedClear();
        return StateType.ABORT;
    }

    @Override
    public StateType execute()
    {
        map.unitsAssistHide();
        StateType nextState = StateType.DONE;
        if (map.engageUnit(selectedUnit, activeUnit)) {
            ctrl.battle.getPlayer().engagementWon += 1;
            ctrl.battle.getOpponent().casualty(activeUnit);
            if (map.unitsActivableSize() > 0) {
                nextState = StateType.BREAK;
            }
        } else {
            ctrl.battle.getPlayer().engagementLost += 1;
        }

        activeUnit.showTarget();
        ctrl.setAfterAnimationState(nextState);
        return StateType.ANIMATION;
    }

    @Override
    public void touch(Hex hex)
    {
        Unit unit = hex.getUnit();

        // activeUnit is the target, selectedTarget is the engagement leader
        if (unit == selectedUnit) {
            ctrl.post(StateType.ABORT);
        } else if ((activeUnit == null) && map.unitsTargetContains(unit)) {
            // ctrl.hud.notify("Engage " + unit);
            map.unitsTargetHide();
            to = hex;
            activeUnit = unit;
            activeUnit.showTarget();
            map.collectAssists(selectedUnit, activeUnit, ctrl.battle.getPlayer().units);
            map.unitsAssistShow();
        }
        else if (unit == activeUnit) {
            ctrl.post(StateType.DONE);
        }
        else if ((activeUnit != null) && map.unitsActivableContains(unit)) {
            map.toggleAssist(unit);
            // if (map.toggleAssist(unit))
            //     ctrl.hud.notify(unit + " will fire");
            // else
            //     ctrl.hud.notify(unit + " wont fire");
        }
    }
}
