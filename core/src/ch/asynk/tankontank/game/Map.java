package ch.asynk.tankontank.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.SpriteAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public abstract class Map extends Board
{
    private final Ctrl ctrl;

    private final ArrayList<Vector3> finalPath = new ArrayList<Vector3>(10);
    private final ArrayList<GridPoint2> possibleMoves = new ArrayList<GridPoint2>(40);
    private final ArrayList<GridPoint2> possibleTargets = new ArrayList<GridPoint2>(10);
    private final HashSet<GridPoint2> possiblePaths = new HashSet<GridPoint2>(10);
    private final ArrayList<GridPoint2> moveAssists = new ArrayList<GridPoint2>(6);
    private final ArrayList<GridPoint2> attackAssists = new ArrayList<GridPoint2>(6);

    private final ArrayList<Pawn> activablePawns = new ArrayList<Pawn>(7);
    private final ArrayList<Pawn> activatedPawns = new ArrayList<Pawn>(7);

    private final SpriteAnimation explosion;
    private final SpriteAnimation explosions;

    protected abstract void setup();

    public Map(final TankOnTank game, Board.Config cfg, String textureName)
    {
        super(game.factory, cfg, game.manager.get(textureName, Texture.class));
        this.ctrl = game.ctrl;
        this.explosion = new SpriteAnimation(game.manager.get("data/explosion.png", Texture.class), 10, 4, 40);
        this.explosions = new SpriteAnimation(game.manager.get("data/explosions.png", Texture.class), 16, 8, 15);
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
        clearPointVector(attackAssists);
        activablePawns.clear();
        activatedPawns.clear();
        clearPointSet(possiblePaths);
        clearPointVector(possibleMoves);
        clearPointVector(possibleTargets);
        clearCoordinateVector(finalPath);
    }

    public void clearPossiblePaths()
    {
        clearPointSet(possiblePaths);
    }

    public void clearPossibleTargets()
    {
        clearPointVector(possibleTargets);
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

    public int possiblePathsSize()
    {
        return possiblePaths.size();
    }

    public boolean isInPossibleMoves(GridPoint2 hex)
    {
        return possibleMoves.contains(hex);
    }

    public boolean isInPossibleMoveAssists(GridPoint2 hex)
    {
        return moveAssists.contains(hex);
    }

    public boolean isInPossibleAttackAssists(GridPoint2 hex)
    {
        return attackAssists.contains(hex);
    }

    public boolean isInPossiblePaths(GridPoint2 hex)
    {
        return possiblePaths.contains(hex);
    }

    public boolean isInPossibleTargets(GridPoint2 hex)
    {
        return possibleTargets.contains(hex);
    }

    public void selectHex(GridPoint2 hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.SELECT, enable);
    }

    public void showAssist(GridPoint2 hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.ASSIST, enable);
    }

    public void showTarget(GridPoint2 hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.TARGET, enable);
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

    public void showAttackAssists(boolean enable)
    {
        for(GridPoint2 hex : attackAssists) {
            enableOverlayOn(hex, Hex.ASSIST, enable);
            enableOverlayOn(hex, Hex.TARGET, false);
        }
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

    public void hidePossibleTargetsMovesAssists()
    {
        showPossibleMoves(false);
        showPossibleTargets(false);
        showMoveAssists(false);
    }

    public void togglePathOverlay(GridPoint2 hex)
    {
        boolean enable= !isOverlayEnabledOn(hex, Hex.MOVE2);
        enableOverlayOn(hex, Hex.MOVE2, enable);
    }

    public int buildPossibleMoves(Pawn pawn, GridPoint2 hex)
    {
        return buildPossibleMovesFrom(pawn, hex, possibleMoves);
    }

    public int buildPossibleTargets(Pawn pawn, GridPoint2 hex, Iterator<Pawn> foes)
    {
        // return buildPossibleTargetsFrom(pawn, hex, possibleTargets);
        return buildPossibleTargetsFrom(pawn, hex, foes, possibleTargets);
    }

    public int buildMoveAssists(Pawn pawn, GridPoint2 hex)
    {
        if (!pawn.isHq()) {
            moveAssists.clear();
            return 0;
        }
        return buildMoveAssists(pawn, hex, moveAssists);
    }

    public int buildAttackAssists(Pawn pawn, Pawn target, GridPoint2 hex, Iterator<Pawn> units)
    {
        int s = buildAttackAssists(pawn, target, hex, units, attackAssists);
        activatedPawns.add(pawn);
        for (GridPoint2 p : attackAssists)
            activablePawns.add(getTopPawnAt(p));
        return s;
    }

    public boolean toggleAttackAssist(Pawn pawn)
    {
        if (activablePawns.contains(pawn)) {
            activablePawns.remove(pawn);
            activatedPawns.add(pawn);
            return true;
        } else {
            activatedPawns.remove(pawn);
            activablePawns.add(pawn);
            return false;
        }
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

    public boolean attackPawn(Pawn pawn, final Pawn target, GridPoint2 from, GridPoint2 to, int dice)
    {
        Hex hex = getHex(to.x, to.y);

        int activatedUnits = activatedPawns.size();

        final boolean success;
        if (dice == 2) {
            success = false;
        } else if (dice == 12) {
            success = true;
        } else {
            int flankAttacks = 0;
            for (Pawn assist : activatedPawns) {
                if (assist.isFlankAttack()) {
                    flankAttacks = 1;
                    break;
                }
            }
            System.err.print(" + " + activatedUnits + " + " + flankAttacks);
            success = ((dice + activatedUnits + flankAttacks) >= hex.defenseFor(target, activatedPawns));
        }

        AnimationSequence seq = AnimationSequence.get(2);
        if (success) {
            explosions.init(1, target.getCenter().x, target.getCenter().y);
            seq.addAnimation(explosions);
        } else {
            explosion.init(1, target.getCenter().x, target.getCenter().y);
            seq.addAnimation(explosion);
        }
        seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                if (success) {
                    removePawn(target);
                }
                ctrl.animationDone();
            }
        }));

        addAnimation(seq);

        for (Pawn p : activatedPawns)
            pawn.attack(target);
        if ((activatedPawnsCount() == 1) && pawn.isA(Unit.UnitType.AT_GUN) && target.isHardTarget())
            activatedPawns.clear();

        return success;
    }

    public int movePawn(Pawn pawn, GridPoint2 from, Orientation o)
    {
        System.err.println("    movePawn : " + from.x + ";" + from.y + " " + o);
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
        System.err.println("    rotatePawn : " + from.x + ";" + from.y + " " +o);
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
        System.err.println("    revertMoves()");
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

    public void promote(Pawn pawn, Pawn with)
    {
        GridPoint2 coords = getHexUnder(pawn);
        removePawnFrom(pawn, coords);
        setPawnAt(with, coords, pawn.getOrientation());
        activatedPawns.add(with);
    }
}
