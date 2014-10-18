package ch.asynk.tankontank.game.battles;

import java.util.Random;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.Unit.UnitId;
import ch.asynk.tankontank.engine.Orientation;

public class BattleFake extends BattleCommon
{
    public BattleFake(Factory factory)
    {
        super(factory);
        randomizeArmies();
    }

    @Override
    public Map getMap()
    {
        return factory.getMap(Factory.MapType.MAP_A);
    }

    @Override
    public Player getFirstPlayer()
    {
        return factory.getPlayer(firstArmy);
    }

    @Override
    public Player getSecondPlayer()
    {
        return factory.getPlayer(secondArmy);
    }

    @Override
    public void setup(Map map, Player a, Player b)
    {
        Player gePlayer;
        Player usPlayer;

        if (a.isEnemy(Army.GE)) {
            usPlayer = a;
            gePlayer = b;
        } else {
            usPlayer = b;
            gePlayer = a;
        }
        Orientation o = Orientation.NORTH;
        GridPoint2 p = new GridPoint2();

        gePlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.GE_TIGER), p.set(4, 7), o));
        gePlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.GE_TIGER), p.set(3, 6), o));
        gePlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.GE_PANZER_IV), p.set(3, 5), o));
        gePlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.GE_PANZER_IV_HQ), p.set(2, 4), o));
        gePlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.GE_PANZER_IV), p.set(2, 3), o));
        gePlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.GE_PANZER_IV), p.set(1, 2), o));
        gePlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.GE_PANZER_IV_HQ), p.set(1, 1), o));
        gePlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.GE_PANZER_IV), p.set(0, 0), o));

        o = Orientation.SOUTH;
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_WOLVERINE), p.set(13, 8), o));
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_WOLVERINE), p.set(12, 7), o));
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_PRIEST), p.set(12, 6), o));
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_SHERMAN), p.set(11, 5), o));
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_SHERMAN_HQ), p.set(11, 4), o));
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_SHERMAN), p.set(10, 3), o));
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_SHERMAN), p.set(10, 2), o));
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_SHERMAN_HQ), p.set(9, 1), o));
        usPlayer.addUnit(map.setPawnAt(factory.getUnit(UnitId.US_SHERMAN), p.set(9, 0), o));
    }

    public boolean checkVictory()
    {
        return false;
    }
}
