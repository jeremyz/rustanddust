package ch.asynk.rustanddust.game.battles;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Board;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Hex;

public class MapA extends Map
{
    public MapA(final RustAndDust game, String map, String hex)
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
        getHex(5, 1).terrain = Hex.Terrain.HILLS;
        getHex(7, 3).terrain = Hex.Terrain.HILLS;
        getHex(7, 8).terrain = Hex.Terrain.HILLS;
        getHex(8, 8).terrain = Hex.Terrain.HILLS;

        getHex(5, 0).terrain = Hex.Terrain.WOODS;
        getHex(6, 0).terrain = Hex.Terrain.WOODS;
        getHex(3, 3).terrain = Hex.Terrain.WOODS;
        getHex(4, 3).terrain = Hex.Terrain.WOODS;
        getHex(10, 7).terrain = Hex.Terrain.WOODS;
        getHex(11, 7).terrain = Hex.Terrain.WOODS;
        getHex(11, 8).terrain = Hex.Terrain.WOODS;

        getHex(6, 1).terrain = Hex.Terrain.TOWN;
        getHex(2, 2).terrain = Hex.Terrain.TOWN;
        getHex(6, 4).terrain = Hex.Terrain.TOWN;
        getHex(10, 5).terrain = Hex.Terrain.TOWN;
        getHex(7, 7).terrain = Hex.Terrain.TOWN;
        getHex(4, 6).terrain = Hex.Terrain.TOWN;

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

        getHex(6, 1).roads = (NW | SW);
        for (int i = 1; i < 11; i++) {
            if (i == 6)
                getHex(i, 2).roads = (NE | S | SW);
            else if (i == 7)
                getHex(i, 2).roads = (N | SE);
            else
                getHex(i, 2).roads = (N | S);
        }
        getHex(6, 3).roads = (NE | SW);
        getHex(6, 4).roads = (N | NE | SW);
        getHex(7, 4).roads = (N | S);
        getHex(8, 4).roads = (NW | S);
        getHex(6, 5).roads = (NE | SW);
        getHex(8, 5).roads = (N | SW);
        getHex(9, 5).roads = (N | S | NE);
        getHex(10, 5).roads = (N | S);
        getHex(11, 5).roads = (N | S);
        getHex(3, 6).roads = (N | S);
        getHex(4, 6).roads = (N | S);
        getHex(5, 6).roads = (N | S);
        getHex(6, 6).roads = (NE | NW | S);
        getHex(8, 6).roads = (NE | SW);
        getHex(7, 7).roads = (N | SE);
        getHex(8, 7).roads = (NE | S);
    }
}
