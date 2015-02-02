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
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.Faction;
import ch.asynk.tankontank.engine.Move;
import ch.asynk.tankontank.engine.SelectedTile;
import ch.asynk.tankontank.engine.ObjectiveSet;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.engine.Meteorology;
import ch.asynk.tankontank.engine.PathBuilder;
import ch.asynk.tankontank.engine.gfx.Moveable;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.DiceAnimation;
import ch.asynk.tankontank.engine.gfx.animations.FireAnimation;
import ch.asynk.tankontank.engine.gfx.animations.TankFireAnimation;
import ch.asynk.tankontank.engine.gfx.animations.InfantryFireAnimation;
import ch.asynk.tankontank.engine.gfx.animations.PromoteAnimation;
import ch.asynk.tankontank.engine.gfx.animations.DestroyAnimation;
import ch.asynk.tankontank.engine.gfx.animations.SoundAnimation;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;
import ch.asynk.tankontank.engine.gfx.animations.MoveToAnimation.MoveToAnimationCb;

import ch.asynk.tankontank.ui.Position;

public abstract class Map extends Board implements MoveToAnimationCb, ObjectiveSet.ObjectiveCb
{
    private final Ctrl ctrl;

    private Random rand = new Random();

    public final HexSet possibleMoves;
    public final PathBuilder pathBuilder;

    public final UnitList moveableUnits;
    public final UnitList possibleTargets;
    public final UnitList engagementAssists;
    public final UnitList activatedUnits;
    public final UnitList breakUnits;
    public final ObjectiveSet objectives;

    public final Meteorology meteorology;

    private final DestroyAnimation destroy;
    private final Sound tankMoveSound;
    private final Sound infantryMoveSound;
    private Sound sound;
    private long soundId = -1;
    private Animation animationClosure;

    protected abstract void setup();

    public class Engagement
    {
        public Army attacker;
        public Army defender;
        public boolean success;
        public int d1;
        public int d2;
        public int d3;
        public int d4;
        public int unitCount;
        public int flankBonus;
        public int unitDefense;
        public int terrainDefense;
        public int weatherDefense;
        public int attack;
        public int defense;

        public void set(int d1, int d2, int d3, int d4, int cnt, int flk, int def, int tdf, int wdf)
        {
            this.d1 = d1;
            this.d2 = d2;
            this.d3 = d3;
            this.d4 = d4;
            this.unitCount = cnt;
            this.flankBonus = flk;
            this.unitDefense = def;
            this.terrainDefense = tdf;
            this.weatherDefense = wdf;
            if (d3 == 0)
                this.attack = (d1 + d2 + unitCount + flankBonus);
            else
                this.attack = (d3 + d4 + unitCount + flankBonus);
            this.defense = (unitDefense + terrainDefense + weatherDefense);
        }
    }
    private Engagement engagement;

    public int d6()
    {
        return rand.nextInt(6) + 1;
    }

    public Map(final TankOnTank game, Board.Config cfg, String textureName)
    {
        super(game.factory, cfg, game.manager.get(textureName, Texture.class),
                new SelectedTile(game.manager.get("data/hex.png", Texture.class), new float[] {.2f, .1f, .1f, .1f, .2f, .1f} ));
        this.ctrl = game.ctrl;
        this.destroy = new DestroyAnimation();
        this.tankMoveSound = game.manager.get("sounds/tank_move.mp3", Sound.class);
        this.infantryMoveSound = game.manager.get("sounds/infantry_move.mp3", Sound.class);
        DiceAnimation.init(game.manager.get("data/dice.png", Texture.class), 16, 9, game.manager.get("sounds/dice.mp3", Sound.class));
        PromoteAnimation.init(game.manager.get("data/hud.atlas", TextureAtlas.class),
                game.manager.get("sounds/promote_us.mp3", Sound.class),
                game.manager.get("sounds/promote_ge.mp3", Sound.class));
        FireAnimation.init(
                game.manager.get("data/infantry_fire.png", Texture.class), 1, 8,
                game.manager.get("data/tank_fire.png", Texture.class), 1, 8,
                game.manager.get("data/explosions.png", Texture.class), 16, 8,
                game.manager.get("sounds/infantry_fire.mp3", Sound.class),
                game.manager.get("sounds/tank_fire.mp3", Sound.class),
                game.manager.get("sounds/tank_fire_short.mp3", Sound.class),
                game.manager.get("sounds/explosion.mp3", Sound.class),
                game.manager.get("sounds/explosion_short.mp3", Sound.class)
                );

        setup();

        possibleMoves = new HexSet(this, 40);
        pathBuilder = new PathBuilder(this, 10, 20, 5, 10);
        moveableUnits = new UnitList(6);

        possibleTargets = new UnitList(10);
        engagementAssists = new UnitList(6);
        activatedUnits = new UnitList(7);
        breakUnits = new UnitList(4);

        objectives = new ObjectiveSet(this, 4);

        meteorology = new Meteorology();
        engagement = new Engagement();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        clearAll();
        destroy.dispose();
        pathBuilder.dispose();
        DiceAnimation.free();
        PromoteAnimation.free();
        FireAnimation.free();
    }

    public void clearAll()
    {
        possibleMoves.clear();
        possibleTargets.clear();
        pathBuilder.clear();
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
        addObjective(col, row, army, true);
    }

    public void addHoldObjective(int col, int row, Army army)
    {
        addObjective(col, row, army, false);
    }

    private void addObjective(int col, int row, Army army, boolean persistent)
    {
        Hex hex = getHex(col, row);
        objectives.add(hex, army, persistent);
        showObjective(hex, army, !persistent);
    }

    private void claim(Hex hex, Army army)
    {
        showObjective(hex, objectives.claim(hex, army));
    }

    private void unclaim(Hex hex)
    {
        showObjective(hex, objectives.unclaim(hex));
    }

    public int collectPossibleMoves(Unit unit)
    {
        if (!unit.canMove()) {
            possibleMoves.clear();
            return 0;
        }
        return collectPossibleMoves(unit, possibleMoves.asTiles());
    }

    public int togglePathBuilderHex(Hex hex)
    {
        return pathBuilder.toggleCtrlTile(hex);
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
        if (unit.canHQMove()) {
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

    // -> implement MoveToAnimationCb

    @Override
    public void moveToAnimationEnter(Moveable moveable, float x, float y, float r)
    {
        claim(getHexAt(x, y), (Army) moveable.getFaction());
    }

    @Override
    public void moveToAnimationLeave(Moveable moveable, float x, float y, float r)
    {
        unclaim(getHexAt(x, y));
    }

    @Override
    public void moveToAnimationDone(Moveable moveable, float x, float y, float r)
    {
    }

    // <- implement MoveToAnimationCb

    private int process(Unit unit, Move move)
    {
        TankOnTank.debug("Process", String.format("%s %s", move.type, move.toString()));

        int r = 1;

        switch(move.type) {
            case REGULAR:
                initMove(unit);
                movePawn(unit, move, notifyDoneAnimation(unit), this);
                r = moveableUnits.size();
                break;
            case EXIT:
                initMove(unit);
                movePawn(unit, move, notifyDoneAnimation(unit), this);
                ctrl.player.unitWithdraw(unit);
                r = moveableUnits.size();
                break;
            case SET:
                // FIXME SET -> activatedUnits.add(unit); ??
                setPawnOnto(unit, move);
                ctrl.player.unitEntry(unit);
                claim((Hex) move.to, unit.getArmy());
                break;
            case ENTER:
                // FIXME ENTER -> activatedUnits.add(unit); ??
                enterPawn(unit, move);
                ctrl.player.unitEntry(unit);
                claim((Hex) move.to, unit.getArmy());
                break;
            default:
                System.err.println(String.format("process wrong type %s", move.type));
                r = -1;
                break;
        }

        return r;
    }

    // STATES ENTRY ->

    public void turnDone()
    {
        objectives.forget();
    }

    public void actionDone()
    {
        objectives.forget();
    }

    public boolean enterBoard(Unit unit, Hex to, int allowedMoves)
    {
        Orientation entry = findBestEntry(unit, to, allowedMoves);
        if (entry == Orientation.KEEP)
            return false;

        return (process(unit, Move.getEnter(unit, to, entry)) == 1);
    }

    public boolean setOnBoard(Unit unit, Hex to, Orientation entry)
    {
        return (process(unit, Move.getSet(unit, to, entry)) == 1);
    }

    public int exitBoard(Unit unit)
    {
        return process(unit, pathBuilder.getExitMove());
    }

    public void promoteUnit(final Player player, final Unit unit)
    {
        activatedUnits.add(unit);

        Hex hex = unit.getHex();
        AnimationSequence seq = AnimationSequence.get(2);
        seq.addAnimation(PromoteAnimation.get((unit.getArmy() == Army.US), ctrl.mapTouch.x, ctrl.mapTouch.y, hex.getX(), hex.getY(), ctrl.cfg.fxVolume));
        seq.addAnimation ( RunnableAnimation.get(unit, new Runnable() {
            @Override
            public void run() {
                player.promote(unit);
                animationDone();
            }
        }));
        addAnimation(seq);
    }

    public int moveUnit(Unit unit)
    {
        return process(unit, pathBuilder.getMove());
    }

    public void revertMoves()
    {
        TankOnTank.debug("    revertMoves()");
        for (Unit unit: activatedUnits) {
            revertLastPawnMove(unit, notifyDoneAnimation(unit));
        }
        activatedUnits.clear();
        objectives.revert(this);
    }

    public void revertEnter(Unit unit)
    {
        TankOnTank.debug("    revertEnter()"+ unit);
        unit.reset();
        removePawn(unit);
        objectives.revert(this);
        ctrl.player.revertUnitEntry(unit);
    }

    // STATES ENTRY <-

    private void initMove(Unit unit)
    {
        moveableUnits.remove(unit);
        activatedUnits.add(unit);
        playMoveSound(unit);
    }

    private void playMoveSound(Unit unit)
    {
        if (unit.isA(Unit.UnitType.INFANTRY))
            sound = infantryMoveSound;
        else
            sound = tankMoveSound;
        soundId = sound.play(ctrl.cfg.fxVolume);
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
        if (soundId >= 0)
            addAnimation( SoundAnimation.get(SoundAnimation.Action.FADE_OUT, sound, soundId, ctrl.cfg.fxVolume, 0.5f));
        soundId = -1;
        ctrl.animationDone();
    }

    private boolean resolveFight(Unit unit, final Unit target, boolean mayReroll)
    {
        int d1 = d6();
        int d2 = d6();
        int d3 = 0;
        int d4 = 0;
        int die = d1 + d2;

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

        boolean success = false;
        if (die == 2) {
            success = false;
        } else if (die == 12) {
            success = true;
        } else {
            success = (s1 >= s2);
        }
        if (!success && mayReroll) {
            d3 = d6();
            d4 = d6();
            die = d3 + d4;
            TankOnTank.debug(String.format("Reroll: (%d %d -> %d %d)", d1, d2, d3, d4));
            s1 = (die + cnt + flk);
            if (die == 2) {
                success = false;
            } else if (die == 12) {
                success = true;
            } else {
                success = (s1 >= s2);
            }
        }

        engagement.set(d1, d2, d3, d4, cnt, flk, def, tdf, wdf);
        engagement.success = success;
        engagement.attacker = ctrl.player.army;
        engagement.defender = ctrl.opponent.army;
        ctrl.hud.engagementSummary(engagement, ctrl.cfg.fxVolume);

        return success;
    }

    private void addEngagementAnimation(Unit target)
    {
        FireAnimation.reset();
        Hex to = target.getHex();
        for (Unit u : activatedUnits) {
            Hex from = u.getHex();
            AnimationSequence seq = AnimationSequence.get(2);
            float halfWidth = (u.getWidth() / 2f);
            if (u.isA(Unit.UnitType.INFANTRY))
                seq.addAnimation(InfantryFireAnimation.get(ctrl.cfg.fxVolume, from.getX(), from.getY(), to.getX(), to.getY(), halfWidth));
            else
                seq.addAnimation(TankFireAnimation.get(ctrl.cfg.fxVolume, from.getX(), from.getY(), to.getX(), to.getY(), halfWidth));
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

        boolean success = resolveFight(unit, target, mayReroll);

        breakUnits.clear();
        for (Unit u : activatedUnits) {
            u.engage();
            if (u.isA(Unit.UnitType.INFANTRY))
                breakUnits.add(u);
        }

        if (success) {
            unclaim(target.getHex());
            removePawn(target);
            destroy.set(2f, target);
            addAnimation(destroy);
        }

        addEngagementAnimation(target);

        if ((activatedUnits.size() == 1) && unit.isA(Unit.UnitType.AT_GUN) && target.isHardTarget())
            activatedUnits.clear();

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
    public void showPathBuilder()       { pathBuilder.enable(Hex.AREA, true); }
    public void hidePathBuilder()       { pathBuilder.enable(Hex.AREA, false); }
    public void showPath(Hex dst)       { pathBuilder.enable(Hex.MOVE, true); showMove(dst); }
    public void hidePath(Hex dst)       { pathBuilder.enable(Hex.MOVE, false); hideMove(dst); }

    public void selectHex(Hex hex)      { selectedTile.set(hex); }
    public void unselectHex(Hex hex)    { selectedTile.hide(); }
    public void showMove(Hex hex)       { enableOverlayOn(hex, Hex.MOVE, true); }
    public void hideMove(Hex hex)       { enableOverlayOn(hex, Hex.MOVE, false); }
    public void showDirections(Hex hex) { enableOverlayOn(hex, Hex.DIRECTIONS, true); }
    public void hideDirections(Hex hex) { enableOverlayOn(hex, Hex.DIRECTIONS, false); }
    public void showOrientation(Hex hex, Orientation o) { enableOverlayOn(hex, Hex.ORIENTATION, o, true); }
    public void hideOrientation(Hex hex) { enableOverlayOn(hex, Hex.ORIENTATION, false); }
    public void showExit(Hex hex)       { enableOverlayOn(hex, Hex.EXIT, true); }
    public void hideExit(Hex hex)       { enableOverlayOn(hex, Hex.EXIT, false); }

    public void showObjective(Hex hex, Army army, boolean hold)
    {
        if (hold)
            enableOverlayOn(hex, Hex.OBJECTIVE_HOLD, true);
        else
            enableOverlayOn(hex, Hex.OBJECTIVE, true);
    }


    // -> implement ObjectiveSet.ObjectiveCb

    public void showObjective(Tile tile, Faction faction)
    {
        showObjective((Hex) tile, (Army) faction);
    }

    // <- implement MoveToAnimationCb

    public void showObjective(Hex hex, Army army)
    {
        if (army == null)
            army = Army.NONE;
        switch(army) {
            case GE:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, true);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, false);
                break;
            case US:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, false);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, true);
                break;
            case NONE:
            default:
                enableOverlayOn(hex, Hex.OBJECTIVE_GE, false);
                enableOverlayOn(hex, Hex.OBJECTIVE_US, false);
                break;
        }
    }
}
