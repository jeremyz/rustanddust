package ch.asynk.tankontank.game.states;

public class GameStateMove extends GameStateCommon
{
    @Override
    public void enter()
    {
        map.clearPossiblePaths();
        buildAndShowMoves();
        ctrl.hud.show(false, true, false, ctrl.cfg.mustValidate, ctrl.cfg.canCancel);
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
        map.showMoveAssists(false);
        map.showPossibleMoves(false);
        if (from.x != -1) {
            unselectHex(from);
            from.set(-1, -1);
        }
        if (to.x != -1) {
            unselectHex(to);
            map.showFinalPath(to, false);
            to.set(-1, -1);
        }
        super.abort();
    }

    private void buildAndShowMoves()
    {
        map.showPossibleMoves(false);
        map.showMoveAssists(false);
        map.buildPossibleMoves(pawn, hex);
        map.buildMoveAssists(pawn, hex);
        map.showPossibleMoves(true);
        map.showMoveAssists(true);
    }

    private int buildPaths()
    {
        from.set(hex.x, hex.y);
        to.set(upHex.x, upHex.y);
        int s = map.buildPossiblePaths(pawn, from, to);
        selectHex(to);
        map.showPossibleMoves(false);
        map.showPossiblePaths(true, true);
        return s;
    }

    private int togglePoint(int s)
    {
        if (sameHexes(downHex, from)) {
            //
        } else if (sameHexes(downHex, to)) {
            //
        } else {
            map.showPossiblePaths(false, true);
            map.togglePathOverlay(downHex);
            s = map.possiblePathsPointToggle(downHex);
            map.showPossiblePaths(true, true);
        }

        return s;
    }
}
