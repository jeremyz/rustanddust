package ch.asynk.rustanddust.game.battles;

import ch.asynk.rustanddust.RustAndDust;

import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Engagement;

abstract public class MapRules extends Map
{
    public MapRules(final RustAndDust game, String map, String hex)
    {
        super(game, map, hex);
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

}
