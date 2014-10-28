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
            map.possibleTargets.hide();
            map.buildPossibleTargets(selectedUnit, ctrl.opponent.unitIterator());
            map.possibleTargets.show();
            if (to != null) {
                // quick fire -> replay touchUp
                upHex = to;
                touchUp();
            }
            selectedUnit.showAttack(true);
            map.selectHex(selectedHex, true);
        } else
            System.err.println("should not happen");
    }

    @Override
    public void leave(StateType nextState)
    {
        selectedUnit.showAttack(false);
        map.attackAssists.enable(Unit.ATTACK, false);
        map.attackAssists.enable(Unit.ATTACK_ASSIST, false);
        map.possibleTargets.hide();
        map.selectHex(selectedHex, false);
        if (to != null)
            map.selectHex(to, false);
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
            map.possibleTargets.hide();
            to = upHex;
            activeUnit = unit;
            activeUnit.showTarget(true);
            map.buildAttackAssists(selectedUnit, activeUnit, ctrl.player.unitIterator());
            map.attackAssists.show();
            ctrl.hud.show(false, false, false, true, true, ctrl.cfg.canCancel);
        }

        if ((activeUnit != null) && map.attackAssists.contains(unit)) {
            if (map.toggleAttackAssist(unit)) {
                unit.showAttack(true);
                unit.showAttackAssist(false);
            } else {
                unit.showAttack(false);
                unit.showAttackAssist(true);
            }
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
        if (map.attackPawn(selectedUnit, activeUnit, d1 + d2))
            ctrl.player.casualty(activeUnit);
        activeUnit.showTarget(true);
        ctrl.setState(StateType.ANIMATION);

        super.done();
    }
}
