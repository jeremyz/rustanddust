package ch.asynk.rustanddust.game.states;

public class StateAnimation extends StateCommon
{
    @Override
    public void enterFrom(StateType prevState)
    {
        ctrl.blockMap = true;
        ctrl.hud.actionButtons.hide();
    }
}
