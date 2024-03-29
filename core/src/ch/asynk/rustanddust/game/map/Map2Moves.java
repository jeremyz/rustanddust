package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.TileSet;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.PathBuilder;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Hex;

public abstract class Map2Moves extends Map1Units
{
    protected final TileSet moves;
    protected final PathBuilder paths;

    public Map2Moves(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);

        moves = new TileSet(this, 40);
        paths = new PathBuilder(this, 10, 20, 5, 20);
    }

    @Override
    public void dispose()
    {
        clearMoves();
        super.dispose();
        paths.dispose();
    }

    public void clearMoves()
    {
        moves.clear();
        paths.clear();
    }

    public boolean movesContains(Hex hex)
    {
        return moves.contains(hex);
    }

    public int movesCollect(Unit unit)
    {
        moves.clear();
        if (unit.canMove())
            return collectPossibleMoves(unit, moves);
        return 0;
    }

    public int pathsSize()                          { return paths.size(); }
    public void pathsClear()                        { paths.clear(); }
    public void pathsInit(Unit unit)                { paths.init(unit); }
    public void pathsInit(Unit unit, Hex from)      { paths.init(unit, from); }
    public int pathsBuild(Hex hex)                  { return paths.build(hex); }
    public int pathsBuildShortest(Hex hex)          { return paths.buildShortest(hex); }
    public Hex pathsTo()                            { return (Hex) paths.to; }
    public void pathsSetOrientation(Orientation o)  { paths.orientation = o; }
    public boolean pathsIsSet()                     { return paths.isSet(); }
    public boolean pathsCanExit(Orientation o)      { return paths.canExit(o); }
    public boolean pathsContains(Hex hex)           { return paths.contains(hex); }
    public int pathsChooseBest()                    { return paths.chooseBest(); }
    public int pathsChooseExit(Orientation o)       { return paths.chooseExit(o); }
    public int pathsToggleHex(Hex hex)
    {
        boolean enable = !hex.isOverlayEnabled(Hex.MOVE);
        enableOverlayOn(hex, Hex.MOVE, enable);
        return paths.toggleCtrlTile(hex, true);
    }

    public void movesShow()             { moves.enable(Hex.AREA, true); }
    public void movesHide()             { moves.enable(Hex.AREA, false); }
    public void pathsShow()             { paths.enable(Hex.AREA, true); }
    public void pathsHide()             { paths.enable(Hex.AREA, false); }
    public void pathShow(Hex dst)       { paths.enable(Hex.MOVE, true); hexMoveShow(dst); }
    public void pathHide(Hex dst)       { paths.enable(Hex.MOVE, false); hexMoveHide(dst); }
}
