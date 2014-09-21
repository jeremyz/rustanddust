package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Pawn;

public class Map extends Board
{
    private Pawn currentPawn;
    private GridPoint2 currentHex = new GridPoint2(-1, -1);

    public Map(Board.Config cfg, Hex[][] board, Texture texture)
    {
        super(cfg, board, texture);
    }

    public boolean drag(float dx, float dy)
    {
        if (currentPawn == null) return false;
        currentPawn.translate(dx, dy);
        return true;
    }

    public void touchDown(float x, float y)
    {
        getHexAt(currentHex, x, y);
        if (currentHex.x != -1) {
            currentPawn = getTopPawnAt(currentHex);
        }
    }

    public void touchUp(float x, float y)
    {
        getHexAt(currentHex, x, y);
        if (currentPawn != null) {
            movePawnTo(currentPawn, currentHex);
        }
    }
}
