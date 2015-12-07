package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Map.UnitType;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

import ch.asynk.rustanddust.RustAndDust;

public class StateEngage extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        map.unitsClear(UnitType.TARGETS);
        ctrl.hud.actionButtons.show(ctrl.cfg.canCancel ? Buttons.ABORT.b : 0);

        // activeUnit is the target
        if (prevState == StateType.SELECT) {
            activeUnit = null;
            // use selectedHex and selectedUnit
            map.unitsHide(UnitType.TARGETS);
            map.collectPossibleTargets(selectedUnit, ctrl.opponent.units);
            map.unitsShow(UnitType.TARGETS);
            if (to != null) {
                // quick fire -> replay touchUp
                upHex = to;
                touchUp();
            }
            selectedUnit.showAttack();
            map.selectHex(selectedHex);
        } else
            RustAndDust.debug("should not happen");
    }

    @Override
    public void leave(StateType nextState)
    {
        selectedUnit.hideAttack();
        map.unitsHide(UnitType.ASSISTS);
        map.unitsHide(UnitType.TARGETS);
        map.unselectHex(selectedHex);
        if (to != null)
            map.unselectHex(to);
    }

    @Override
    public StateType abort()
    {
        map.unitsClear(UnitType.ACTIVATED);
        return StateType.ABORT;
    }

    @Override
    public StateType execute()
    {
        StateType nextState = StateType.DONE;
        if (map.engageUnit(selectedUnit, activeUnit)) {
            ctrl.player.wonEngagementCount += 1;
            ctrl.opponent.casualty(activeUnit);
            if (map.unitsSize(UnitType.BREAK_THROUGH) > 0) {
                nextState = StateType.BREAK;
            }
        } else {
            ctrl.player.lostEngagementCount += 1;
        }

        activeUnit.showTarget();
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

        // activeUnit is the target, selectedTarget is the engagement leader
        if (unit == selectedUnit) {
            ctrl.setState(StateType.ABORT);
        } else if ((activeUnit == null) && map.unitsContains(UnitType.TARGETS, unit)) {
            // ctrl.hud.notify("Engage " + unit);
            map.unitsHide(UnitType.TARGETS);
            to = upHex;
            activeUnit = unit;
            activeUnit.showTarget();
            map.collectAttackAssists(selectedUnit, activeUnit, ctrl.player.units);
            map.unitsShow(UnitType.ASSISTS);
            ctrl.hud.actionButtons.show((ctrl.cfg.mustValidate ? Buttons.DONE.b : 0) | (ctrl.cfg.canCancel ? Buttons.ABORT.b : 0));
        }
        else if (unit == activeUnit) {
            ctrl.setState(StateType.DONE);
        }
        else if ((activeUnit != null) && map.unitsContains(UnitType.ASSISTS, unit)) {
            map.toggleAttackAssist(unit);
            // if(map.toggleAttackAssist(unit))
            //     ctrl.hud.notify(unit + " will fire");
            // else
            //     ctrl.hud.notify(unit + " wont fire");
        }
    }
}
