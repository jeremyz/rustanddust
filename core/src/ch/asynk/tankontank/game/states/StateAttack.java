package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Pawn;
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
            activePawn = null;
            // use selectedHex and selectedPawn
            from = selectedHex;
            map.showPossibleTargets(false);
            map.buildPossibleTargets(selectedPawn, ctrl.opponent.unitIterator());
            map.showPossibleTargets(true);
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
        map.showAttackAssists(false);
        map.showPossibleTargets(false);
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
        // activePawn is the target
        if ((activePawn == null) && map.possibleTargets.contains(upHex)) {
            map.showPossibleTargets(false);
            to = upHex;
            activePawn = to.getTopPawn();
            map.showTarget(to, true);
            map.buildAttackAssists(selectedPawn, activePawn, ctrl.player.unitIterator());
            map.showAttackAssists(true);
            ctrl.hud.show(false, false, false, true, true, ctrl.cfg.canCancel);
        }

        if ((activePawn != null) && map.attackAssists.contains(upHex)) {
            if (map.toggleAttackAssist(upHex.getTopPawn())) {
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
        if (map.attackPawn(selectedPawn, activePawn, d1 + d2))
            ctrl.player.casualty(activePawn);
        map.showTarget(to, false);
        ctrl.setState(StateType.ANIMATION);

        super.done();
    }
}
