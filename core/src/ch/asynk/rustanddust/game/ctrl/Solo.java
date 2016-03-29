package ch.asynk.rustanddust.game.ctrl;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.util.Marshal;
import ch.asynk.rustanddust.util.GameRecord;
import ch.asynk.rustanddust.game.Ctrl;

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
            gameId = game.db.storeGameGetId(other, battle.getId(), game.config.gameMode.i);
            battle.getPlayer().id = me;
            battle.getOpponent().id = other;
            battle.initialDeployment();
        } else {
            GameRecord r = game.db.loadGame(gameId);
            if (r != null) {
                load(Marshal.Mode.STATE, r.state);
                load(Marshal.Mode.ORDERS, r.orders);
                battle.getMap().clearMarshalUnits();
                replayLastOrder = !r.synched;
                r.dispose();
            } else
                System.err.println("TODO : null GameRecord");
        }
    }

    @Override
    public void orderProcessedCb()
    {
        if (!isLoading())
            storeOrders();
    }

    @Override
    protected void actionDoneCb()
    {
        storeState();
    }

    @Override
    protected void turnDoneCb()
    {
        storeOrders();
        storeState();
        storeTurn();
    }

    private void storeState()
    {
        game.db.storeGameState(gameId, battle.getTurnCount(), battle.getPlayer().id, unload(Marshal.Mode.STATE));
    }

    private void storeOrders()
    {
        game.db.storeGameOrders(gameId, battle.getTurnCount(), battle.getPlayer().id, unload(Marshal.Mode.ORDERS));
    }

    private void storeTurn()
    {
        game.db.storeLastTurn(gameId);
    }
}
