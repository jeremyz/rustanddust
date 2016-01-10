package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.OrderList;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Command;
import ch.asynk.rustanddust.game.Engagement;
import ch.asynk.rustanddust.game.Battle;

public abstract class Map4Commands extends Map3Animations
{
    private final Battle battle;
    private final OrderList commands;

    protected abstract int engagementCost(Engagement e);
    protected abstract void resolveEngagement(Engagement e);

    public Map4Commands(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);

        this.battle = game.ctrl.battle;
        this.commands = new OrderList(10);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        commands.dispose();
        Command.clearPool();
        Engagement.clearPool();
    }

    protected int commandsSize() { return commands.size(); }
    protected void commandsClear() { commands.dispose(); }

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
            revertLastPawnMove(unit, ((Command) commands.get(unit, Command.CommandType.MOVE)).move);
            commands.dispose(unit, Command.CommandType.MOVE);
        }
        activatedUnits.clear();
    }

    public void revertEnter(final Unit unit)
    {
        RustAndDust.debug("    revertEnter() "+ unit);

        revertclaim(unit, unit.getHex());
        removePawn(unit);
        battle.getPlayer().revertUnitEntry(unit);
        commands.dispose(unit);
        unit.reset();
    }

    public boolean engageUnit(final Unit unit, final Unit target)
    {
        attack(unit, target, true);

        Command cmd = Command.get(battle.getPlayer());
        cmd.setEngage(unit, target);
        return (process(cmd) == 1);
    }

    public void promoteUnit(final Unit unit)
    {
        Command cmd = Command.get(battle.getPlayer());
        cmd.setPromote(unit);
        process(cmd);
    }

    // STATES ENTRY <-

    private Command getMoveCommand(Unit unit, Move move)
    {
        Command cmd = Command.get(battle.getPlayer());
        cmd.setMove(unit, move);
        return cmd;
    }

    private void initMove(Unit unit)
    {
        moveableUnits.remove(unit);
        activatedUnits.add(unit);
        playMoveSound(unit);
    }

    private int promoteUnit(final Unit unit, final Player player)
    {
        activatedUnits.add(unit);
        addPromoteAnimation(unit, player, new Runnable() {
            @Override
            public void run() {
                player.promote(unit);
            }
        });
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
                battle.getPlayer().unitWithdraw(unit);
                r = moveableUnits.size();
                break;
            case SET:
                setPawnOnto(unit, move);
                battle.getPlayer().unitEntry(unit);
                claim(unit, move.to);
                break;
            case ENTER:
                enterPawn(unit, move);
                battle.getPlayer().unitEntry(unit);
                claim(unit, move.to);
                break;
            default:
                System.err.println(String.format("process wrong Move type %s", move.type));
                r = -1;
                break;
        }

        return r;
    }

    private int doEngagement(Engagement e)
    {
        resolveEngagement(e);

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
            unclaim(e.defender, e.defender.getHex());
            removePawn(e.defender);
            addDestroyAnimation(e.defender);
        }

        game.ctrl.hud.engagementSummary(e);
        addEngagementAnimation(e.defender);

        if (engagementCost(e) == 0)
            activatedUnits.clear();

        return (e.success ? 1 : 0);
    }

}
