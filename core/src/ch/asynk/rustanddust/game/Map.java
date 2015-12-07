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

    protected final HexSet moves;
    protected final PathBuilder paths;

    protected final UnitList moveableUnits;
    protected final UnitList targetUnits;
    protected final UnitList assistUnits;
    protected final UnitList breakthroughUnits;
    protected final UnitList activatedUnits;

    protected final ObjectiveSet objectives;
    protected final Meteorology meteorology;

    private final DestroyAnimation destroy;
    private final Sound tankMoveSound;
    private final Sound infantryMoveSound;
    private Sound sound;
    private long soundId = -1;

    private OrderList commands;

    protected abstract void setup();
    protected abstract void resolveEngagement(Engagement e);
    protected abstract int engagementCost(Engagement e);

    public Map(final RustAndDust game, String map, String hex)
    {
        super(game.factory, game.manager.get(map, Texture.class),
                new SelectedTile(game.manager.get(hex, Texture.class), new float[] {.1f, .1f, .1f, .1f, .3f, .1f} ));
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

        moves = new HexSet(this, 40);
        paths = new PathBuilder(this, 10, 20, 5, 10);

        moveableUnits = new UnitList(6);
        targetUnits = new UnitList(10);
        assistUnits = new UnitList(6);
        breakthroughUnits = new UnitList(4);
        activatedUnits = new UnitList(7);

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
        paths.dispose();
        DiceAnimation.free();
        PromoteAnimation.free();
        FireAnimation.free();
        commands.dispose();
        Command.clearPool();
        Engagement.clearPool();
    }

    public void clearAll()
    {
        moves.clear();
        paths.clear();
        moveableUnits.clear();
        targetUnits.clear();
        assistUnits.clear();
        breakthroughUnits.clear();
        activatedUnits.clear();
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

    public boolean movesContains(Hex hex)
    {
        return moves.contains(hex);
    }

    public int movesCollect(Unit unit)
    {
        if (unit.canMove())
            return collectPossibleMoves(unit, moves.asTiles());

        moves.clear();
        return 0;
    }

    public void pathsClear()                        { paths.clear(); }

    public int pathsSize()                          { return paths.size(); }

    public void pathsInit(Unit unit)                { paths.init(unit); }

    public void pathsInit(Unit unit, Hex hex)       { paths.init(unit, hex); }

    public boolean pathsIsSet()                     { return paths.isSet(); }

    public boolean pathsCanExit(Orientation o)      { return paths.canExit(o); }

    public void pathsSetExit(Orientation o)         { paths.setExit(o); }

    public int pathsBuild(Hex hex)                  { return pathsBuild(hex); }

    public boolean pathsContains(Hex hex)           { return paths.contains(hex); }

    public void pathsSetOrientation(Orientation o)  { paths.orientation = o; }

    public Hex pathsTo()                            { return (Hex) paths.to; }

    public int pathsChooseOne()                     { return paths.choosePath(); }

    public int pathsToggleHex(Hex hex)
    {
        boolean enable = !hex.isOverlayEnabled(Hex.MOVE);
        enableOverlayOn(hex, Hex.MOVE, enable);
        return paths.toggleCtrlTile(hex);
    }

    public int collectPossibleTargets(Unit unit, UnitList foes)
    {
        if (unit.canEngage())
            return collectPossibleTargets(unit, foes.asPawns(), targetUnits.asPawns());

        targetUnits.clear();
        return 0;
    }

    public int collectMoveableUnits(Unit unit)
    {
        if (unit.canHQMove())
                collectMoveAssists(unit, moveableUnits.asPawns());
        else
            moveableUnits.clear();

        if (unit.canMove())
            moveableUnits.add(unit);

        return moveableUnits.size();
    }

    public int collectAttackAssists(Unit unit, Unit target, UnitList units)
    {
        int s = collectAttackAssists(unit, target, units.asPawns(), assistUnits.asPawns());
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
        movesHide();
        unitsHide(UnitType.MOVEABLE);
        movesCollect(unit);
        collectMoveableUnits(unit);
        movesShow();
        unitsShow(UnitType.MOVEABLE);
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
        return process(getMoveCommand(unit, paths.getExitMove()));
    }

    public int moveUnit(final Unit unit)
    {
        return process(getMoveCommand(unit, paths.getMove()));
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

    private int doEngagement(Engagement e)
    {
        breakthroughUnits.clear();
        activatedUnits.clear();

        activatedUnits.add(e.attacker);
        for (Unit u : e.assists)
            activatedUnits.add(u);

        for (Unit u : activatedUnits) {
            u.engage();
            if (u.canBreak())
                breakthroughUnits.add(u);
        }

        if (e.success) {
            unclaim(e.defender.getHex());
            removePawn(e.defender);
            destroy.set(2f, e.defender);
            addAnimation(destroy);
        }

        ctrl.hud.engagementSummary(e, ctrl.cfg.fxVolume);
        addEngagementAnimation(e.defender);

        if (engagementCost(e) == 0)
            activatedUnits.clear();

        return (e.success ? 1 : 0);
    }

    // SHOW / HIDE

    public enum UnitType
    {
        MOVEABLE,
        TARGETS,
        ASSISTS,
        BREAK_THROUGH,
        ACTIVATED
    };

    public void unitsClear(UnitType unitType)
    {
        getUnitList(unitType).clear();
    }

    public int unitsSize(UnitType unitType)
    {
        return getUnitList(unitType).size();
    }

    public Unit unitsGet(UnitType unitType, int i)
    {
        return getUnitList(unitType).get(i);
    }

    public boolean unitsContains(UnitType unitType, Unit unit)
    {
        return getUnitList(unitType).contains(unit);
    }

    private UnitList getUnitList(UnitType unitType)
    {
        UnitList ul = null;
        switch(unitType) {
            case MOVEABLE:
                ul = moveableUnits;
                break;
            case TARGETS:
                ul = targetUnits;
                break;
            case ASSISTS:
                ul = assistUnits;
                break;
            case BREAK_THROUGH:
                ul = breakthroughUnits;
                break;
            case ACTIVATED:
                ul = activatedUnits;
                break;
        }
        return ul;
    }

    public void unitsShow(UnitType unitType) { unitsShow(unitType, true); }
    public void unitsHide(UnitType unitType) { unitsShow(unitType, false); }
    private void unitsShow(UnitType unitType, boolean show)
    {
        switch(unitType) {
            case MOVEABLE:
                unitsShowOverlay(moveableUnits, Unit.MOVE, show);
                break;
            case TARGETS:
                unitsShowOverlay(targetUnits, Unit.TARGET, show);
                break;
            case ASSISTS:
                unitsShowOverlay(assistUnits, Unit.MAY_FIRE, show);
                if (!show)
                    unitsShowOverlay(assistUnits, Unit.FIRE, show);
                break;
            case BREAK_THROUGH:
                unitsShowOverlay(breakthroughUnits, Unit.MOVE, show);
                break;
        }
    }
    private void unitsShowOverlay(UnitList units, int overlay, boolean on)
    {
        for (Unit unit : units)
            unit.enableOverlay(overlay, on);
    }

    public void movesShow()             { moves.enable(Hex.AREA, true); }
    public void movesHide()             { moves.enable(Hex.AREA, false); }
    public void pathsShow()             { paths.enable(Hex.AREA, true); }
    public void pathsHide()             { paths.enable(Hex.AREA, false); }
    public void pathShow(Hex dst)       { paths.enable(Hex.MOVE, true); hexMoveShow(dst); }
    public void pathHide(Hex dst)       { paths.enable(Hex.MOVE, false); hexMoveHide(dst); }

    public void hexSelect(Hex hex)          { selectedTile.set(hex); }
    public void hexUnselect(Hex hex)        { selectedTile.hide(); }
    public void hexMoveShow(Hex hex)        { enableOverlayOn(hex, Hex.MOVE, true); }
    public void hexMoveHide(Hex hex)        { enableOverlayOn(hex, Hex.MOVE, false); }
    public void hexDirectionsShow(Hex hex)  { enableOverlayOn(hex, Hex.DIRECTIONS, true); }
    public void hexDirectionsHide(Hex hex)  { enableOverlayOn(hex, Hex.DIRECTIONS, false); }
    public void hexExitShow(Hex hex)        { enableOverlayOn(hex, Hex.EXIT, true); }
    public void hexExitHide(Hex hex)        { enableOverlayOn(hex, Hex.EXIT, false); }

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
