package ch.asynk.tankontank.game;

import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.engine.Meteorology;
import ch.asynk.tankontank.engine.PossiblePaths;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.ShotAnimation;
import ch.asynk.tankontank.engine.gfx.animations.SoundAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;


public abstract class Map extends Board
{
    private final Ctrl ctrl;

    private Random rand = new Random();

    public final HexSet possibleMoves;
    public final PossiblePaths possiblePaths;

    public final UnitList moveableUnits;
    public final UnitList possibleTargets;
    public final UnitList engagementAssists;
    public final UnitList activatedUnits;
    public final UnitList breakUnits;
    public final ObjectiveSet objectives;

    public final Meteorology meteorology;

    private final Sound moveSound;
    private Sound sound;
    private long soundId = -1;
    private Animation animationClosure;

    protected abstract void setup();

    public int d6()
    {
        return rand.nextInt(6) + 1;
    }

    public Map(final TankOnTank game, Board.Config cfg, String textureName)
    {
        super(game.factory, cfg, game.manager.get(textureName, Texture.class));
        this.ctrl = game.ctrl;
        this.moveSound = game.manager.get("sounds/move.mp3", Sound.class);
        ShotAnimation.init(
                game.manager.get("data/shots.png", Texture.class), 1, 7,
                game.manager.get("data/explosions.png", Texture.class), 16, 8,
                game.manager.get("sounds/shot.mp3", Sound.class),
                game.manager.get("sounds/short_shot.mp3", Sound.class),
                game.manager.get("sounds/explosion.mp3", Sound.class),
                game.manager.get("sounds/explosion_short.mp3", Sound.class)
                );

        setup();

        possibleMoves = new HexSet(this, 40);
        possiblePaths = new PossiblePaths(this, 10, 20, 5, 10);
        moveableUnits = new UnitList(6);

        possibleTargets = new UnitList(10);
        engagementAssists = new UnitList(6);
        activatedUnits = new UnitList(7);
        breakUnits = new UnitList(4);

        objectives = new ObjectiveSet(this, 4);

        meteorology = new Meteorology();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        clearAll();
        moveSound.dispose();
        ShotAnimation.free();
    }

    public void clearAll()
    {
        possibleMoves.clear();
        possibleTargets.clear();
        possiblePaths.clear();
        moveableUnits.clear();
        engagementAssists.clear();
        activatedUnits.clear();
        breakUnits.clear();
    }

    public Hex getHexAt(float x, float y)
    {
        return (Hex) getTileAt(x, y);
    }

    public Hex getHex(int col, int row)
    {
        return (Hex) getTile(col, row);
    }

    public void addObjective(int col, int row, Army army)
    {
        objectives.add(getHex(col, row), army, true);
    }

    public void addHoldObjective(int col, int row, Army army)
    {
        objectives.add(getHex(col, row), army, false);
    }

    public int collectPossibleMoves(Unit unit)
    {
        if (!unit.canMove()) {
            possibleMoves.clear();
            return 0;
        }
        return collectPossibleMoves(unit, possibleMoves.asTiles());
    }

    public int togglePossiblePathHex(Hex hex)
    {
        return possiblePaths.toggleCtrlTile(hex);
    }

    public int collectPossibleTargets(Unit unit, UnitList foes)
    {
        if (!unit.canEngage()) {
            possibleTargets.clear();
            return 0;
        }
        // return collectPossibleTargets(unit, possibleTargets);
        return collectPossibleTargets(unit, foes.asPawns(), possibleTargets.asPawns());
    }

    public int collectMoveableUnits(Unit unit)
    {
        if (unit.isHq() && !unit.movement.entryMove) {
            collectMoveAssists(unit, moveableUnits.asPawns());
        } else {
            moveableUnits.clear();
        }
        if (unit.canMove())
            moveableUnits.add(unit);
        return moveableUnits.size();
    }

    public int collectAttackAssists(Unit unit, Unit target, UnitList units)
    {
        int s = collectAttackAssists(unit, target, units.asPawns(), engagementAssists.asPawns());
        activatedUnits.add(unit);
        return s;
    }

    public boolean toggleAttackAssist(Unit unit)
    {
        if (activatedUnits.contains(unit)) {
            activatedUnits.remove(unit);
            unit.hideAttack();
            unit.showAttackAssist();
            return false;
        } else {
            activatedUnits.add(unit);
            unit.showAttack();
            unit.hideAttackAssist();
            return true;
        }
    }

    public void collectAndShowMovesAndAssits(Unit unit)
    {
        hidePossibleMoves();
        hideMoveableUnits();
        collectPossibleMoves(unit);
        collectMoveableUnits(unit);
        showPossibleMoves();
        showMoveableUnits();
        activatedUnits.clear();
    }

    public int animationsDone()
    {
        if (animationClosure != null) {
            addAnimation(animationClosure);
            animationClosure = null;
            return 1;
        }
        return 0;
    }

    // ACTIONS

    public void actionDone()
    {
        objectives.forget();
    }

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
        objectives.claim(to, unit.getArmy());
        return true;
    }

    public void leaveBoard(Unit unit)
    {
        Hex hex = unit.getHex();
        if (unit.movement.entryMove) {
            objectives.revert();
            unit.reset();
        }
        removePawn(unit);
        activatedUnits.add(unit);
    }

    public int moveUnit(Unit unit, Orientation o)
    {
        possiblePaths.orientation = o;
        movePawn(unit, possiblePaths, notifyDoneAnimation(unit), objectives);

        return startMove(unit);
    }

    public void revertMoves()
    {
        TankOnTank.debug("    revertMoves()");
        for (Unit unit: activatedUnits) {
            revertLastPawnMove(unit, notifyDoneAnimation(unit));
        }
        activatedUnits.clear();
        objectives.revert();
    }

    private int startMove(Unit unit)
    {
        moveableUnits.remove(unit);
        activatedUnits.add(unit);
        sound = moveSound;
        soundId = sound.play(ctrl.cfg.fxVolume);
        return moveableUnits.size();
    }

    private RunnableAnimation notifyDoneAnimation(final Unit unit)
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
            addAnimation( SoundAnimation.get(SoundAnimation.Action.FADE_OUT, sound, soundId, ctrl.cfg.fxVolume, 0.5f));
        soundId = -1;
        ctrl.animationDone();
    }

    private boolean resolveFight(Unit unit, final Unit target)
    {
        int d1 = d6();
        int d2 = d6();
        int die = d1 + d2;

        boolean success = false;
        if (die == 2) {
            ctrl.hud.engagementSummary(d1, d2, 0, 0, 0, 0, 0, target.toString() + " is destroyed");
            success = false;
        } else if (die == 12) {
            ctrl.hud.engagementSummary(d1, d2, 0, 0, 0, 0, 0, target.toString() + " resisted the assault");
            success = true;
        } else {

            int distance = 0;
            boolean night = (meteorology.day == Meteorology.Day.NIGHT);
            boolean flankAttack = false;
            boolean terrainBonus = true;

            for (Pawn assist : activatedUnits) {
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

            int cnt = activatedUnits.size();
            int def = target.getDefense(unit.getTile());
            int flk = (flankAttack ? Unit.FLANK_ATTACK_BONUS : 0);
            int tdf = (terrainBonus ? target.getTile().defense() : 0);
            int wdf = 0;
            if (night) {
            if (distance > 3)
                wdf = 3;
            else if (distance > 2)
                wdf = 2;
            else if (distance > 1)
                wdf = 1;
            }
            int s1 = (die + cnt + flk);
            int s2 = (def + tdf + wdf);
            success = (s1 >= s2);

            ctrl.hud.engagementSummary(d1, d2, cnt, flk, def, tdf, wdf,
                    target.toString() + (success ? " is destroyed" : " resisted the assault"));
        }
        return success;
    }

    public void addEngagementAnimation(Unit target)
    {
        ShotAnimation.resetSound();
        Hex to = target.getHex();
        for (Unit u : activatedUnits) {
            Hex from = u.getHex();
            AnimationSequence seq = AnimationSequence.get(2);
            seq.addAnimation(ShotAnimation.get(ctrl.cfg.fxVolume, (u.getWidth() / 2.f), from.getX(), from.getY(), to.getX(), to.getY()));
            seq.addAnimation(notifyDoneAnimation(target));
            addAnimation(seq);
        }
    }

    public boolean engageUnit(Unit unit, final Unit target)
    {
        boolean mayReroll = false;
        for (Unit assist : activatedUnits) {
            if (assist.isAce())
                mayReroll = true;
        }

        boolean success;
        success = resolveFight(unit, target);
        if (!success && mayReroll) {
            TankOnTank.debug("Reroll");
            success = resolveFight(unit, target);
        }

        breakUnits.clear();
        for (Unit u : activatedUnits) {
            u.engage();
            if (u.isA(Unit.UnitType.INFANTRY))
                breakUnits.add(u);
        }

        if ((activatedUnits.size() == 1) && unit.isA(Unit.UnitType.AT_GUN) && target.isHardTarget())
            activatedUnits.clear();

        if (success) {
            animationClosure = RunnableAnimation.get(target, new Runnable() {
                @Override
                public void run() {
                    objectives.unclaim(target.getHex());
                    removePawn(target);
                    animationDone();
                }
            });
        }

        addEngagementAnimation(target);

        return success;
    }

    // SHOW / HIDE

    public void togglePathOverlay(Hex hex)
    {
        boolean enable= !hex.isOverlayEnabled(Hex.MOVE);
        enableOverlayOn(hex, Hex.MOVE, enable);
    }

    private void showUnitsOverlay(UnitList units, int overlay, boolean on)
    {
        for (Unit unit : units)
            unit.enableOverlay(overlay, on);
    }

    public void showMoveableUnits()     { showUnitsOverlay(moveableUnits, Unit.MOVE, true); }
    public void hideMoveableUnits()     { showUnitsOverlay(moveableUnits, Unit.MOVE, false); }
    public void showPossibleTargets()   { showUnitsOverlay(possibleTargets, Unit.TARGET, true); }
    public void hidePossibleTargets()   { showUnitsOverlay(possibleTargets, Unit.TARGET, false); }
    public void showAttackAssists()     { showUnitsOverlay(engagementAssists, Unit.MAY_FIRE, true); }
    public void hideAttackAssists()     { showUnitsOverlay(engagementAssists, Unit.FIRE, false);
                                          showUnitsOverlay(engagementAssists, Unit.MAY_FIRE, false); }
    public void showBreakUnits()        { showUnitsOverlay(breakUnits, Unit.MOVE, true); }
    public void hideBreakUnits()        { showUnitsOverlay(breakUnits, Unit.MOVE, false); }

    public void showPossibleMoves()     { possibleMoves.enable(Hex.AREA, true); }
    public void hidePossibleMoves()     { possibleMoves.enable(Hex.AREA, false); }
    public void showPossiblePaths()     { possiblePaths.enable(Hex.AREA, true); }
    public void hidePossiblePaths()     { possiblePaths.enable(Hex.AREA, false); }
    public void showPath(Hex dst)       { possiblePaths.enable(Hex.MOVE, true); showMove(dst); }
    public void hidePath(Hex dst)       { possiblePaths.enable(Hex.MOVE, false); hideMove(dst); }

    public void selectHex(Hex hex)      { enableOverlayOn(hex, Hex.SELECT, true); }
    public void unselectHex(Hex hex)    { enableOverlayOn(hex, Hex.SELECT, false); }
    public void showMove(Hex hex)       { enableOverlayOn(hex, Hex.MOVE, true); }
    public void hideMove(Hex hex)       { enableOverlayOn(hex, Hex.MOVE, false); }
    public void showDirections(Hex hex) { enableOverlayOn(hex, Hex.DIRECTIONS, true); }
    public void hideDirections(Hex hex) { enableOverlayOn(hex, Hex.DIRECTIONS, false); }
    public void showOrientation(Hex hex, Orientation o) { enableOverlayOn(hex, Hex.ORIENTATION, o, true); }
    public void hideOrientation(Hex hex) { enableOverlayOn(hex, Hex.ORIENTATION, false); }

    public void hideObjective(Hex hex)
    {
        enableOverlayOn(hex, Hex.OBJECTIVE, false);
        enableOverlayOn(hex, Hex.OBJECTIVE_US, false);
        enableOverlayOn(hex, Hex.OBJECTIVE_GE, false);
    }

    public void showObjective(Hex hex, Army army)
    {
        switch(army) {
            case GE:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, true);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, false);
                enableOverlayOn(hex, Hex.OBJECTIVE, false);
                break;
            case US:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, false);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, true);
                enableOverlayOn(hex, Hex.OBJECTIVE, false);
                break;
            case NONE:
            default:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, false);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, false);
                enableOverlayOn(hex, Hex.OBJECTIVE, true);
                break;
        }
    }
}
