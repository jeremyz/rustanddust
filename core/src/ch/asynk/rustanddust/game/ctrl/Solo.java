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
            gameId = game.db.storeGameGetId(other, battle.getId(), game.config.gameMode.i);
            battle.getPlayer().id = me;
            battle.getOpponent().id = other;
            battle.initialDeployment();
        } else {
            battle.load(game.db.loadGame(gameId));
        }
    }

    @Override
    protected void processAction()
    {
        storeGame();
    }

    @Override
    protected void processTurn()
    {
        storeTurn();
        storeGame();
    }

    private void storeGame()
    {
        game.db.storeGame(gameId, battle.getPlayer().id, battle.unload(true));
    }

    private void storeTurn()
    {
        game.db.storeTurn(gameId, battle.getPlayer().id, battle.unload(false));
    }
}
