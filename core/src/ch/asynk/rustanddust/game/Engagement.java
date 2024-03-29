package ch.asynk.rustanddust.game;

import java.util.Random;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

public class Engagement implements Disposable, Pool.Poolable
{
    private static Random rand = new Random();

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
        e.diceRoll();

        return e;
    }

    public static void clearPool()
    {
        engagementPool.clear();
    }

    public Unit attacker;
    public Unit defender;
    public UnitList assists;
    public boolean success;
    public int cost;
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
        assists = new UnitList(10);
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

    private void diceRoll()
    {
        d1 = rand.nextInt(6) + 1;
        d2 = rand.nextInt(6) + 1;
        d3 = rand.nextInt(6) + 1;
        d4 = rand.nextInt(6) + 1;
    }

    public void set(int cnt, int flk, int def, int tdf, int wdf)
    {
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
