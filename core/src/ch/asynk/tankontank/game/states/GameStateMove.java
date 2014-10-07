package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.GridPoint2;

public class GameStateMove extends GameStateCommon
{
    private boolean skipFirst;
    private GridPoint2 from = new GridPoint2(-1, -1);
    private GridPoint2 to = new GridPoint2(-1, -1);

    @Override
    public void enter()
    {
        map.enablePossibleTargets(false);
        map.enablePossiblePaths(false, false);
        if (hasPawn()) {
            skipFirst = false;
            map.clearPossiblePaths();
            map.buildAndShowPossibleMoves(pawn, hex);
        }
    }

    @Override
    public void touchDown()
    {
        if (!hasPawn()) {
            reselect();
            if (hasPawn()) {
                skipFirst = true;
                map.clearPossiblePaths();
                map.buildAndShowPossibleMoves(pawn, hex);
            }
        }
    }

    @Override
    public void touchUp()
    {
        if (!hasPawn()) {
            unselectHex();
            return;
        }

        if (skipFirst) {
            skipFirst = false;
            return;
        }

        int s = map.possiblePathsSize();

        if (s == 0) {
            if (map.isInPossibleMoves(upHex))
                s = buildPaths();
        } else {
            if (map.isInPossiblePaths(upHex))
                s = togglePoint(s);
        }

        if (s == 1) {
            tmp.set(to.x, to.y);
            map.enableFinalPath(to, true);
            ctrl.setState(State.DIRECTION);
        }
    }

    @Override
    public void abort()
    {
        to.set(-1, -1);
        from.set(-1, -1);
        super.abort();
    }

    private int buildPaths()
    {
        from.set(hex.x, hex.y);
        to.set(upHex.x, upHex.y);
        map.clearPossiblePaths();
        int s = map.buildPossiblePaths(pawn, from, to);
        map.togglePathOverlay(downHex);
        map.enablePossibleMoves(false);
        map.enablePossiblePaths(true, true);
        return s;
    }

    private int togglePoint(int s)
    {
        if (sameHexes(downHex, from)) {
            //
        } else if (sameHexes(downHex, to)) {
            //
        } else {
            map.enablePossiblePaths(false, true);
            map.togglePathOverlay(downHex);
            s = map.possiblePathsPointToggle(downHex);
            map.enablePossiblePaths(true, true);
        }

        return s;
    }
}
