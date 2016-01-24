package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Move;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.OrderList;
import ch.asynk.rustanddust.game.Engagement;
import ch.asynk.rustanddust.game.Battle;

public abstract class Map4Orders extends Map3Animations
{
    private final Battle battle;
    private final OrderList orders;

    protected abstract int engagementCost(Engagement e);
    protected abstract void resolveEngagement(Engagement e);

    public Map4Orders(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);

        this.battle = game.ctrl.battle;
        this.orders = new OrderList(10);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        orders.dispose();
        Order.clearPool();
        Engagement.clearPool();
    }

    protected int ordersSize() { return orders.size(); }
    protected void ordersClear() { orders.dispose(); }

    // STATES ENTRY ->

    public void showOnBoard(final Unit unit, Hex to, Orientation o)
    {
        setPawnOnto(unit, to, o);
    }

    public boolean setOnBoard(final Unit unit, Hex to, Orientation entry)
    {
        orders.dispose(unit);
        return (process(getMoveOrder(unit, Move.getSet(unit, to, entry))) == 1);
    }

    public boolean enterBoard(final Unit unit, Hex to, int allowedMoves)
    {
        Orientation entry = findBestEntry(unit, to, allowedMoves);
        if (entry == Orientation.KEEP)
            return false;

        return (process(getMoveOrder(unit, Move.getEnter(unit, to, entry))) == 1);
    }

    public int exitBoard(final Unit unit)
    {
        return process(getMoveOrder(unit, paths.getExitMove()));
    }

    public int moveUnit(final Unit unit)
    {
        return process(getMoveOrder(unit, paths.getMove()));
    }

    public void revertMoves()
    {
        for (Unit unit: activatedUnits) {
            RustAndDust.debug("    revertMove() " + unit);
            revertLastPawnMove(unit, ((Order) orders.get(unit, Order.OrderType.MOVE)).move);
            orders.dispose(unit, Order.OrderType.MOVE);
        }
        activatedUnits.clear();
    }

    public void revertEnter(final Unit unit)
    {
        RustAndDust.debug("    revertEnter() "+ unit);

        revertclaim(unit, unit.getHex());
        removePawn(unit);
        battle.getPlayer().revertUnitEntry(unit);
        orders.dispose(unit);
        unit.reset();
    }

    public boolean engageUnit(final Unit unit, final Unit target)
    {
        attack(unit, target, true);

        Order cmd = Order.get();
        cmd.setEngage(unit, target);
        return (process(cmd) == 1);
    }

    public void promoteUnit(final Unit unit)
    {
        Order cmd = Order.get();
        cmd.setPromote(unit);
        process(cmd);
    }

    // STATES ENTRY <-

    private Order getMoveOrder(Unit unit, Move move)
    {
        Order cmd = Order.get();
        cmd.setMove(unit, move);
        return cmd;
    }

    private void initMove(Unit unit)
    {
        moveableUnits.remove(unit);
        activatedUnits.add(unit);
        playMoveSound(unit);
    }

    private int doPromoteUnit(final Unit unit)
    {
        activatedUnits.add(unit);
        addPromoteAnimation(unit, battle.getPlayer(), new Runnable() {
            @Override
            public void run() {
                battle.getPlayer().promote(unit);
            }
        });
        return 1;
    }

    private int process(Order cmd)
    {
        RustAndDust.debug("Order", cmd.toString());

        int r = 1;

        switch(cmd.type) {
            case MOVE:
                r = process(cmd.unit, cmd.move);
                break;
            case PROMOTE:
                r = doPromoteUnit(cmd.unit);
                break;
            case ENGAGE:
                r = doEngagement(cmd.engagement);
                break;
            default:
                System.err.println(String.format("process wrong Order type %s", cmd.type));
                r = -1;
                break;
        }

        if (r != -1)
            orders.add(cmd);

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
