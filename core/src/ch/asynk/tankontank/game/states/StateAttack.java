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
        ctrl.hud.show(false, false, false, true, false, ctrl.cfg.canCancel);
        ctrl.hud.attackBtn.setOn();

        if (fromSelect) {
            activePawn = null;
            // use selectedHex and selectedPawn
            from.set(selectedHex);
            map.showPossibleTargets(false);
            map.buildPossibleTargets(selectedPawn, from, ctrl.opponent.unitIterator());
            map.showPossibleTargets(true);
            if (to.x != -1) {
                // quick fire -> replay touchUp
                upHex.set(to);
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
        if (to.x != -1)
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
        if ((activePawn == null) && map.isInPossibleTargets(upHex)) {
            map.showPossibleTargets(false);
            to.set(upHex);
            activePawn = map.getTopPawnAt(to);
            map.showTarget(to, true);
            map.buildAttackAssists(selectedPawn, activePawn, to, ctrl.player.unitIterator());
            map.showAttackAssists(true);
            ctrl.hud.show(false, false, false, true, true, ctrl.cfg.canCancel);
        }

        if ((activePawn != null) && map.isInPossibleAttackAssists(upHex)) {
            if (map.toggleAttackAssist(map.getTopPawnAt(upHex))) {
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
        map.clearActivatedPawns();
        super.abort();
    }

    @Override
    public void done()
    {
        int d1 = ctrl.player.d6();
        int d2 = ctrl.player.d6();
        System.err.print("  attack (" + from.x + ";" + from.y + ") -> (" + to.x + ";" + to.y + ") : 2D6 -> (" + d1 + " + " + d2 + ")");
        if (map.attackPawn(selectedPawn, activePawn, from, to, d1 + d2))
            ctrl.player.casualty(activePawn);
        map.showTarget(to, false);
        ctrl.setState(StateType.ANIMATION);

        super.done();
    }
}
