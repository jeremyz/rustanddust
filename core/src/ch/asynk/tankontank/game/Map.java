package ch.asynk.tankontank.game;

import java.util.Vector;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Pawn;

public abstract class Map extends Board
{
    private boolean roadsOn = false;
    private boolean hexOn = false;
    private Hex.Terrain t = Hex.Terrain.CLEAR;

    private Pawn currentPawn;
    private GridPoint2 currentHex = new GridPoint2(-1, -1);

    private final Vector<GridPoint2> possibleMoves = new Vector<GridPoint2>(20);
    private final Vector<GridPoint2> possibleTargets = new Vector<GridPoint2>(10);

    protected abstract void setup();

    public Map(GameFactory gameFactory, Board.Config cfg, Texture texture)
    {
        super(gameFactory, cfg, texture);
        setup();
    }

    protected Hex getHex(int col, int row)
    {
        return (Hex) getTile(col, row);
    }

    public boolean drag(float dx, float dy)
    {
        if (currentPawn == null) return false;
        currentPawn.translate(dx, dy);
        return true;
    }

    public void touchDown(float x, float y)
    {
        if (currentHex.x != -1)
            enableOverlayOn(currentHex.x, currentHex.y, Hex.BLUE, false);
        getHexAt(currentHex, x, y);
        if (currentHex.x != -1) {
            enableOverlayOn(currentHex.x, currentHex.y, Hex.BLUE, true);
            currentPawn = removeTopPawnFrom(currentHex);
            if (currentPawn != null) {
                enablePossibleMoves(false);
                enablePossibleTargets(false);
                pawnsToDraw.add(currentPawn);
            }
        }
    }

    public void touchUp(float x, float y)
    {
        if (currentHex.x != -1)
            enableOverlayOn(currentHex.x, currentHex.y, Hex.BLUE, false);
        getHexAt(currentHex, x, y);
        if (currentPawn != null) {
            enableOverlayOn(currentHex.x, currentHex.y, Hex.BLUE, true);
            pawnsToDraw.remove(currentPawn);
            if (currentHex.x != -1) {
                movePawnTo(currentPawn, currentHex);
                showPossibleActions(currentPawn);
            } else {
                resetPawnMoves(currentPawn);
            }
        }
    }

    public void showPossibleActions(Pawn pawn)
    {
        possibleMovesFrom(pawn, currentHex.x, currentHex.y, possibleMoves);
        enablePossibleMoves(true);

        possibleTargetsFrom(pawn, currentHex.x, currentHex.y, possibleTargets);
        enablePossibleTargets(true);
    }

    public void enablePossibleMoves(boolean enable)
    {
        for(GridPoint2 hex : possibleMoves)
            enableOverlayOn(hex.x, hex.y, Hex.GREEN, enable);
    }

    public void enablePossibleTargets(boolean enable)
    {
        for(GridPoint2 hex : possibleTargets)
            enableOverlayOn(hex.x, hex.y, Hex.RED, enable);
    }
}
