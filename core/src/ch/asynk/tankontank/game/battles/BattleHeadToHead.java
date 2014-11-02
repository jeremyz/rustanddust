package ch.asynk.tankontank.game.battles;

import java.util.Random;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.Unit;
import ch.asynk.tankontank.game.Unit.UnitId;
import ch.asynk.tankontank.engine.Orientation;

public class BattleHeadToHead extends BattleCommon
{
    public BattleHeadToHead(Factory factory)
    {
        super(factory);
        randomizeArmies();
    }

    public boolean checkVictory(Ctrl ctrl)
    {
        if (ctrl.opponent.unitsLeft() == 0)
            return true;

        if ((ctrl.player.getTurn() < 10) || (ctrl.opponent.getTurn() < 10))
            return false;

        Unit unit;
        int objectives = 0;

        unit = ctrl.map.getHex(7, 7).getUnit();
        if ((unit != null) && (!ctrl.player.isEnemy(unit)))
                objectives += 1;
        unit = ctrl.map.getHex(6, 4).getUnit();
        if ((unit != null) && (!ctrl.player.isEnemy(unit)))
                objectives += 1;
        unit = ctrl.map.getHex(6, 1).getUnit();
        if ((unit != null) && (!ctrl.player.isEnemy(unit)))
                objectives += 1;

        return (objectives > 1);
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

        map.showObjective(map.getHex(7, 7));
        map.showObjective(map.getHex(6, 4));
        map.showObjective(map.getHex(6, 1));

        Orientation o = Orientation.NORTH;
        gePlayer.addReinforcement(factory.getUnit(UnitId.GE_TIGER));
        gePlayer.addReinforcement(factory.getUnit(UnitId.GE_TIGER));
        gePlayer.addReinforcement(factory.getUnit(UnitId.GE_PANZER_IV));
        gePlayer.addReinforcement(factory.getUnit(UnitId.GE_PANZER_IV_HQ));
        gePlayer.addReinforcement(factory.getUnit(UnitId.GE_PANZER_IV));
        gePlayer.addReinforcement(factory.getUnit(UnitId.GE_PANZER_IV));
        gePlayer.addReinforcement(factory.getUnit(UnitId.GE_PANZER_IV_HQ));
        gePlayer.addReinforcement(factory.getUnit(UnitId.GE_PANZER_IV));

        o = Orientation.SOUTH;
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_WOLVERINE));
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_WOLVERINE));
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_PRIEST));
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_SHERMAN));
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_SHERMAN_HQ));
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_SHERMAN));
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_SHERMAN));
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_SHERMAN_HQ));
        usPlayer.addReinforcement(factory.getUnit(UnitId.US_SHERMAN));
    }

    public boolean checkVictory()
    {
        return false;
    }
}
