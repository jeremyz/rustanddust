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
            synched = true;
        } else {
            GameRecord r = loadState();
            if (r != null) {
                load(Marshal.Mode.STATE, r.state);
                load(Marshal.Mode.ORDERS, r.orders);
                battle.getMap().clearMarshalUnits();
                synched = r.synched;
                r.dispose();
            } else
                System.err.println("TODO : null GameRecord");
        }
    }

    private GameRecord loadState()
    {
        GameRecord r = null;
        switch (game.config.loadMode) {
            case LOAD:
                r = game.db.loadGame(gameId);
                break;
            case REPLAY_LAST:
                r = game.db.loadLastTurn(gameId);
                break;
            case REPLAY_ALL:
                // TODO REPLAY_ALL
                break;
        }
        return r;
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
