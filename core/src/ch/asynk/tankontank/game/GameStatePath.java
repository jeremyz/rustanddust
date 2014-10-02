package ch.asynk.tankontank.game;

import com.badlogic.gdx.math.GridPoint2;

public class GameStatePath extends GameStateCommon
{
    private GridPoint2 from = new GridPoint2(-1, -1);
    private GridPoint2 to = new GridPoint2(-1, -1);

    @Override
    public boolean drag(float dx, float dy)
    {
        return false;
    }

    @Override
    public void touchDown()
    {
        System.out.println("GameStatePath: touchDown()");
        int s = map.possiblePathsSize();
        if (s == 0) {
            s = buildPaths();
        } else {
            if (map.isInPossiblePaths(downHex)) {
                s = togglePoint();
            } else {
                s = reset(false);
            }
        }

        if (s == 1) {
            map.enableFinalPath(true);
            ctrl.setState(State.DIRECTION, true);
        }
    }

    @Override
    public void touchUp()
    {
        System.out.println("GameStatePath: touchUp()");
    }

    @Override
    public void abort()
    {
        System.err.println("GameStatePath: abort");
    }

    private int buildPaths()
    {
        from.set(hex.x, hex.y);
        to.set(downHex.x, downHex.y);
        int s = map.buildPossiblePaths(pawn, from, to);
        map.enablePossibleMoves(false);
        map.toggleDotOverlay(downHex);
        map.enablePossiblePaths(true, true);
        return s;
    }

    private int togglePoint()
    {
        int s = 0;
        if ((downHex.x == from.x) && (downHex.y == from.y)) {
            s = map.possiblePathsSize();
        } else if ((downHex.x == to.x) && (downHex.y == to.y)) {
            s = reset(true);
        } else {
            map.enablePossiblePaths(false, true);
            map.toggleDotOverlay(downHex);
            s = map.possiblePathsPointToggle(downHex);
            map.enablePossiblePaths(true, true);
        }
        return s;
    }

    private int reset(boolean showMoves)
    {
        to.set(-1, -1);
        from.set(-1, -1);
        map.clearPossibles();
        if (showMoves)
            map.enablePossibleMoves(true);
        ctrl.setState(State.NONE, false);
        return -1;
    }
}
