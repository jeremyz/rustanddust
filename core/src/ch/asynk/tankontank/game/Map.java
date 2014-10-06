package ch.asynk.tankontank.game;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;

public abstract class Map extends Board
{
    private final GameCtrl ctrl;

    private final ArrayList<Vector3> finalPath = new ArrayList<Vector3>(10);
    private final ArrayList<GridPoint2> possibleMoves = new ArrayList<GridPoint2>(40);
    private final ArrayList<GridPoint2> possibleTargets = new ArrayList<GridPoint2>(10);
    private final HashSet<GridPoint2> possiblePaths = new HashSet<GridPoint2>(10);

    protected abstract void setup();

    public Map(GameCtrl ctrl, GameFactory factory, Board.Config cfg, Texture texture)
    {
        super(factory, cfg, texture);
        this.ctrl = ctrl;
        setup();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        resetPaths();
        clearCoordinateVector(finalPath);
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

    public void enablePossiblePaths(boolean enable, boolean keepMoves)
    {
        if (keepMoves) {
            for(GridPoint2 hex : possiblePaths)
                enableOverlayOn(hex, Hex.GREEN, enable);
        } else {
            for(GridPoint2 hex : possiblePaths) {
                enableOverlayOn(hex, Hex.GREEN, enable);
                enableOverlayOn(hex, Hex.MOVE, false);
            }
        }
    }

    public void showPossibleMoves(Pawn pawn, GridPoint2 hex, boolean enable)
    {
        enablePossibleMoves(false);
        if (enable) {
            possibleMovesFrom(pawn, hex, possibleMoves);
            enablePossibleMoves(true);
        }

    }

    public void showPossibleTargets(Pawn pawn, GridPoint2 hex, boolean enable)
    {
        enablePossibleTargets(false);
        if (enable) {
            possibleTargetsFrom(pawn, hex, possibleTargets);
            enablePossibleTargets(true);
        }

    }

    public void showPossibleActions(Pawn pawn, GridPoint2 hex, boolean enable)
    {
        showPossibleMoves(pawn, hex, enable);
        showPossibleTargets(pawn, hex, enable);
    }

    public int possiblePathsSize()
    {
        return possiblePaths.size();
    }

    public int buildPossiblePaths(Pawn pawn, GridPoint2 from, GridPoint2 to)
    {
        return possiblePaths(pawn, from, to, possiblePaths);
    }

    public int possiblePathsPointToggle(GridPoint2 hex)
    {
        return possiblePathsFilterToggle(hex, possiblePaths);
    }

    public void movePawn(Pawn pawn, Orientation o)
    {
        int s = getCoordinatePath(pawn, finalPath, o);
        if (s > 0) {
            movePawn(pawn, finalPath);
        }
    }

    public void hidePaths()
    {
        enablePossibleTargets(false);
        enablePossibleMoves(false);
        enablePossiblePaths(false, false);
    }

    public void resetPaths()
    {
        clearPointSet(possiblePaths);
        clearPointVector(possibleMoves);
        clearPointVector(possibleTargets);
    }

    public void toggleDotOverlay(GridPoint2 hex)
    {
        boolean enable= !isOverlayEnabledOn(hex, Hex.MOVE);
        enableOverlayOn(hex, Hex.MOVE, enable);
    }

    public void enableFinalPath(GridPoint2 dst, boolean enable)
    {
        for(GridPoint2 hex : possiblePaths) {
            enableOverlayOn(hex, Hex.GREEN, false);
            enableOverlayOn(hex, Hex.MOVE, enable);
        }
        enableDirections(dst, enable);
    }

    public void enableDirections(GridPoint2 hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.ROSE, enable);
    }
}
