package ch.asynk.tankontank.game;

import java.util.List;
import java.util.Random;

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
import ch.asynk.tankontank.engine.Meteorology;
import ch.asynk.tankontank.engine.PossiblePaths;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.SpriteAnimation;
import ch.asynk.tankontank.engine.gfx.animations.SoundAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;


public abstract class Map extends Board
{
    private final Ctrl ctrl;

    private Random rand = new Random();

    public final Board.TileCollection possibleMoves;
    public final PossiblePaths possiblePaths;

    public final Board.PawnCollection moveablePawns;
    public final Board.PawnCollection possibleTargets;
    public final Board.PawnCollection engagementAssists;
    public final Board.PawnCollection activatedPawns;
    public final Board.PawnCollection breakPawns;

    public final Meteorology meteorology;

    private final SpriteAnimation explosion;
    private final SpriteAnimation explosions;
    private final Sound moveSound;
    private final Sound engagementSound;
    private Sound sound;
    private long soundId = -1;

    protected abstract void setup();

    public int d6()
    {
        return rand.nextInt(6) + 1;
    }

    public Map(final TankOnTank game, Board.Config cfg, String textureName)
    {
        super(game.factory, cfg, game.manager.get(textureName, Texture.class));
        this.ctrl = game.ctrl;
        this.explosion = new SpriteAnimation(game.manager.get("data/explosion.png", Texture.class), 10, 4, 40);
        this.explosions = new SpriteAnimation(game.manager.get("data/explosions.png", Texture.class), 16, 8, 15);
        this.moveSound = game.manager.get("sounds/move.mp3", Sound.class);
        this.engagementSound = game.manager.get("sounds/attack.mp3", Sound.class);

        setup();

        possibleMoves = new TileSet(this, 40);
        possiblePaths = new PossiblePaths(this, 10, 20, 5, 10);
        moveablePawns = new PawnSet(this, 6);

        possibleTargets = new PawnSet(this, 10);
        engagementAssists = new PawnSet(this, 6);
        activatedPawns = new PawnSet(this, 7);
        breakPawns = new PawnSet(this, 4);

        meteorology = new Meteorology();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        clearAll();
        explosion.dispose();
        explosions.dispose();
        moveSound.dispose();
        engagementSound.dispose();
    }

    public void clearAll()
    {
        possibleMoves.clear();
        possibleTargets.clear();
        possiblePaths.clear();
        moveablePawns.clear();
        engagementAssists.clear();
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

    public int collectPossibleMoves(Unit unit)
    {
        if (!unit.canMove()) {
            possibleMoves.clear();
            return 0;
        }
        return collectPossibleMoves(unit, possibleMoves);
    }

    public int togglePossiblePathHex(Hex hex)
    {
        return possiblePaths.toggleCtrlTile(hex);
    }

    public int collectPossibleTargets(Unit unit, List<Pawn> foes)
    {
        if (!unit.canEngage()) {
            possibleTargets.clear();
            return 0;
        }
        // return collectPossibleTargets(unit, possibleTargets);
        return collectPossibleTargets(unit, foes, possibleTargets);
    }

    public int collectMoveablePawns(Unit unit)
    {
        if (unit.isHq() && !unit.movement.entryMove) {
            collectMoveAssists(unit, moveablePawns);
        } else {
            moveablePawns.clear();
        }
        if (unit.canMove())
            moveablePawns.add(unit);
        return moveablePawns.size();
    }

    public int collectAttackAssists(Unit unit, Unit target, List<Pawn> units)
    {
        int s = collectAttackAssists(unit, target, units, engagementAssists);
        activatedPawns.add(unit);
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

    public void collectAndShowMovesAndAssits(Unit unit)
    {
        hidePossibleMoves();
        hideMoveablePawns();
        collectPossibleMoves(unit);
        collectMoveablePawns(unit);
        showPossibleMoves();
        showMoveablePawns();
        activatedPawns.clear();
    }

    // ACTIONS

    public boolean enterBoard(Unit unit, Hex to, int allowedMoves)
    {
        Orientation entry = findBestEntry(unit, to, allowedMoves);
        if (entry == Orientation.KEEP)
            return false;
        return enterBoard(unit, to, entry);
    }

    public boolean enterBoard(Unit unit, Hex to, Orientation entry)
    {
        unit.enterBoard(to, entry);
        setPawnOnto(unit, to, entry);
        return true;
    }

    public void leaveBoard(Unit unit)
    {
        removePawn(unit);
        activatedPawns.add(unit);
    }

    public int movePawn(Unit unit, Orientation o)
    {
        possiblePaths.orientation = o;
        movePawn(unit, possiblePaths, notifyDoneAnimation(unit));

        return startMove(unit);
    }

    public void revertMoves()
    {
        TankOnTank.debug("    revertMoves()");
        for (Pawn pawn: activatedPawns) {
            revertLastPawnMove(pawn, notifyDoneAnimation(pawn));
        }
        activatedPawns.clear();
    }

    private int startMove(Unit unit)
    {
        moveablePawns.remove(unit);
        activatedPawns.add(unit);
        sound = moveSound;
        soundId = sound.play(1.0f);
        return moveablePawns.size();
    }

    private RunnableAnimation notifyDoneAnimation(final Pawn unit)
    {
        return RunnableAnimation.get(unit, new Runnable() {
            @Override
            public void run() {
                animationDone();
            }
        });
    }

    private void animationDone()
    {
        TankOnTank.debug("animation done");
        if (soundId >= 0)
            addAnimation( SoundAnimation.get(SoundAnimation.Action.FADE_OUT, sound, soundId, 0.5f));
        soundId = -1;
        ctrl.animationDone();
    }

    private boolean resolve(Unit unit, final Unit target)
    {
        int d1 = d6();
        int d2 = d6();
        int dice = d1 + d2;

        if (dice == 2) {
            unit.engagement.calculus = "2D6 -> (1 + 1) automatic failure";
            return false;
        } else if (dice == 12) {
            unit.engagement.calculus = "2D6 -> (6 + 6) automatic success";
            return true;
        } else {

            int distance = 0;
            boolean night = (meteorology.day == Meteorology.Day.NIGHT);
            boolean flankAttack = false;
            boolean terrainBonus = true;

            for (Pawn assist : activatedPawns) {
                if (assist.isFlankAttack())
                    flankAttack = true;
                if (assist.isA(Unit.UnitType.INFANTRY))
                    terrainBonus = false;
                if (night) {
                    int d = distance(assist.getTile(), target.getTile());
                    if (d > distance)
                        distance = d;
                }
            }

            int cnt = activatedPawns.size();
            int def = target.getDefense(unit.getTile());
            int flk = (flankAttack ? Unit.FLANK_ATTACK_BONUS : 0);
            int tdf = (terrainBonus ? unit.getTile().defense() : 0);
            int wdf = 0;
            if (night) {
            if (distance > 3)
                wdf = 3;
            else if (distance > 2)
                wdf = 2;
            else if (distance > 1)
                wdf = 1;
            }

            unit.engagement.calculus = "2D6(" + d1 + " + " + d2 + ") + " + cnt + " + " + flk + " >= " + def + " + " + tdf + " + " + wdf;
            if (night)
                unit.engagement.calculus += " + " + wdf;
            return ((dice + cnt + flk) >= (def + tdf + wdf));
        }
    }

    public boolean engagePawn(Unit unit, final Unit target)
    {
        boolean mayReroll = false;
        for (Pawn assist : activatedPawns) {
            if (((Unit) assist).isAce())
                mayReroll = true;
        }

        boolean success;
        success = resolve(unit, target);
        if (!success && mayReroll) {
            TankOnTank.debug("Reroll");
            success = resolve(unit, target);
        }

        TankOnTank.debug(unit + "  engagements " + target + " : " + unit.engagement.calculus);

        AnimationSequence seq = AnimationSequence.get(2);
        if (success) {
            explosions.init(1, target.getCenter().x, target.getCenter().y);
            seq.addAnimation(explosions);
            seq.addAnimation(notifyDoneAnimation(unit));
        } else {
            explosion.init(1, target.getCenter().x, target.getCenter().y);
            seq.addAnimation(explosion);
            seq.addAnimation(RunnableAnimation.get(unit, new Runnable() {
                @Override
                public void run() {
                    animationDone();
                }
            }));
        }

        breakPawns.clear();
        for (Pawn p : activatedPawns) {
            p.engage();
            if (p.isA(Unit.UnitType.INFANTRY))
                breakPawns.add(p);
        }

        if ((activatedPawns.size() == 1) && unit.isA(Unit.UnitType.AT_GUN) && target.isHardTarget())
            activatedPawns.clear();

        addAnimation(seq);
        sound = engagementSound;
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
    public void showPath(Hex dst)       { possiblePaths.enable(Hex.MOVE, true); showMove(dst); }
    public void hidePath(Hex dst)       { possiblePaths.enable(Hex.MOVE, false); hideMove(dst); }

    public void showMoveablePawns()     { moveablePawns.enable(Unit.MOVE, true); }
    public void hideMoveablePawns()     { moveablePawns.enable(Unit.MOVE, false); }
    public void showPossibleTargets()   { possibleTargets.enable(Unit.TARGET, true); }
    public void hidePossibleTargets()   { possibleTargets.enable(Unit.TARGET, false); }
    public void showAttackAssists()     { engagementAssists.enable(Unit.MAY_FIRE, true); }
    public void hideAttackAssists()     { engagementAssists.enable(Unit.FIRE, false);
                                          engagementAssists.enable(Unit.MAY_FIRE, false); }
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
