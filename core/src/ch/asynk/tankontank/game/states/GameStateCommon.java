package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.GameCtrl;
import ch.asynk.tankontank.game.GameState;

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

    protected static boolean hexInMap(GridPoint2 hex)
    {
        if (hex.x == -1) return false;
        return !map.isOffMap(hex);
    }

    public boolean downInMap(float x, float y)
    {
        map.getHexAt(downHex, x, y);
        return hexInMap(downHex);
    }

    public boolean upInMap(float x, float y)
    {
        map.getHexAt(upHex, x, y);
        return hexInMap(upHex);
    }

    protected void setPawn()
    {
        pawn = map.getTopPawnAt(hex);
    }

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
}
