package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;

public class GameStateDirection extends GameStateCommon
{
    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        Orientation o = Orientation.KEEP;;

        if (downHex.y == hex.y) {
            if (downHex.x == (hex.x - 1)) {
                o = Orientation.SOUTH;
            } else if (downHex.x == (hex.x + 1)) {
                o = Orientation.NORTH;
            }
        } else if (downHex.y == (hex.y - 1)) {
            if (downHex.x == (hex.x - 1)) {
                o = Orientation.SOUTH_EAST;
            } else if (downHex.x == hex.x) {
                o = Orientation.NORTH_EAST;
            }

        } else if (downHex.y == (hex.y + 1)) {
            if (downHex.x == hex.x) {
                o = Orientation.SOUTH_WEST;
            } else if (downHex.x == (hex.x + 1)) {
                o = Orientation.NORTH_WEST;
            }
        }

        if (o != Orientation.KEEP)
            map.movePawn(pawn, o);

        map.enableFinalPath(hex, false);
        map.resetPaths();
        ctrl.setState(State.NONE, false);
    }
}
