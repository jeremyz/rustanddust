package ch.asynk.tankontank.game;

import java.util.Vector;
import java.util.HashSet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Pawn;

public abstract class Map extends Board
{
    private final Vector<GridPoint2> possibleMoves = new Vector<GridPoint2>(20);
    private final Vector<GridPoint2> possibleTargets = new Vector<GridPoint2>(10);
    private final HashSet<GridPoint2> possiblePaths = new HashSet<GridPoint2>(10);

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

    public boolean isInPossibleMoves(GridPoint2 hex)
    {
        return possibleMoves.contains(hex);
    }

    public boolean isInPossiblePaths(GridPoint2 hex)
    {
        return possiblePaths.contains(hex);
    }

    public boolean isInPossibleTargets(GridPoint2 hex)
    {
        return possibleTargets.contains(hex);
    }

    public void enablePossibleMoves(boolean enable)
    {
        for(GridPoint2 hex : possibleMoves)
            enableOverlayOn(hex, Hex.GREEN, enable);
    }

    public void enablePossibleTargets(boolean enable)
    {
        for(GridPoint2 hex : possibleTargets)
            enableOverlayOn(hex, Hex.RED, enable);
    }

    public void enablePossiblePaths(boolean enable, boolean keepDots)
    {
        if (keepDots) {
            for(GridPoint2 hex : possiblePaths)
                enableOverlayOn(hex, Hex.GREEN, enable);
        } else {
            for(GridPoint2 hex : possiblePaths) {
                enableOverlayOn(hex, Hex.GREEN, enable);
                enableOverlayOn(hex, Hex.DOT, false);
            }
        }
    }

    public void showPossibleActions(Pawn pawn, GridPoint2 hex, boolean enable)
    {
        enablePossibleMoves(false);
        enablePossibleTargets(false);
        if (enable) {
            possibleMovesFrom(pawn, hex, possibleMoves);
            enablePossibleMoves(true);
            possibleTargetsFrom(pawn, hex, possibleTargets);
            enablePossibleTargets(true);
        }

    }

    public int possiblePathsSize()
    {
        return possiblePaths.size();
    }

    public int buildPossiblePaths(Pawn pawn, GridPoint2 from, GridPoint2 to)
    {
        return possiblePaths(pawn, from, to, possiblePaths);
    }

    public int possiblePathsPointToggle(GridPoint2 point)
    {
        return possiblePathsFilterToggle(point.x, point.y, possiblePaths);
    }

    public void clearPossibles()
    {
        enablePossibleMoves(false);
        enablePossiblePaths(false, false);
        enablePossibleTargets(false);
        clearNodesSet(possiblePaths);
        // clearNodesVector(possibleMoves);
        // clearNodesVector(possibleTargets);
    }

    public void toggleDotOverlay(GridPoint2 hex)
    {
        boolean enable= !isOverlayEnabledOn(hex.x, hex.y, Hex.DOT);
        enableOverlayOn(hex, Hex.DOT, enable);
    }

    public void enableFinalPath(GridPoint2 dst, boolean enable)
    {
        for(GridPoint2 hex : possiblePaths) {
            enableOverlayOn(hex, Hex.GREEN, false);
            enableOverlayOn(hex, Hex.DOT, false);
            enableOverlayOn(hex, Hex.MOVE, enable);
        }
        enableOverlayOn(dst, Hex.ROSE, true);
    }
}
