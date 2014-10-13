package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.game.State.StateType;

public class StateAttack extends StateCommon
{
    @Override
    public void enter(boolean fromSelect)
    {
        map.clearPossibleTargets();
        ctrl.hud.show(false, false, true, false, ctrl.cfg.canCancel);
        ctrl.hud.attackBtn.setOn();

        if (fromSelect) {
            activePawn = null;
            // use selectedHex and selectedPawn
            from.set(selectedHex);
            map.showPossibleTargets(false);
            map.buildPossibleTargets(selectedPawn, from);
            map.showPossibleTargets(true);
            if (to.x != -1) {
                // quick fire -> replay touchUp
                upHex.set(to);
                touchUp();
            }
            selectHex(from);
        }
    }

    @Override
    public void leave(StateType nextState)
    {
        map.showAttackAssists(false);
        map.showPossibleTargets(false);
        unselectHex(from);
        if (to.x != -1)
            unselectHex(to);
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        // activePawn is the target
        if ((activePawn == null) && map.isInPossibleTargets(upHex)) {
            map.showPossibleTargets(false);
            to.set(upHex);
            activePawn = map.getTopPawnAt(to);
            showTarget(to, true);
            map.buildAttack(selectedPawn, activePawn, to, ctrl.currentPlayer.unitIterator());
            map.showAttackAssists(true);
            ctrl.hud.show(false, false, true, true, ctrl.cfg.canCancel);
        }

        if ((activePawn != null) && map.isInPossibleAttackAssists(upHex)) {
            if (map.toggleAttackAssist(map.getTopPawnAt(upHex))) {
                showAssist(upHex, false);
                showTarget(upHex, true);
            } else {
                showAssist(upHex, true);
                showTarget(upHex, false);
            }
        }
    }

    @Override
    public void abort()
    {
        super.abort();
    }

    @Override
    public void done()
    {
        int d1 = ctrl.currentPlayer.d6();
        int d2 = ctrl.currentPlayer.d6();
        System.err.print("  attack (" + from.x + ";" + from.y + ") -> (" + to.x + ";" + to.y + ") : 2D6 -> (" + d1 + " + " + d2 + ")");
        if (map.attackPawn(selectedPawn, activePawn, from, to, d1 + d2)) {
            map.removePawnFrom(activePawn, to);
            ctrl.currentPlayer.casualty(activePawn);
            // TODO free move for infantry
        }

        super.done();
    }
}
