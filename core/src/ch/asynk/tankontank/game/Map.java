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

    private final ArrayList<Vector3> finalPath = new ArrayList<Vector3>(10);
    private final ArrayList<GridPoint2> possibleMoves = new ArrayList<GridPoint2>(40);
    private final ArrayList<GridPoint2> possibleTargets = new ArrayList<GridPoint2>(10);
    private final HashSet<GridPoint2> possiblePaths = new HashSet<GridPoint2>(10);
    private final ArrayList<GridPoint2> moveAssists = new ArrayList<GridPoint2>(6);

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
        moveAssists.clear();
        clearPointSet(possiblePaths);
        clearPointVector(possibleMoves);
        clearPointVector(possibleTargets);
        clearCoordinateVector(finalPath);
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
            enableOverlayOn(hex, Hex.MOVE1, enable);
    }

    public void enableMoveAssists(boolean enable)
    {
        for(GridPoint2 hex : moveAssists)
            enableOverlayOn(hex, Hex.ASSIST, enable);
    }

    public void enablePossibleTargets(boolean enable)
    {
        for(GridPoint2 hex : possibleTargets)
            enableOverlayOn(hex, Hex.TARGET, enable);
    }

    public void enablePossiblePaths(boolean enable, boolean keepFinal)
    {
        if (keepFinal) {
            for(GridPoint2 hex : possiblePaths)
                enableOverlayOn(hex, Hex.MOVE1, enable);
        } else {
            for(GridPoint2 hex : possiblePaths) {
                enableOverlayOn(hex, Hex.MOVE1, enable);
                enableOverlayOn(hex, Hex.MOVE2, false);
            }
        }
    }

    public int buildPossibleMoves(Pawn pawn, GridPoint2 hex)
    {
        buildPossibleMovesFrom(pawn, hex, possibleMoves);
        return possibleMoves.size();
    }

    public int buildPossibleTargets(Pawn pawn, GridPoint2 hex)
    {
        buildPossibleTargetsFrom(pawn, hex, possibleTargets);
        return possibleTargets.size();
    }

    public int buildMoveAssists(Pawn pawn, GridPoint2 hex)
    {
        if (pawn.isHq()) {
            buildMoveAssists(pawn, hex, moveAssists);
        } else {
            moveAssists.clear();
        }
        return moveAssists.size();
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

    public void clearPossiblePaths()
    {
        clearPointSet(possiblePaths);
    }

    public void clearPossibleTargetsMovesAssists()
    {
        clearPointSet(possiblePaths);
        clearPointVector(possibleMoves);
        clearPointVector(possibleTargets);
    }

    public void togglePathOverlay(GridPoint2 hex)
    {
        boolean enable= !isOverlayEnabledOn(hex, Hex.MOVE2);
        enableOverlayOn(hex, Hex.MOVE2, enable);
    }

    public void enableFinalPath(GridPoint2 dst, boolean enable)
    {
        for(GridPoint2 hex : possiblePaths) {
            enableOverlayOn(hex, Hex.MOVE1, false);
            enableOverlayOn(hex, Hex.MOVE2, enable);
        }
        enableDirections(dst, enable);
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

    public void enableDirections(GridPoint2 hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.DIRECTIONS, enable);
    }
}
