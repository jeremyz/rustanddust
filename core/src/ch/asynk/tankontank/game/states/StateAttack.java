package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.State.StateType;

public class StateAttack extends StateCommon
{
    @Override
    public void enter(boolean fromSelect)
    {
        map.possibleTargets.clear();
        ctrl.hud.show(false, false, false, true, false, ctrl.cfg.canCancel);
        ctrl.hud.attackBtn.setOn();

        // activeUnit is the target
        if (fromSelect) {
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
            System.err.println("should not happen");
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
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Unit unit = upHex.getUnit();

        // activeUnit is the target
        if ((activeUnit == null) && map.possibleTargets.contains(unit)) {
            map.hidePossibleTargets();
            to = upHex;
            activeUnit = unit;
            activeUnit.showTarget();
            map.collectAttackAssists(selectedUnit, activeUnit, ctrl.player.unitIterator());
            map.showAttackAssists();
            ctrl.hud.show(false, false, false, true, true, ctrl.cfg.canCancel);
        }

        if ((activeUnit != null) && map.attackAssists.contains(unit)) {
            map.toggleAttackAssist(unit);
        }
    }

    @Override
    public void abort()
    {
        map.activatedPawns.clear();
        super.abort();
    }

    @Override
    public void done()
    {
        int d1 = ctrl.player.d6();
        int d2 = ctrl.player.d6();
        System.err.print("  attack (" + selectedHex.getCol() + ";" + selectedHex.getRow() + ") -> (" + to.getCol() + ";" + to.getRow() + ") : 2D6 -> (" + d1 + " + " + d2 + ")");
        if (map.attackPawn(selectedUnit, activeUnit, d1 + d2)) {
            ctrl.opponent.casualty(activeUnit);
            if (map.breakPawns.size() > 0)
                setNextState(StateType.BREAK);
        }


        activeUnit.showTarget();
        ctrl.setState(StateType.ANIMATION);

        super.done();
    }
}
