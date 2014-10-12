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
    private final Ctrl ctrl;

    private final ArrayList<Vector3> finalPath = new ArrayList<Vector3>(10);
    private final ArrayList<GridPoint2> possibleMoves = new ArrayList<GridPoint2>(40);
    private final ArrayList<GridPoint2> possibleTargets = new ArrayList<GridPoint2>(10);
    private final HashSet<GridPoint2> possiblePaths = new HashSet<GridPoint2>(10);
    private final ArrayList<GridPoint2> moveAssists = new ArrayList<GridPoint2>(6);

    private final ArrayList<Pawn> activablePawns = new ArrayList<Pawn>(7);
    private final ArrayList<Pawn> activatedPawns = new ArrayList<Pawn>(7);

    protected abstract void setup();

    public Map(Ctrl ctrl, Factory factory, Board.Config cfg, Texture texture)
    {
        super(factory, cfg, texture);
        this.ctrl = ctrl;
        setup();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        clearAll();
    }

    public void clearAll()
    {
        moveAssists.clear();
        activablePawns.clear();
        activatedPawns.clear();
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

    public GridPoint2 getFirstMoveAssist()
    {
        return moveAssists.get(0);
    }

    public int activablePawnsCount()
    {
        return activablePawns.size();
    }

    public int activatedPawnsCount()
    {
        return activatedPawns.size();
    }

    public boolean isInPossibleMoves(GridPoint2 hex)
    {
        return possibleMoves.contains(hex);
    }

    public boolean isInPossibleMoveAssists(GridPoint2 hex)
    {
        return moveAssists.contains(hex);
    }

    public boolean isInPossiblePaths(GridPoint2 hex)
    {
        return possiblePaths.contains(hex);
    }

    public boolean isInPossibleTargets(GridPoint2 hex)
    {
        return possibleTargets.contains(hex);
    }

    public void showPossibleMoves(boolean enable)
    {
        for(GridPoint2 hex : possibleMoves)
            enableOverlayOn(hex, Hex.MOVE1, enable);
    }

    public void showMoveAssists(boolean enable)
    {
        for(GridPoint2 hex : moveAssists)
            enableOverlayOn(hex, Hex.ASSIST, enable);
    }

    public void showPossibleTargets(boolean enable)
    {
        for(GridPoint2 hex : possibleTargets)
            enableOverlayOn(hex, Hex.TARGET, enable);
    }

    public void showPossiblePaths(boolean enable, boolean keepFinal)
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

    public void showFinalPath(GridPoint2 dst, boolean enable)
    {
        for(GridPoint2 hex : possiblePaths) {
            enableOverlayOn(hex, Hex.MOVE1, false);
            enableOverlayOn(hex, Hex.MOVE2, enable);
        }
    }

    public void showDirections(GridPoint2 hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.DIRECTIONS, enable);
    }

    public void showOrientation(GridPoint2 hex, boolean enable, Orientation o)
    {
        enableOverlayOn(hex, Hex.ORIENTATION, enable, o);
    }

    public int possiblePathsSize()
    {
        return possiblePaths.size();
    }

    public void togglePathOverlay(GridPoint2 hex)
    {
        boolean enable= !isOverlayEnabledOn(hex, Hex.MOVE2);
        enableOverlayOn(hex, Hex.MOVE2, enable);
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

    public void buildAndShowMovesAndAssits(Pawn pawn, GridPoint2 hex)
    {
        showPossibleMoves(false);
        showMoveAssists(false);
        activablePawns.clear();
        activatedPawns.clear();
        buildPossibleMoves(pawn, hex);
        buildMoveAssists(pawn, hex);
        activablePawns.add(pawn);
        for (GridPoint2 p : moveAssists)
            activablePawns.add(getTopPawnAt(p));
        showPossibleMoves(true);
        showMoveAssists(true);
    }

    public int buildPossiblePaths(Pawn pawn, GridPoint2 from, GridPoint2 to)
    {
        return buildPossiblePaths(pawn, from, to, possiblePaths);
    }

    public int possiblePathsPointToggle(GridPoint2 hex)
    {
        return possiblePathsFilterToggle(hex, possiblePaths);
    }

    public void clearPossiblePaths()
    {
        clearPointSet(possiblePaths);
    }

    public int movePawn(Pawn pawn, GridPoint2 from, Orientation o)
    {
        System.err.println(" movePawn : " + from.x + ";" + from.y + " " + o);
        int cost = getPathCost(pawn, 0);
        int s = getCoordinatePath(pawn, 0, finalPath, o);
        if (s > 0) {
            movePawn(pawn, cost, finalPath, RunnableAnimation.get(pawn, new Runnable() {
                @Override
                public void run() {
                    ctrl.animationDone();
                }
            }));
        }

        return finishMove(pawn, from);
    }

    public int rotatePawn(Pawn pawn, GridPoint2 from, Orientation o)
    {
        System.err.println(" rotatePawn : " + from.x + ";" + from.y + " " +o);
        rotatePawn(pawn, o, RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                ctrl.animationDone();
            }
        }));

        return finishMove(pawn, from);
    }

    public void revertMoves()
    {
        System.err.println(" revertMoves()");
        for (Pawn pawn : activatedPawns) {
            revertLastPawnMove(pawn, RunnableAnimation.get(pawn, new Runnable() {
                @Override
                public void run() {
                    ctrl.animationDone();
                }
            }));
        }
        activatedPawns.clear();
    }

    private int finishMove(Pawn pawn, GridPoint2 from) {
        moveAssists.remove(from);
        activablePawns.remove(pawn);
        activatedPawns.add(pawn);
        return activablePawns.size();
    }
}
