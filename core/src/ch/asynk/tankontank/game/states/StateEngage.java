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
        ctrl.hud.actionButtons.show(Buttons.ENGAGE.b | ((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
        ctrl.hud.actionButtons.setOn(Buttons.ENGAGE);

        // activeUnit is the target
        if (prevState == StateType.SELECT) {
            activeUnit = null;
            // use selectedHex and selectedUnit
            map.hidePossibleTargets();
            map.collectPossibleTargets(selectedUnit, ctrl.opponent.unitIterator());
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
        map.activatedPawns.clear();
        return StateType.ABORT;
    }

    @Override
    public StateType done()
    {
        StateType nextState = StateType.DONE;
        if (map.engagePawn(selectedUnit, activeUnit)) {
            ctrl.player.wonEngagementCount += 1;
            ctrl.hud.notify(selectedUnit.engagement.calculus + " : " + activeUnit + " is destroyed");
            ctrl.opponent.casualty(activeUnit);
            if (map.breakPawns.size() > 0) {
                nextState = StateType.BREAK;
            }
        } else {
            ctrl.player.lostEngagementCount += 1;
            ctrl.hud.notify(selectedUnit.engagement.calculus + " : failure");
        }

        activeUnit.showTarget();
        ctrl.setAnimationCount(1);
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
            ctrl.hud.notify("Engage " + unit);
            map.hidePossibleTargets();
            to = upHex;
            activeUnit = unit;
            activeUnit.showTarget();
            map.collectAttackAssists(selectedUnit, activeUnit, ctrl.player.unitIterator());
            map.showAttackAssists();
            ctrl.hud.actionButtons.show(Buttons.ENGAGE.b | Buttons.DONE.b | ((ctrl.cfg.canCancel) ? Buttons.ABORT.b : 0));
        }

        if ((activeUnit != null) && map.engagementAssists.contains(unit)) {
            if(map.toggleAttackAssist(unit))
                ctrl.hud.notify(unit + " will fire");
            else
                ctrl.hud.notify(unit + " wont fire");
        }
    }
}
