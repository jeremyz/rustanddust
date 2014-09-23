package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Pawn;

public abstract class Map extends Board
{
    private Pawn currentPawn;
    private GridPoint2 currentHex = new GridPoint2(-1, -1);

    protected abstract void setup();

    public Map(Board.Config cfg, Texture texture, TextureAtlas hexAtlas)
    {
        super(cfg, texture, new Hex(hexAtlas));
        setup();
    }

    protected Hex getHex(int col, int row)
    {
        return (Hex) board[row][col];
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
            currentPawn = removeTopPawnFrom(currentHex);
            if (currentPawn != null) pawnsToDraw.add(currentPawn);
        }
    }

    public void touchUp(float x, float y)
    {
        getHexAt(currentHex, x, y);
        if (currentPawn != null) {
            pawnsToDraw.remove(currentPawn);
            movePawnTo(currentPawn, currentHex);
        }
    }
}
