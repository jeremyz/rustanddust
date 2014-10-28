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

        if (fromSelect) {
            activeUnit = null;
            // use selectedHex and selectedUnit
            from = selectedHex;
            map.possibleTargets.hide();
            map.buildPossibleTargets(selectedUnit, ctrl.opponent.unitIterator());
            map.possibleTargets.show();
            if (to != null) {
                // quick fire -> replay touchUp
                upHex = to;
                touchUp();
            }
            map.selectHex(from, true);
        }
    }

    @Override
    public void leave(StateType nextState)
    {
        map.attackAssists.hide();
        map.attackAssists.enable(Hex.TARGET, false);    // disable selected assists
        map.possibleTargets.hide();
        map.selectHex(from, false);
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
        // activeUnit is the target
        if ((activeUnit == null) && map.possibleTargets.contains(upHex)) {
            map.possibleTargets.hide();
            to = upHex;
            activeUnit = to.getUnit();
            map.showTarget(to, true);
            map.buildAttackAssists(selectedUnit, activeUnit, ctrl.player.unitIterator());
            map.attackAssists.show();
            ctrl.hud.show(false, false, false, true, true, ctrl.cfg.canCancel);
        }

        if ((activeUnit != null) && map.attackAssists.contains(upHex)) {
            if (map.toggleAttackAssist(upHex.getUnit())) {
                map.showAssist(upHex, false);
                map.showTarget(upHex, true);
            } else {
                map.showAssist(upHex, true);
                map.showTarget(upHex, false);
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
        System.err.print("  attack (" + from.getCol() + ";" + from.getRow() + ") -> (" + to.getCol() + ";" + to.getRow() + ") : 2D6 -> (" + d1 + " + " + d2 + ")");
        if (map.attackPawn(selectedUnit, activeUnit, d1 + d2))
            ctrl.player.casualty(activeUnit);
        map.showTarget(to, false);
        ctrl.setState(StateType.ANIMATION);

        super.done();
    }
}
