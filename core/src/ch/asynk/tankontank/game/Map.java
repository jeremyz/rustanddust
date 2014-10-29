package ch.asynk.tankontank.game;

import java.util.Iterator;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.PawnSet;
import ch.asynk.tankontank.engine.TileSet;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.SpriteAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;


public abstract class Map extends Board
{
    private final Ctrl ctrl;

    public final Board.TileCollection possibleMoves;
    public final Board.TileCollection possiblePaths;
    public final Board.PawnCollection moveablePawns;
    public final Board.PawnCollection possibleTargets;
    public final Board.PawnCollection attackAssists;
    public final Board.PawnCollection activatedPawns;

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

        possibleMoves = new TileSet(this, 40);
        possiblePaths = new TileSet(this, 10);
        moveablePawns = new PawnSet(this, 6);

        possibleTargets = new PawnSet(this, 10);
        attackAssists = new PawnSet(this, 6);

        activatedPawns = new PawnSet(this, 7);
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
        moveablePawns.clear();
        attackAssists.clear();
        activatedPawns.clear();
    }

    public Hex getHexAt(float x, float y)
    {
        return (Hex) getTileAt(x, y);
    }

    public Hex getHex(int col, int row)
    {
        return (Hex) getTile(col, row);
    }

    public int buildPossibleMoves(Pawn pawn)
    {
        if (!pawn.canMove()) {
            possibleMoves.clear();
            return 0;
        }
        return buildPossibleMoves(pawn, possibleMoves);
    }

    public int buildPossiblePaths(Pawn pawn, Hex to)
    {
        return buildPossiblePaths(pawn, to, possiblePaths);
    }

    public int possiblePathsPointToggle(Hex hex)
    {
        return possiblePathsFilterToggle(hex, possiblePaths);
    }

    public int buildPossibleTargets(Pawn pawn, Iterator<Pawn> foes)
    {
        if (!pawn.canAttack()) {
            possibleTargets.clear();
            return 0;
        }
        // return collectPossibleTargets(pawn, possibleTargets);
        return collectPossibleTargets(pawn, foes, possibleTargets);
    }

    public int collectMoveablePawns(Pawn pawn)
    {
        if (pawn.isHq()) {
            collectMoveAssists(pawn, moveablePawns);
        } else {
            moveablePawns.clear();
        }
        if (pawn.canMove())
            moveablePawns.add(pawn);
        return moveablePawns.size();
    }

    public int buildAttackAssists(Pawn pawn, Pawn target, Iterator<Pawn> units)
    {
        int s = collectAttackAssists(pawn, target, units, attackAssists);
        activatedPawns.add(pawn);
        return s;
    }

    public void toggleAttackAssist(Unit unit)
    {
        if (activatedPawns.contains(unit)) {
            activatedPawns.remove(unit);
            unit.hideAttack();
            unit.showAttackAssist();
        } else {
            activatedPawns.add(unit);
            unit.showAttack();
            unit.hideAttackAssist();
        }
    }

    public void buildAndShowMovesAndAssits(Pawn pawn)
    {
        hidePossibleMoves();
        hideMoveablePawns();
        buildPossibleMoves(pawn);
        collectMoveablePawns(pawn);
        showPossibleMoves();
        showMoveablePawns();
        activatedPawns.clear();
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

        for (Pawn p : activatedPawns) {
            p.attack(target);
            System.err.println(pawn);
        }
        if ((activatedPawns.size() == 1) && pawn.isA(Unit.UnitType.AT_GUN) && target.isHardTarget())
            activatedPawns.clear();

        return success;
    }

    public int movePawn(Pawn pawn, Orientation o)
    {
        System.err.println("    movePawn : " + pawn.getTile() + " " + o);
        int cost = getPathCost(pawn, 0);
        movePawn(pawn, cost, o, RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                ctrl.animationDone();
            }
        }));

        return startMove(pawn);
    }

    public int rotatePawn(Pawn pawn, Orientation o)
    {
        System.err.println("    rotatePawn : " + pawn.getTile() + " " +o);
        rotatePawn(pawn, o, RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                ctrl.animationDone();
            }
        }));

        return startMove(pawn);
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

    private int startMove(Pawn pawn)
    {
        moveablePawns.remove(pawn);
        activatedPawns.add(pawn);
        return moveablePawns.size();
    }

    public void promote(Pawn pawn, Pawn with)
    {
        removePawn(pawn);
        setPawnOnto(with, pawn.getTile(), pawn.getOrientation());
        activatedPawns.add(with);
    }

    // SHOW / HIDE

    public void togglePathOverlay(Hex hex)
    {
        boolean enable= !hex.isOverlayEnabled(Hex.MOVE2);
        enableOverlayOn(hex, Hex.MOVE2, enable);
    }

    public void showPossibleMoves()     { possibleMoves.enable(Hex.MOVE1, true); }
    public void hidePossibleMoves()     { possibleMoves.enable(Hex.MOVE1, false); }
    public void showPossiblePaths()     { possiblePaths.enable(Hex.MOVE1, true); }
    public void hidePossiblePaths()     { possiblePaths.enable(Hex.MOVE1, false); }
    public void showFinalPath(Hex dst)  { possiblePaths.enable(Hex.MOVE2, true); }
    public void hideFinalPath(Hex dst)  { possiblePaths.enable(Hex.MOVE2, false); }

    public void showMoveablePawns()     { moveablePawns.enable(Unit.MOVE, true); }
    public void hideMoveablePawns()     { moveablePawns.enable(Unit.MOVE, false); }
    public void showPossibleTargets()   { possibleTargets.enable(Unit.TARGET, true); }
    public void hidePossibleTargets()   { possibleTargets.enable(Unit.TARGET, false); }
    public void showAttackAssists()     { attackAssists.enable(Unit.ATTACK_ASSIST, true); }
    public void hideAttackAssists()     { attackAssists.enable(Unit.ATTACK, false);
                                          attackAssists.enable(Unit.ATTACK_ASSIST, false); }


    public void selectHex(Hex hex)      { enableOverlayOn(hex, Hex.SELECT, true); }
    public void unselectHex(Hex hex)    { enableOverlayOn(hex, Hex.SELECT, false); }
    public void showDirections(Hex hex) { enableOverlayOn(hex, Hex.DIRECTIONS, true); }
    public void hideDirections(Hex hex) { enableOverlayOn(hex, Hex.DIRECTIONS, false); }
    public void showTarget(Hex hex)     { enableOverlayOn(hex, Hex.TARGET, true); }
    public void hideTarget(Hex hex)     { enableOverlayOn(hex, Hex.TARGET, false); }
    public void showAssist(Hex hex)     { enableOverlayOn(hex, Hex.ASSIST, true); }
    public void hideAssist(Hex hex)     { enableOverlayOn(hex, Hex.ASSIST, false); }
    public void showOrientation(Hex hex, Orientation o) { enableOverlayOn(hex, Hex.ORIENTATION, o, true); }
    public void hideOrientation(Hex hex) { enableOverlayOn(hex, Hex.ORIENTATION, false); }
}
