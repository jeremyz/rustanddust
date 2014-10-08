package ch.asynk.tankontank.game.states;

public class GameStateMove extends GameStateCommon
{
    @Override
    public void enter()
    {
        map.clearPossiblePaths();
        buildAndShowMoves();
        ctrl.hud.show(false, true, false, true, true);
    }

    @Override
    public void leave()
    {
    }

    @Override
    public void touchDown()
    {
    }

    @Override
    public void touchUp()
    {
        int s = map.possiblePathsSize();

        if (s == 0) {
            if (map.isInPossibleMoves(upHex))
                s = buildPaths();
        } else {
            if (map.isInPossiblePaths(upHex))
                s = togglePoint(s);
        }

        if (s == 1)
            ctrl.setState(State.DIRECTION);
    }

    @Override
    public void abort()
    {
        map.enableMoveAssists(false);
        map.enablePossibleMoves(false);
        if (from.x != -1) {
            unselectHex(from);
            from.set(-1, -1);
        }
        if (to.x != -1) {
            unselectHex(to);
            map.enableFinalPath(to, false);
            to.set(-1, -1);
        }
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

    private int buildPaths()
    {
        from.set(hex.x, hex.y);
        to.set(upHex.x, upHex.y);
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
