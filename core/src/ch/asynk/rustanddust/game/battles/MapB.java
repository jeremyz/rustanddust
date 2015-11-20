package ch.asynk.rustanddust.game.battles;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Board;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Hex;

public class MapB extends Map
{
    public MapB(final RustAndDust game, String map, String hex)
    {
        super(game, map, hex);
    }

    @Override
    protected Board.Config getConfig()
    {
        Board.Config cfg = new Board.Config();
        cfg.cols = 10;
        cfg.rows = 9;
        cfg.x0 = 86;
        cfg.y0 = 182;
        cfg.w = 189;
        cfg.dw = 94;
        cfg.s = 110;
        cfg.dh = 53.6f;
        cfg.h = cfg.s + cfg.dh;
        cfg.slope = (cfg.dh / (float) cfg.dw);

        return cfg;
    }

    @Override
    protected void setup()
    {
        getHex(4, 0).terrain = Hex.Terrain.HILLS;
        getHex(5, 0).terrain = Hex.Terrain.HILLS;
        getHex(1, 1).terrain = Hex.Terrain.HILLS;
        getHex(9, 7).terrain = Hex.Terrain.HILLS;
        getHex(10, 7).terrain = Hex.Terrain.HILLS;

        getHex(3, 0).terrain = Hex.Terrain.WOODS;
        getHex(6, 0).terrain = Hex.Terrain.WOODS;
        getHex(8, 1).terrain = Hex.Terrain.WOODS;
        getHex(9, 2).terrain = Hex.Terrain.WOODS;
        getHex(4, 5).terrain = Hex.Terrain.WOODS;
        getHex(5, 6).terrain = Hex.Terrain.WOODS;
        getHex(6, 6).terrain = Hex.Terrain.WOODS;
        getHex(11, 8).terrain = Hex.Terrain.WOODS;

        getHex(1, 2).terrain = Hex.Terrain.TOWN;
        getHex(5, 3).terrain = Hex.Terrain.TOWN;
        getHex(6, 4).terrain = Hex.Terrain.TOWN;
        getHex(7, 8).terrain = Hex.Terrain.TOWN;

        getHex(10, 1).terrain = Hex.Terrain.OFFMAP;
        getHex(11, 3).terrain = Hex.Terrain.OFFMAP;
        getHex(12, 5).terrain = Hex.Terrain.OFFMAP;
        getHex(13, 7).terrain = Hex.Terrain.OFFMAP;

        int N = Orientation.NORTH.s;
        int S = Orientation.SOUTH.s;
        int NE = Orientation.NORTH_EAST.s;
        int NW = Orientation.NORTH_WEST.s;
        int SE = Orientation.SOUTH_EAST.s;
        int SW = Orientation.SOUTH_WEST.s;

        getHex(1, 2).roads = (S | NW);
        getHex(2, 3).roads = (SE | N);
        getHex(3, 3).roads = (S | N);
        getHex(4, 3).roads = (S | N);
        getHex(5, 3).roads = (S | NW);
        getHex(6, 4).roads = (SE | N);
        getHex(7, 4).roads = (S | N);
        getHex(8, 4).roads = (S | SW | N);
        getHex(9, 4).roads = (S | N);
        getHex(10, 4).roads = (S | N);
        getHex(11, 4).roads = (S | N);
        getHex(4, 8).roads = (S | N);
        getHex(5, 8).roads = (S | N);
        getHex(6, 8).roads = (S | N);
        getHex(7, 8).roads = (S | N);
        getHex(8, 8).roads = (S | NE);
        getHex(8, 7).roads = (SW | NE);
        getHex(8, 6).roads = (SW | NE | N);
        getHex(8, 5).roads = (SW | NE);
        getHex(9, 6).roads = (S | N);
        getHex(10, 6).roads = (S | N);
        getHex(11, 6).roads = (S | N);
        getHex(12, 6).roads = (S | N);
    }
}
