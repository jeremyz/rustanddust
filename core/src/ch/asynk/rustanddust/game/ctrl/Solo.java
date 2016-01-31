package ch.asynk.rustanddust.game.ctrl;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Battle;

public class Solo extends Ctrl
{
    public Solo(final RustAndDust game, final Battle battle)
    {
        super(game, battle);
    }

    @Override
    public void init()
    {
        battle.init(this, 0, 1);
    }

    @Override
    protected void processAction() { }

    @Override
    protected void processTurn() { }
}
