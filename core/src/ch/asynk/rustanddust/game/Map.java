package ch.asynk.rustanddust.game;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;

import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.Meteorology;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Engagement;
import ch.asynk.rustanddust.game.map.Map4Orders;

public abstract class Map extends Map4Orders
{
    protected final Meteorology meteorology;

    public Map(final RustAndDust game, String map, String hex)
    {
        super(game,
                game.manager.get(map, Texture.class),
                new SelectedTile(game.manager.get(hex, Texture.class), new float[] {.1f, .1f, .1f, .1f, .3f, .1f} )
                );

        meteorology = new Meteorology();
    }

    public void clearAll()
    {
        clearMoves();
        clearUnits();
    }

    public void actionDone()
    {
    }

    public void turnDone()
    {
        RustAndDust.debug("TurnDone", String.format(" Processed Orders : %d", ordersSize()));
        game.ctrl.processTurn();
        ordersClear();
    }

    @Override
    protected int engagementCost(Engagement e)
    {
        if ((activatedUnits.size() == 1) && e.attacker.isA(Unit.UnitType.AT_GUN) && e.defender.isHardTarget())
            return 0;
        return 1;
    }

    @Override
    protected void resolveEngagement(Engagement e)
    {
        int dice = e.d1 + e.d2;

        int distance = 0;
        boolean mayReroll = false;
        boolean night = meteorology.isNight();
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
        int def = e.defender.getDefense(e.defender.getTile());
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
}
