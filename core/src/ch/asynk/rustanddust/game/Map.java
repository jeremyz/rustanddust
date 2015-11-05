package ch.asynk.rustanddust.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Pawn;
import ch.asynk.rustanddust.engine.Board;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Faction;
import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.ObjectiveSet;
import ch.asynk.rustanddust.engine.OrderList;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.Meteorology;
import ch.asynk.rustanddust.engine.PathBuilder;
import ch.asynk.rustanddust.engine.gfx.Moveable;
import ch.asynk.rustanddust.engine.gfx.Animation;
import ch.asynk.rustanddust.engine.gfx.animations.AnimationSequence;
import ch.asynk.rustanddust.engine.gfx.animations.DiceAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.FireAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.TankFireAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.InfantryFireAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.PromoteAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.DestroyAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.SoundAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.RunnableAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.MoveToAnimation.MoveToAnimationCb;

import ch.asynk.rustanddust.ui.Position;

public abstract class Map extends Board implements MoveToAnimationCb, ObjectiveSet.ObjectiveCb
{
    private final Ctrl ctrl;

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

    private OrderList commands;

    protected abstract void setup();

    public Map(final RustAndDust game, Board.Config cfg, String textureName)
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
        commands = new OrderList();
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
        commands.dispose();
        Command.clearPool();
        Engagement.clearPool();
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
        RustAndDust.debug("  Move", String.format("%s %s", move.type, move.toString()));

        int r = 1;

        switch(move.type) {
            case REGULAR:
                initMove(unit);
                movePawn(unit, move, this);
                r = moveableUnits.size();
                break;
            case EXIT:
                initMove(unit);
                movePawn(unit, move, this);
                ctrl.player.unitWithdraw(unit);
                r = moveableUnits.size();
                break;
            case SET:
                setPawnOnto(unit, move);
                ctrl.player.unitEntry(unit);
                claim((Hex) move.to, unit.getArmy());
                break;
            case ENTER:
                enterPawn(unit, move);
                ctrl.player.unitEntry(unit);
                claim((Hex) move.to, unit.getArmy());
                break;
            default:
                System.err.println(String.format("process wrong Move type %s", move.type));
                r = -1;
                break;
        }

        return r;
    }

    private int promoteUnit(final Unit unit, final Player player)
    {
        activatedUnits.add(unit);

        Hex hex = unit.getHex();
        AnimationSequence seq = AnimationSequence.get(2);
        seq.addAnimation(PromoteAnimation.get((unit.getArmy() == Army.US), hex.getX(), hex.getY(), ctrl.cfg.fxVolume));
        seq.addAnimation ( RunnableAnimation.get(unit, new Runnable() {
            @Override
            public void run() {
                player.promote(unit);
            }
        }));
        addAnimation(seq);
        return 1;
    }

    private int process(Command cmd)
    {
        RustAndDust.debug("Command", cmd.toString());

        int r = 1;

        switch(cmd.type) {
            case MOVE:
                r = process(cmd.unit, cmd.move);
                break;
            case PROMOTE:
                r = promoteUnit(cmd.unit, cmd.player);
                break;
            case ENGAGE:
                resolveEngagement(cmd.engagement);
                r = doEngagement(cmd.engagement);
                break;
            default:
                System.err.println(String.format("process wrong Command type %s", cmd.type));
                r = -1;
                break;
        }

        if (r != -1)
            commands.add(cmd);

        return r;
    }

    // Ctrl Methods

    public void init()
    {
        actionDone();
    }

    public void turnDone()
    {
        RustAndDust.debug("TurnDone", String.format(" Processed Commands : %d", commands.size()));

        if (objectives.modifiedCount() > 0)
            throw new RuntimeException("objectives not cleared");

        // FIXME do something with these Commands
        commands.dispose();
    }

    public void actionDone()
    {
        objectives.forget();
    }

    // STATES ENTRY ->

    public void showOnBoard(final Unit unit, Hex to, Orientation o)
    {
        setPawnOnto(unit, to, o);
    }

    public boolean setOnBoard(final Unit unit, Hex to, Orientation entry)
    {
        commands.dispose(unit);
        return (process(getMoveCommand(unit, Move.getSet(unit, to, entry))) == 1);
    }

    public boolean enterBoard(final Unit unit, Hex to, int allowedMoves)
    {
        Orientation entry = findBestEntry(unit, to, allowedMoves);
        if (entry == Orientation.KEEP)
            return false;

        return (process(getMoveCommand(unit, Move.getEnter(unit, to, entry))) == 1);
    }

    public int exitBoard(final Unit unit)
    {
        return process(getMoveCommand(unit, pathBuilder.getExitMove()));
    }

    public int moveUnit(final Unit unit)
    {
        return process(getMoveCommand(unit, pathBuilder.getMove()));
    }

    public void revertMoves()
    {
        for (Unit unit: activatedUnits) {
            RustAndDust.debug("    revertMove() " + unit);
            revertLastPawnMove(unit);
            commands.dispose(unit, Command.CommandType.MOVE);
        }
        activatedUnits.clear();
        objectives.revert(this);
    }

    public void revertEnter(final Unit unit)
    {
        RustAndDust.debug("    revertEnter() "+ unit);
        removePawn(unit);
        objectives.revert(this);
        ctrl.player.revertUnitEntry(unit);
        commands.dispose(unit);
        unit.reset();
    }

    public boolean engageUnit(final Unit unit, final Unit target)
    {
        attack(unit, target, true);

        Command cmd = Command.get(ctrl.player);
        cmd.setEngage(unit, target);
        return (process(cmd) == 1);
    }

    public void promoteUnit(final Unit unit)
    {
        Command cmd = Command.get(ctrl.player);
        cmd.setPromote(unit);
        process(cmd);
    }

    // STATES ENTRY <-

    private Command getMoveCommand(Unit unit, Move move)
    {
        Command cmd = Command.get(ctrl.player);
        cmd.setMove(unit, move);
        return cmd;
    }

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

    @Override
    protected void animationsOver()
    {
        if (soundId >= 0) {
            addAnimation( SoundAnimation.get(SoundAnimation.Action.FADE_OUT, sound, soundId, ctrl.cfg.fxVolume, 0.5f));
            soundId = -1;
            return;
        }
        ctrl.animationsOver();
    }

    private void addEngagementAnimation(Unit target)
    {
        FireAnimation.reset();
        Hex to = target.getHex();
        for (Unit u : activatedUnits) {
            Hex from = u.getHex();
            float halfWidth = (u.getWidth() / 2f);
            if (u.isA(Unit.UnitType.INFANTRY))
                addAnimation(InfantryFireAnimation.get(ctrl.cfg.fxVolume, from.getX(), from.getY(), to.getX(), to.getY(), halfWidth));
            else
                addAnimation(TankFireAnimation.get(ctrl.cfg.fxVolume, from.getX(), from.getY(), to.getX(), to.getY(), halfWidth));
        }
    }

    private void resolveEngagement(Engagement e)
    {
        int dice = e.d1 + e.d2;

        int distance = 0;
        boolean mayReroll = false;
        boolean night = (meteorology.day == Meteorology.Day.NIGHT);
        boolean flankAttack = false;
        boolean terrainBonus = true;

        for (Unit unit : activatedUnits) {
            if (unit != e.attacker)
                e.addAssist(unit);
            if (unit.isAce())
                mayReroll = true;
            if (unit.isFlankAttack())
                flankAttack = true;
            if (unit.preventDefenseOn(e.defender.getTile()))
                terrainBonus = false;
            if (night) {
                if (distance < unit.attackDistance())
                    distance = unit.attackDistance();
            }
        }

        int cnt = activatedUnits.size();
        int def = e.defender.getDefense(e.attacker.getTile());
        int flk = (flankAttack ? Unit.FLANK_ATTACK_BONUS : 0);
        int tdf = (terrainBonus ? e.defender.getTile().defense() : 0);
        int wdf = 0;
        if (night) {
            if (distance > 3)
                wdf = 3;
            else if (distance > 2)
                wdf = 2;
            else if (distance > 1)
                wdf = 1;
        }
        int s1 = (dice + cnt + flk);
        int s2 = (def + tdf + wdf);

        boolean success = false;
        if (dice == 2) {
            success = false;
        } else if (dice == 12) {
            success = true;
        } else {
            success = (s1 >= s2);
        }
        if (!success && mayReroll) {
            dice = e.d3 + e.d4;
            s1 = (dice + cnt + flk);
            if (dice == 2) {
                success = false;
            } else if (dice == 12) {
                success = true;
            } else {
                success = (s1 >= s2);
            }
        } else {
            e.d3 = 0;
            e.d4 = 0;
        }

        e.set(cnt, flk, def, tdf, wdf);
        e.success = success;
    }

    private int doEngagement(Engagement e)
    {
        breakUnits.clear();
        activatedUnits.clear();

        activatedUnits.add(e.attacker);
        for (Unit u : e.assists)
            activatedUnits.add(u);

        for (Unit u : activatedUnits) {
            u.engage();
            if (u.canBreak())
                breakUnits.add(u);
        }

        if (e.success) {
            unclaim(e.defender.getHex());
            removePawn(e.defender);
            destroy.set(2f, e.defender);
            addAnimation(destroy);
        }

        if ((activatedUnits.size() == 1) && e.attacker.isA(Unit.UnitType.AT_GUN) && e.defender.isHardTarget())
            activatedUnits.clear();

        ctrl.hud.engagementSummary(e, ctrl.cfg.fxVolume);
        addEngagementAnimation(e.defender);

        return (e.success ? 1 : 0);
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
