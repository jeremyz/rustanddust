package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.hud.ActionButtons.Buttons;

import ch.asynk.tankontank.TankOnTank;

public class StateEngage extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        map.possibleTargets.clear();
        ctrl.hud.actionButtons.show(ctrl.cfg.canCancel ? Buttons.ABORT.b : 0);

        // activeUnit is the target
        if (prevState == StateType.SELECT) {
            activeUnit = null;
            // use selectedHex and selectedUnit
            map.hidePossibleTargets();
            map.collectPossibleTargets(selectedUnit, ctrl.opponent.units);
            map.showPossibleTargets();
            if (to != null) {
                // quick fire -> replay touchUp
                upHex = to;
                touchUp();
            }
            selectedUnit.showAttack();
            map.selectHex(selectedHex);
        } else
            TankOnTank.debug("should not happen");
    }

    @Override
    public void leave(StateType nextState)
    {
        selectedUnit.hideAttack();
        map.hideAttackAssists();
        map.hidePossibleTargets();
        map.unselectHex(selectedHex);
        if (to != null)
            map.unselectHex(to);
    }

    @Override
    public StateType abort()
    {
        map.activatedUnits.clear();
        return StateType.ABORT;
    }

    @Override
    public StateType execute()
    {
        StateType nextState = StateType.DONE;
        if (map.engageUnit(selectedUnit, activeUnit)) {
            ctrl.player.wonEngagementCount += 1;
            ctrl.opponent.casualty(activeUnit);
            if (map.breakUnits.size() > 0) {
                nextState = StateType.BREAK;
            }
        } else {
            ctrl.player.lostEngagementCount += 1;
        }

        activeUnit.showTarget();
        ctrl.setAnimationCount(map.activatedUnits.size());
        ctrl.setAfterAnimationState(nextState);
        return StateType.ANIMATION;
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Unit unit = upHex.getUnit();

        // activeUnit is the target
        if ((activeUnit == null) && map.possibleTargets.contains(unit)) {
            // ctrl.hud.notify("Engage " + unit);
            map.hidePossibleTargets();
            to = upHex;
            activeUnit = unit;
            activeUnit.showTarget();
            map.collectAttackAssists(selectedUnit, activeUnit, ctrl.player.units);
            map.showAttackAssists();
            ctrl.hud.actionButtons.show((ctrl.cfg.mustValidate ? Buttons.DONE.b : 0) | (ctrl.cfg.canCancel ? Buttons.ABORT.b : 0));
        }
        else if (unit == activeUnit) {
            ctrl.setState(StateType.DONE);
        }
        else if ((activeUnit != null) && map.engagementAssists.contains(unit)) {
            map.toggleAttackAssist(unit);
            // if(map.toggleAttackAssist(unit))
            //     ctrl.hud.notify(unit + " will fire");
            // else
            //     ctrl.hud.notify(unit + " wont fire");
        }
    }
}
