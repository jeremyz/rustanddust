package ch.asynk.rustanddust.game.ctrl;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Battle;

public class Solo extends Ctrl
{
    private int gameId;

    public Solo(final RustAndDust game)
    {
        super(game);
    }

    @Override
    public void init()
    {
        gameId = game.config.gameId;
        if (gameId == game.db.NO_RECORD) {
            int me = game.backend.getMyId();
            int other = game.backend.getOpponentId();
            gameId = game.db.storeGameGetId(me, other, battle.getId(), game.config.gameMode.i);
            battle.init(this, me, other);
        } else {
            battle.init(this, game.db.loadState(gameId));
        }
    }

    @Override
    protected void processAction()
    {
        storeState();
    }

    @Override
    protected void processTurn()
    {
        storeOrders();
        storeState();
    }

    private void storeState()
    {
        game.db.storeState(gameId, battle.getPlayer().id, battle.getOpponent().id, battle.unload(true));
    }

    private void storeOrders()
    {
        game.db.storeTurn(gameId, battle.getPlayer().id, battle.unload(false));
    }
}
