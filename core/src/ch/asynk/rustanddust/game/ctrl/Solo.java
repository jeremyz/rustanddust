package ch.asynk.rustanddust.game.ctrl;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.util.Marshal;
import ch.asynk.rustanddust.util.GameRecord;
import ch.asynk.rustanddust.game.Ctrl;

public class Solo extends Ctrl
{
    public Solo(final RustAndDust game)
    {
        super(game);
    }

    @Override
    public void init()
    {
        if (!loadState()) {
            int me = game.backend.getMyId();
            int other = game.backend.getOpponentId();
            gameId = game.db.storeGameGetId(other, battle.getId(), game.config.gameMode.i);
            battle.getPlayer().id = me;
            battle.getOpponent().id = other;
            battle.initialDeployment(this);
            synched = true;
        }
    }

    private boolean loadState()
    {
        GameRecord r = null;
        gameId = game.config.gameId;

        switch (game.config.loadMode)
        {
            case NEW:           break;
            case RESUME:        r = game.db.loadGame(gameId); break;
            case REPLAY_LAST:   r = game.db.loadLastTurn(gameId); break;
            case REPLAY_BATTLE: r = game.db.loadTurn(gameId, 0); break;
        }

        if (r == null)
            return false;

        load(Marshal.Mode.MAP, r.map);
        load(Marshal.Mode.PLAYERS, r.players);
        load(Marshal.Mode.ORDERS, r.orders);
        synched = r.synched;
        r.dispose();

        return true;
    }
}
