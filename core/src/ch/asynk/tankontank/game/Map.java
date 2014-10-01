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
    enum Action {
        NONE,
        DRAG,
        PATH,
        DIRECTION,
        ATTACK
    };

    private Action action = Action.NONE;

    private Pawn pawn;
    private Pawn touchPawn;
    private GridPoint2 hex = new GridPoint2(-1, -1);
    private GridPoint2 touchHex = new GridPoint2(-1, -1);

    private GridPoint2 from = new GridPoint2(-1, -1);
    private GridPoint2 to = new GridPoint2(-1, -1);

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

    public boolean drag(float dx, float dy)
    {
        if (pawn == null) return false;
        pawn.translate(dx, dy);
        return true;
    }

    public void touchDown(float x, float y)
    {
        if (hex.x != -1)
            enableOverlayOn(hex.x, hex.y, Hex.BLUE, false);

        getHexAt(touchHex, x, y);
        if (touchHex.x != -1) {
            enableOverlayOn(touchHex.x, touchHex.y, Hex.BLUE, true);
            touchPawn = removeTopPawnFrom(touchHex);
            if (action == Action.DIRECTION) {
                System.out.println("DIRECTION");
                enableFinalPath(false);
                action = Action.NONE;
            } else {
                if (touchPawn != null) {
                    action = Action.DRAG;
                    enablePossiblePaths(false, false);
                    enablePossibleMoves(false);
                    enablePossibleTargets(false);
                    possiblePaths.clear();
                    pawnsToDraw.add(touchPawn);
                    pawn = touchPawn;
                } else if (possibleMoves.contains(touchHex)) {
                    int paths = 0;
                    action = Action.PATH;
                    if (possiblePaths.size() > 0) {
                        enablePossiblePaths(false, true);
                        paths = possiblePathsFilterAdd(touchHex.x, touchHex.y, possiblePaths);
                        enableOverlayOn(touchHex.x, touchHex.y, Hex.DOT, true);
                    } else {
                        enablePossibleMoves(false);
                        from.set(hex.x, hex.y);
                        to.set(touchHex.x, touchHex.y);
                        paths = possiblePaths(pawn, from.x, from.y, to.x, to.y, possiblePaths);
                        enableOverlayOn(touchHex.x, touchHex.y, Hex.DOT, true);
                    }
                    if (paths != 1)
                        enablePossiblePaths(true, true);
                    else {
                        enableFinalPath(true);
                        action = Action.DIRECTION;
                    }
                }
            }
            hex.set(touchHex.x, touchHex.y);
        } else {
            // touch out of map
            hex.set(-1, -1);
        }
    }

    public void touchUp(float x, float y)
    {
        if (hex.x != -1)
            enableOverlayOn(hex.x, hex.y, Hex.BLUE, false);

        getHexAt(touchHex, x, y);
        if (touchHex.x != -1) {
            hex.set(touchHex.x, touchHex.y);
            if (action == Action.DRAG) {
                enableOverlayOn(hex.x, hex.y, Hex.BLUE, true);
                pawnsToDraw.remove(pawn);
                movePawnTo(pawn, hex);
                showPossibleActions(pawn);
                action = Action.NONE;
            }
        } else {
            // release out of map
            resetPawnMoves(pawn);
            hex.set(-1, -1);
        }
    }

    private void showPossibleActions(Pawn pawn)
    {
        possibleMovesFrom(pawn, hex.x, hex.y, possibleMoves);
        enablePossibleMoves(true);

        possibleTargetsFrom(pawn, hex.x, hex.y, possibleTargets);
        enablePossibleTargets(true);
    }

    private void enablePossibleMoves(boolean enable)
    {
        for(GridPoint2 hex : possibleMoves)
            enableOverlayOn(hex.x, hex.y, Hex.GREEN, enable);
    }

    private void enableFinalPath(boolean enable)
    {
        for(GridPoint2 hex : possiblePaths) {
            enableOverlayOn(hex.x, hex.y, Hex.GREEN, false);
            enableOverlayOn(hex.x, hex.y, Hex.DOT, false);
            enableOverlayOn(hex.x, hex.y, Hex.MOVE, enable);
        }
        enableOverlayOn(to.x, to.y, Hex.ROSE, enable);
    }

    private void enablePossiblePaths(boolean enable, boolean keepDots)
    {
        for(GridPoint2 hex : possiblePaths) {
            enableOverlayOn(hex.x, hex.y, Hex.GREEN, enable);
            if (!keepDots)
                enableOverlayOn(hex.x, hex.y, Hex.DOT, false);
        }
    }

    private void enablePossibleTargets(boolean enable)
    {
        for(GridPoint2 hex : possibleTargets)
            enableOverlayOn(hex.x, hex.y, Hex.RED, enable);
    }
}
