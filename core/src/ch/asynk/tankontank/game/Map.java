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
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public abstract class Map extends Board
{
    private final GameCtrl ctrl;

    private final GridPoint2 neighbours[] = new GridPoint2[6];
    private final ArrayList<Vector3> finalPath = new ArrayList<Vector3>(10);
    private final ArrayList<GridPoint2> possibleMoves = new ArrayList<GridPoint2>(40);
    private final ArrayList<GridPoint2> possibleTargets = new ArrayList<GridPoint2>(10);
    private final HashSet<GridPoint2> possiblePaths = new HashSet<GridPoint2>(10);
    private final ArrayList<GridPoint2> moveAssist = new ArrayList<GridPoint2>(6);

    protected abstract void setup();

    public Map(GameCtrl ctrl, GameFactory factory, Board.Config cfg, Texture texture)
    {
        super(factory, cfg, texture);
        this.ctrl = ctrl;
        setup();
        for (int i = 0; i < 6; i++)
            neighbours[i] = new GridPoint2(-1, -1);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        clearPossibles();
        clearCoordinateVector(finalPath);
    }

    public void buildNeighbours(GridPoint2 hex)
    {
        setNeighbour(hex, Orientation.SOUTH, neighbours[0]);
        setNeighbour(hex, Orientation.SOUTH_WEST, neighbours[1]);
        setNeighbour(hex, Orientation.NORTH_WEST, neighbours[2]);
        setNeighbour(hex, Orientation.NORTH, neighbours[3]);
        setNeighbour(hex, Orientation.NORTH_EAST, neighbours[4]);
        setNeighbour(hex, Orientation.SOUTH_EAST, neighbours[5]);
    }

    protected Hex getHex(int col, int row)
    {
        return (Hex) getTile(col, row);
    }

    protected Hex getHexSafe(GridPoint2 hex)
    {
        return (Hex) getTileSafe(hex.x, hex.y);
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

    public void enableMoveAssist(boolean enable)
    {
        for(GridPoint2 hex : moveAssist)
            enableOverlayOn(hex, Hex.ASSIST, enable);
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

    public void buildAndShowPossibleMoves(Pawn pawn, GridPoint2 hex)
    {
        enablePossibleMoves(false);
        possibleMovesFrom(pawn, hex, possibleMoves);
        enablePossibleMoves(true);

    }

    public void buildAndShowMoveAssist(Pawn pawn, GridPoint2 hex)
    {
        enableMoveAssist(false);
        moveAssist.clear();
        buildNeighbours(hex);
        for (int i = 0; i < 6; i++) {
            GridPoint2 neighbour = neighbours[i];
            Hex h = getHexSafe(neighbour);
            if (h != null) {
                Pawn p = h.getTopPawn();
                if ((p != null) && (!pawn.isEnemy(p)))
                    moveAssist.add(neighbour);
            }
        }
        enableMoveAssist(true);
    }

    public void buildAndShowPossibleTargets(Pawn pawn, GridPoint2 hex)
    {
        enablePossibleTargets(false);
        possibleTargetsFrom(pawn, hex, possibleTargets);
        enablePossibleTargets(true);

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
            movePawn(pawn, finalPath, RunnableAnimation.get(pawn, new Runnable() {
                @Override
                public void run() {
                    ctrl.animationDone();
                }
            }));
        }
    }

    public void rotatePawn(Pawn pawn, Orientation o)
    {
        rotatePawn(pawn, o, RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                ctrl.animationDone();
            }
        }));
    }

    public void hidePossibles()
    {
        enablePossibleTargets(false);
        enablePossibleMoves(false);
        enablePossiblePaths(false, false);
    }

    public void clearPossibles()
    {
        clearPointSet(possiblePaths);
        clearPointVector(possibleMoves);
        clearPointVector(possibleTargets);
    }

    public void clearPossiblePaths()
    {
        clearPointSet(possiblePaths);
    }

    public void togglePathOverlay(GridPoint2 hex)
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
