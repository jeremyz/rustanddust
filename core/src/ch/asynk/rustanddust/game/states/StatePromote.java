package ch.asynk.rustanddust.game.states;

public class StatePromote extends StateCommon
{
    @Override
    public void enterFrom(StateType prevState)
    {
        ctrl.postOrder(map.getPromoteOrder(selectedUnit));
    }
}
