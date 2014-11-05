package ch.asynk.tankontank.game;

import java.util.Iterator;

import com.badlogic.gdx.audio.Sound;
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
import ch.asynk.tankontank.engine.PossiblePaths;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.SpriteAnimation;
import ch.asynk.tankontank.engine.gfx.animations.SoundAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;


public abstract class Map extends Board
{
    private final Ctrl ctrl;

    public final Board.TileCollection possibleMoves;
    public final PossiblePaths possiblePaths;

    public final Board.PawnCollection moveablePawns;
    public final Board.PawnCollection possibleTargets;
    public final Board.PawnCollection attackAssists;
    public final Board.PawnCollection activatedPawns;
    public final Board.PawnCollection breakPawns;

    private final SpriteAnimation explosion;
    private final SpriteAnimation explosions;
    private final Sound moveSound;
    private final Sound attackSound;
    private Sound sound;
    private long soundId = -1;

    protected abstract void setup();

    public Map(final TankOnTank game, Board.Config cfg, String textureName)
    {
        super(game.factory, cfg, game.manager.get(textureName, Texture.class));
        this.ctrl = game.ctrl;
        this.explosion = new SpriteAnimation(game.manager.get("data/explosion.png", Texture.class), 10, 4, 40);
        this.explosions = new SpriteAnimation(game.manager.get("data/explosions.png", Texture.class), 16, 8, 15);
        this.moveSound = game.manager.get("sounds/move.mp3", Sound.class);
        this.attackSound = game.manager.get("sounds/attack.mp3", Sound.class);

        setup();

        possibleMoves = new TileSet(this, 40);
        possiblePaths = new PossiblePaths(this, 10, 20, 5, 10);
        moveablePawns = new PawnSet(this, 6);

        possibleTargets = new PawnSet(this, 10);
        attackAssists = new PawnSet(this, 6);
        activatedPawns = new PawnSet(this, 7);
        breakPawns = new PawnSet(this, 4);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        clearAll();
        explosion.dispose();
        explosions.dispose();
        moveSound.dispose();
        attackSound.dispose();
    }

    public void clearAll()
    {
        possibleMoves.clear();
        possibleTargets.clear();
        possiblePaths.clear();
        moveablePawns.clear();
        attackAssists.clear();
        activatedPawns.clear();
        breakPawns.clear();
    }

    public Hex getHexAt(float x, float y)
    {
        return (Hex) getTileAt(x, y);
    }

    public Hex getHex(int col, int row)
    {
        return (Hex) getTile(col, row);
    }

    public int collectPossibleMoves(Pawn pawn)
    {
        if (!pawn.canMove()) {
            possibleMoves.clear();
            return 0;
        }
        return collectPossibleMoves(pawn, possibleMoves);
    }

    public int togglePossiblePathHex(Hex hex)
    {
        return possiblePaths.toggleCtrlTile(hex);
    }

    public int collectPossibleTargets(Pawn pawn, Iterator<Pawn> foes)
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
        if (pawn.isHq() && !pawn.move.entryMove) {
            collectMoveAssists(pawn, moveablePawns);
        } else {
            moveablePawns.clear();
        }
        if (pawn.canMove())
            moveablePawns.add(pawn);
        return moveablePawns.size();
    }

    public int collectAttackAssists(Pawn pawn, Pawn target, Iterator<Pawn> units)
    {
        int s = collectAttackAssists(pawn, target, units, attackAssists);
        activatedPawns.add(pawn);
        return s;
    }

    public boolean toggleAttackAssist(Unit unit)
    {
        if (activatedPawns.contains(unit)) {
            activatedPawns.remove(unit);
            unit.hideAttack();
            unit.showAttackAssist();
            return false;
        } else {
            activatedPawns.add(unit);
            unit.showAttack();
            unit.hideAttackAssist();
            return true;
        }
    }

    public void collectAndShowMovesAndAssits(Pawn pawn)
    {
        hidePossibleMoves();
        hideMoveablePawns();
        collectPossibleMoves(pawn);
        collectMoveablePawns(pawn);
        showPossibleMoves();
        showMoveablePawns();
        activatedPawns.clear();
    }

    // ACTIONS

    public void promote(Pawn pawn, Pawn with)
    {
        removePawn(pawn);
        setPawnOnto(with, pawn.getTile(), pawn.getOrientation());
        activatedPawns.add(with);
    }

    public int movePawn(Pawn pawn, Orientation o)
    {
        possiblePaths.orientation = o;
        movePawn(pawn, possiblePaths, notifyDoneAnimation(pawn));

        return startMove(pawn);
    }

    public int rotatePawn(Pawn pawn, Orientation o)
    {
        rotatePawn(pawn, o, notifyDoneAnimation(pawn));

        return startMove(pawn);
    }

    public void revertMoves()
    {
        System.err.println("    revertMoves()");
        for (Pawn pawn : activatedPawns) {
            revertLastPawnMove(pawn, notifyDoneAnimation(pawn));
        }
        activatedPawns.clear();
    }

    private int startMove(Pawn pawn)
    {
        moveablePawns.remove(pawn);
        activatedPawns.add(pawn);
        sound = moveSound;
        soundId = sound.play(1.0f);
        return moveablePawns.size();
    }

    private RunnableAnimation notifyDoneAnimation(final Pawn pawn)
    {
        return RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                animationDone();
            }
        });
    }

    private void animationDone()
    {
        System.err.println("animation done");
        if (soundId >= 0)
            addAnimation( SoundAnimation.get(SoundAnimation.Action.FADE_OUT, sound, soundId, 0.5f));
        soundId = -1;
        ctrl.animationDone();
    }

    public boolean attackPawn(Pawn pawn, final Pawn target, int d1, int d2)
    {
        int activatedUnits = activatedPawns.size();
        int dice = d1 + d2;

        final boolean success;
        if (dice == 2) {
            pawn.attack.calculus = "2D6 -> (1 + 1) automatic failure";
            success = false;
        } else if (dice == 12) {
            pawn.attack.calculus = "2D6 -> (6 + 6) automatic success";
            success = true;
        } else {
            int flankAttacks = 0;
            for (Pawn assist : activatedPawns) {
                if (assist.isFlankAttack()) {
                    flankAttacks = 1;
                    break;
                }
            }
            pawn.attack.calculus = "2D6 -> (" + d1 + " + " + d2 + ") + " + activatedUnits + " + " + flankAttacks;
            int def = target.getTile().defenseFor(pawn, target, activatedPawns);
            success = ((dice + activatedUnits + flankAttacks) >= def);
        }
        System.err.println(pawn + "  attacks " + target + " : " + pawn.attack.calculus);

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

        breakPawns.clear();
        for (Pawn p : activatedPawns) {
            p.attack();
            if (p.isA(Unit.UnitType.INFANTRY))
                breakPawns.add(p);
        }

        if ((activatedPawns.size() == 1) && pawn.isA(Unit.UnitType.AT_GUN) && target.isHardTarget())
            activatedPawns.clear();

        addAnimation(seq);
        sound = attackSound;
        sound.play(1.0f);

        return success;
    }

    // SHOW / HIDE

    public void togglePathOverlay(Hex hex)
    {
        boolean enable= !hex.isOverlayEnabled(Hex.MOVE);
        enableOverlayOn(hex, Hex.MOVE, enable);
    }

    public void showPossibleMoves()     { possibleMoves.enable(Hex.AREA, true); }
    public void hidePossibleMoves()     { possibleMoves.enable(Hex.AREA, false); }
    public void showPossiblePaths()     { possiblePaths.enable(Hex.AREA, true); }
    public void hidePossiblePaths()     { possiblePaths.enable(Hex.AREA, false); }
    public void showFinalPath(Hex dst)  { possiblePaths.enable(Hex.MOVE, true); showMove(dst); }
    public void hideFinalPath(Hex dst)  { possiblePaths.enable(Hex.MOVE, false); hideMove(dst); }

    public void showMoveablePawns()     { moveablePawns.enable(Unit.MOVE, true); }
    public void hideMoveablePawns()     { moveablePawns.enable(Unit.MOVE, false); }
    public void showPossibleTargets()   { possibleTargets.enable(Unit.TARGET, true); }
    public void hidePossibleTargets()   { possibleTargets.enable(Unit.TARGET, false); }
    public void showAttackAssists()     { attackAssists.enable(Unit.MAY_FIRE, true); }
    public void hideAttackAssists()     { attackAssists.enable(Unit.FIRE, false);
                                          attackAssists.enable(Unit.MAY_FIRE, false); }
    public void showBreakPawns()        { breakPawns.enable(Unit.MOVE, true); }
    public void hideBreakPawns()        { breakPawns.enable(Unit.MOVE, false); }

    public void showObjective(Hex hex)  { enableOverlayOn(hex, Hex.OBJECTIVE, true); }
    public void hideObjective(Hex hex)  { enableOverlayOn(hex, Hex.OBJECTIVE, true); }

    public void selectHex(Hex hex)      { enableOverlayOn(hex, Hex.SELECT, true); }
    public void unselectHex(Hex hex)    { enableOverlayOn(hex, Hex.SELECT, false); }
    public void showMove(Hex hex)       { enableOverlayOn(hex, Hex.MOVE, true); }
    public void hideMove(Hex hex)       { enableOverlayOn(hex, Hex.MOVE, false); }
    public void showDirections(Hex hex) { enableOverlayOn(hex, Hex.DIRECTIONS, true); }
    public void hideDirections(Hex hex) { enableOverlayOn(hex, Hex.DIRECTIONS, false); }
    public void showOrientation(Hex hex, Orientation o) { enableOverlayOn(hex, Hex.ORIENTATION, o, true); }
    public void hideOrientation(Hex hex) { enableOverlayOn(hex, Hex.ORIENTATION, false); }
}
