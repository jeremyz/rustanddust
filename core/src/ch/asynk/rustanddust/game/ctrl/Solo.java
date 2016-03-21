package ch.asynk.rustanddust.game.ctrl;

import ch.asynk.rustanddust.RustAndDust;
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
            battle.load(r.turn, r.payload);
            r.dispose();
        }
    }

    @Override
    public void orderProcessedCb()
    {
        // TODO
    }

    @Override
    protected void actionDoneCb()
    {
        storeGame();
    }

    @Override
    protected void turnDoneCb()
    {
        storeGame();
        storeTurn();
    }

    private void storeGame()
    {
        game.db.storeGame(gameId, battle.getTurnCount(), battle.getPlayer().id, battle.unload(true));
    }

    private void storeTurn()
    {
        game.db.storeTurn(gameId);
    }
}
