package ch.asynk.tankontank.game.states;

public class GameStateMove extends GameStateCommon
{
    private boolean skipFirst;

    @Override
    public void enter()
    {
        map.enablePossibleTargets(false);
        map.enablePossiblePaths(false, false);
        if (hasPawn()) {
            selectHex(hex);
            skipFirst = false;
            map.clearPossiblePaths();
            buildAndShowMoves();
        }
    }

    @Override
    public void touchDown()
    {
        if (!hasPawn()) {
            reselectHex();
            if (hasPawn()) {
                skipFirst = true;
                map.clearPossiblePaths();
                buildAndShowMoves();
            }
        }
    }

    @Override
    public void touchUp()
    {
        if (!hasPawn()) {
            unselectHex(hex);
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
        clear();
        super.abort();
    }

    private void buildAndShowMoves()
    {
        map.enablePossibleMoves(false);
        map.enableMoveAssists(false);
        map.buildPossibleMoves(pawn, hex);
        map.buildMoveAssists(pawn, hex);
        map.enablePossibleMoves(true);
        map.enableMoveAssists(true);
    }

    private void clear()
    {
        map.enableMoveAssists(false);
        map.enablePossibleMoves(false);
        map.enableFinalPath(tmp, false);
        if (to.x != -1) unselectHex(to);
        if (from.x != -1) unselectHex(to);
        to.set(-1, -1);
        from.set(-1, -1);
    }

    private int buildPaths()
    {
        from.set(hex.x, hex.y);
        to.set(upHex.x, upHex.y);
        map.clearPossiblePaths();
        int s = map.buildPossiblePaths(pawn, from, to);
        selectHex(to);
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
