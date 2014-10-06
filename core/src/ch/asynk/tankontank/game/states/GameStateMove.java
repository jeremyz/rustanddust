package ch.asynk.tankontank.game.states;

import com.badlogic.gdx.math.GridPoint2;

public class GameStateMove extends GameStateCommon
{
    private boolean skipFirst;
    private GridPoint2 from = new GridPoint2(-1, -1);
    private GridPoint2 to = new GridPoint2(-1, -1);

    @Override
    public void touchDown()
    {
        if (pawn == null) {
            super.touchDown();
            if (hexHasUnit()) {
                // TODO maybe keep the the previous hex
                // FIXME must be one of it's own
                setPawn();
                skipFirst = true;
                map.buildAndShowPossibleMoves(pawn, hex);
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
                s = togglePoint();
        }

        if (s == 1) {
            unselectHex();
            hex.set(to.x, to.y);
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

    private int togglePoint()
    {
        int s = 0;
        if ((downHex.x == from.x) && (downHex.y == from.y)) {
            // s = map.possiblePathsSize();
        } else if ((downHex.x == to.x) && (downHex.y == to.y)) {
            // s = map.possiblePathsSize();
        } else {
            map.enablePossiblePaths(false, true);
            map.togglePathOverlay(downHex);
            s = map.possiblePathsPointToggle(downHex);
            map.enablePossiblePaths(true, true);
        }
        return s;
    }
}
