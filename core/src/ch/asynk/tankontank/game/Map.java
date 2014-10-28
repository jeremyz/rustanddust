package ch.asynk.tankontank.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.SpriteAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public abstract class Map extends Board
{
    private final Ctrl ctrl;

    public final Board.TileCollection possibleMoves;
    public final Board.TileCollection possibleTargets;
    public final Board.TileCollection possiblePaths;
    public final Board.TileCollection moveAssists;
    public final Board.TileCollection attackAssists;
    public final ArrayList<Pawn> activablePawns = new ArrayList<Pawn>(7);  // PawnSet
    public final ArrayList<Pawn> activatedPawns = new ArrayList<Pawn>(7);  // PawnSet

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
        possibleMoves = new HexList(this, Hex.MOVE1, 40);
        possiblePaths = new HexList(this, Hex.MOVE1, 10);       // Hex.MOVE2
        possibleTargets = new HexList(this, Hex.TARGET, 10);
        moveAssists = new HexList(this, Hex.ASSIST, 6);
        attackAssists = new HexList(this, Hex.ASSIST, 6);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        clearAll();
    }

    public void clearAll()
    {
        possibleMoves.clear();
        possibleTargets.clear();
        possiblePaths.clear();
        moveAssists.clear();
        attackAssists.clear();
        activablePawns.clear();
        activatedPawns.clear();
    }

    public Hex getHex(int col, int row)
    {
        return (Hex) getTile(col, row);
    }

    public void selectHex(Hex hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.SELECT, enable);
    }

    public void showAssist(Hex hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.ASSIST, enable);
    }

    public void showTarget(Hex hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.TARGET, enable);
    }

    public void showPossiblePaths(boolean enable, boolean keepFinal)
    {
        if (keepFinal) {
            possiblePaths.enable(Hex.MOVE1, enable);
        } else {
            possiblePaths.enable(Hex.MOVE1, enable);
            possiblePaths.enable(Hex.MOVE2, false);
        }
    }

    public void showFinalPath(Hex dst, boolean enable)
    {
        possiblePaths.enable(Hex.MOVE1, false);
        possiblePaths.enable(Hex.MOVE2, enable);
    }

    public void showDirections(Hex hex, boolean enable)
    {
        enableOverlayOn(hex, Hex.DIRECTIONS, enable);
    }

    public void showOrientation(Hex hex, boolean enable, Orientation o)
    {
        enableOverlayOn(hex, Hex.ORIENTATION, enable, o);
    }

    public void togglePathOverlay(Hex hex)
    {
        boolean enable= !hex.isOverlayEnabled(Hex.MOVE2);
        enableOverlayOn(hex, Hex.MOVE2, enable);
    }

    public int buildPossibleMoves(Pawn pawn)
    {
        return buildPossibleMoves(pawn, possibleMoves);
    }

    public int buildPossibleTargets(Pawn pawn, Iterator<Pawn> foes)
    {
        // return buildPossibleTargets(pawn, possibleTargets);
        return buildPossibleTargets(pawn, possibleTargets);
    }

    public int buildMoveAssists(Pawn pawn)
    {
        if (!pawn.isHq()) {
            moveAssists.clear();
            return 0;
        }
        return buildMoveAssists(pawn, moveAssists);
    }

    public int buildAttackAssists(Pawn pawn, Pawn target, Iterator<Pawn> units)
    {
        int s = buildAttackAssists(pawn, target, units, attackAssists);
        activatedPawns.add(pawn);
        attackAssists.getPawns(activablePawns);
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

    public void buildAndShowMovesAndAssits(Pawn pawn)
    {
        possibleMoves.hide();
        moveAssists.hide();
        activablePawns.clear();
        activatedPawns.clear();
        buildPossibleMoves(pawn);
        buildMoveAssists(pawn);
        activablePawns.add(pawn);
        moveAssists.getPawns(activablePawns);
        possibleMoves.show();
        moveAssists.show();
    }

    public int buildPossiblePaths(Pawn pawn, Hex to)
    {
        return buildPossiblePaths(pawn, to, possiblePaths);
    }

    public int possiblePathsPointToggle(Hex hex)
    {
        return possiblePathsFilterToggle(hex, possiblePaths);
    }

    public boolean attackPawn(Pawn pawn, final Pawn target, int dice)
    {
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
            success = ((dice + activatedUnits + flankAttacks) >= target.getTile().defenseFor(target, activatedPawns));
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
        if ((activatedPawns.size() == 1) && pawn.isA(Unit.UnitType.AT_GUN) && target.isHardTarget())
            activatedPawns.clear();

        return success;
    }

    public int movePawn(Pawn pawn, Orientation o)
    {
        Tile from = pawn.getTile();
        System.err.println("    movePawn : " + from + " " + o);
        int cost = getPathCost(pawn, 0);
        movePawn(pawn, cost, o, RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                ctrl.animationDone();
            }
        }));

        return startMove(pawn, from);
    }

    public int rotatePawn(Pawn pawn, Orientation o)
    {
        Tile from = pawn.getTile();
        System.err.println("    rotatePawn : " + from + " " +o);
        rotatePawn(pawn, o, RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                ctrl.animationDone();
            }
        }));

        return startMove(pawn, from);
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

    private int startMove(Pawn pawn, Tile from) {
        moveAssists.remove(from);
        activablePawns.remove(pawn);
        activatedPawns.add(pawn);
        return activablePawns.size();
    }

    public void promote(Pawn pawn, Pawn with)
    {
        removePawn(pawn);
        setPawnOnto(with, pawn.getTile(), pawn.getOrientation());
        activatedPawns.add(with);
    }
}
