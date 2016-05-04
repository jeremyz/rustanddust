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
        GameRecord r = loadState();
        if (r == null) {
            int me = game.backend.getMyId();
            int other = game.backend.getOpponentId();
            gameId = game.db.storeGameGetId(other, battle.getId(), game.config.gameMode.i);
            battle.getPlayer().id = me;
            battle.getOpponent().id = other;
            battle.initialDeployment(this);
            synched = true;
        } else {
            load(Marshal.Mode.MAP, r.map);
            load(Marshal.Mode.PLAYERS, r.players);
            load(Marshal.Mode.ORDERS, r.orders);
            battle.getMap().clearMarshalUnits();
            synched = r.synched;
            r.dispose();
        }
    }

    private GameRecord loadState()
    {
        GameRecord r = null;
        gameId = game.config.gameId;
        switch (game.config.loadMode)
        {
            case NEW:               break;
            case RESUME:            r = game.db.loadGame(gameId); break;
            case REPLAY_CURRENT:    r = game.db.loadLastTurn(gameId); break;
            case REPLAY_ALL:
                // TODO REPLAY_ALL
                break;
        }
        return r;
    }
}
