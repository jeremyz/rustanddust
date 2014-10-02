package ch.asynk.tankontank.game;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;

public abstract class GameStateCommon implements GameState
{
    protected static GameCtrl ctrl;
    protected static Map map;
    protected static Pawn pawn;
    protected static Tile tile;
    protected static GridPoint2 hex = new GridPoint2(0, 0);

    protected static GridPoint2 downHex = new GridPoint2(-1, -1);
    protected static GridPoint2 upHex = new GridPoint2(-1, -1);

    protected GameStateCommon()
    {
    }

    public GameStateCommon(GameCtrl ctrl, Map map)
    {
        this.ctrl = ctrl;
        this.map = map;
    }

    // downHex

    protected static boolean downHexInMap()
    {
        if (downHex.x == -1) return false;
        return !map.isOffMap(downHex);
    }

    protected static boolean down(float x, float y)
    {
        map.getHexAt(downHex, x, y);
        return downHexInMap();
    }

    protected static boolean up(float x, float y)
    {
        map.getHexAt(upHex, x, y);
        return downHexInMap();
    }

    // pawn

    protected void setPawn()
    {
        pawn = map.getTopPawnAt(hex);
    }

    // hex

    protected void setHex()
    {
        hex.set(downHex.x, downHex.y);
    }

    protected boolean hexHasUnit()
    {
        return map.hasUnits(hex);
    }

    protected void unselectHex()
    {
        map.enableOverlayOn(hex, Hex.BLUE, false);
    }

    protected void selectHex()
    {
        map.enableOverlayOn(hex, Hex.BLUE, true);
    }

    // protected Hex getHex(int col, int row)
    // {
    //     return (Hex) map.getTile(col, row);
    // }
}
