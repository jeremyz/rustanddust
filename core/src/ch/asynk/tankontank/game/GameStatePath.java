package ch.asynk.tankontank.game;

import com.badlogic.gdx.math.GridPoint2;

public class GameStatePath extends GameStateCommon
{
    private GridPoint2 from = new GridPoint2(-1, -1);
    private GridPoint2 to = new GridPoint2(-1, -1);

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        int s = map.possiblePathsSize();
        if (s == 0) {
            s = buildPaths();
        } else {
            if (map.isInPossiblePaths(downHex))
                s = togglePoint();
            else
                s = reset();
        }

        if (s == 1) {
            unselectHex();
            hex.set(to.x, to.y);
            map.enableFinalPath(to, true);
            ctrl.setState(State.DIRECTION, false);
        }
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
            s = reset();
        } else {
            map.enablePossiblePaths(false, true);
            map.toggleDotOverlay(downHex);
            s = map.possiblePathsPointToggle(downHex);
            map.enablePossiblePaths(true, true);
        }
        return s;
    }

    private int reset()
    {
        to.set(-1, -1);
        from.set(-1, -1);
        map.hidePaths();
        map.resetPaths();
        ctrl.setState(State.NONE, false);
        return -1;
    }
}
