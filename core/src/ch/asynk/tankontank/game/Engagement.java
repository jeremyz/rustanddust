package ch.asynk.tankontank.game;

import java.util.List;
import java.util.LinkedList;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

public class Engagement implements Disposable, Pool.Poolable
{
    private static final Pool<Engagement> engagementPool = new Pool<Engagement>() {
        @Override
        protected Engagement newObject() {
            return new Engagement();
        }
    };

    public static Engagement get(Unit attacker, Unit defender)
    {
        Engagement e = engagementPool.obtain();
        e.attacker = attacker;
        e.defender = defender;

        return e;
    }

    public static void clearPool()
    {
        engagementPool.clear();
    }

    public Unit attacker;
    public Unit defender;
    public List<Unit> assists;
    public Army attackerArmy;
    public Army defenderArmy;
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
    public int attackSum;
    public int defenseSum;

    public Engagement()
    {
        assists = new LinkedList<Unit>();
        reset();
    }

    @Override
    public void reset()
    {
        attacker = null;
        defender = null;
        assists.clear();
    }

    @Override
    public void dispose()
    {
        assists.clear();
        engagementPool.free(this);
    }

    public void addAssist(Unit unit)
    {
        assists.add(unit);
    }

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
            this.attackSum = (d1 + d2 + unitCount + flankBonus);
        else
            this.attackSum = (d3 + d4 + unitCount + flankBonus);
        this.defenseSum = (unitDefense + terrainDefense + weatherDefense);
    }


    @Override
    public String toString()
    {
        int a, b;
        if (d3 == 0) {
            a = d1;
            b = d2;
        } else {
            a = d3;
            b = d4;
        }
        return String.format("Engagement : (%d + %d + %d + %d) vs (%d + %d + %d) -> %b", a, b, unitCount, flankBonus, unitDefense, terrainDefense, weatherDefense, success);
    }
}
