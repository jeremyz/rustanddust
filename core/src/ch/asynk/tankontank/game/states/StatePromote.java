package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.game.Unit;

public class StatePromote extends StateCommon
{
    @Override
    public void enter(StateType prevState)
    {
        ctrl.hud.actionButtons.hide();
        String str = selectedUnit.toString();
        if (ctrl.player.promote(selectedUnit)) {
            map.activatedUnits.add(selectedUnit);
            // ctrl.hud.notify(str+ " has been promoted");
        }
        ctrl.setState(StateType.DONE);
    }

    @Override
    public void leave(StateType nextState)
    {
        map.unselectHex(selectedHex);
    }

    @Override
    public StateType abort()
    {
        return StateType.ABORT;
    }

    @Override
    public StateType done()
    {
        return StateType.DONE;
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
    }
}
