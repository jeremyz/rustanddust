package ch.asynk.tankontank.game.states;

import ch.asynk.tankontank.engine.Orientation;

public class GameStateRotate extends GameStateCommon
{
    @Override
    public void touchDown()
    {
        if (pawn == null) {
            super.touchDown();
            if (hexHasUnit()) {
                // TODO maybe keep the the previous hex
                // FIXME must be one of it's own
                setPawn();
                map.enableDirections(hex, true);
            }
        }
    }

    @Override
    public void touchUp()
    {
        if (pawn == null) {
            unselectHex();
            return;
        }

        Orientation o = Orientation.KEEP;

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

        if (o != Orientation.KEEP) {
            clear();
            map.rotatePawn(pawn, o);
            ctrl.setState(State.ANIMATION);
        }

    }

    @Override
    public void abort()
    {
        clear();
        super.abort();
    }

    private void clear()
    {
        unselectHex();
        map.enableDirections(hex, false);
    }
}
