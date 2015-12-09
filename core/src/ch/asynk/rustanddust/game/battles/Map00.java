package ch.asynk.rustanddust.game.battles;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Board;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Hex;

public class Map00 extends Map
{
    public Map00(final RustAndDust game, String map, String hex)
    {
        super(game, map, hex);
        setup();
    }

    @Override
    protected Board.Config getConfig()
    {
        Board.Config cfg = new Board.Config();
        cfg.cols = 10;
        cfg.rows =  9;
        cfg.s  = 110;
        cfg.x0 = 50;
        cfg.y0 = 103;
        cfg.w  = cfg.s * 1.73205f;
        cfg.dw = cfg.w / 2.0f;
        cfg.dh = cfg.s / 2.0f;
        cfg.h  = cfg.s + cfg.dh;
        cfg.slope = (cfg.dh / (float) cfg.dw);

        return cfg;
    }

    private void setup()
    {
        getHex(6, 1).terrain = Hex.Terrain.HILLS;
        getHex(5, 7).terrain = Hex.Terrain.HILLS;
        getHex(6, 8).terrain = Hex.Terrain.HILLS;
        getHex(7, 8).terrain = Hex.Terrain.HILLS;
        getHex(9, 4).terrain = Hex.Terrain.HILLS;
        getHex(10, 4).terrain = Hex.Terrain.HILLS;

        getHex(2, 2).terrain = Hex.Terrain.WOODS;
        getHex(3, 3).terrain = Hex.Terrain.WOODS;
        getHex(6, 5).terrain = Hex.Terrain.WOODS;
        getHex(7, 5).terrain = Hex.Terrain.WOODS;
        getHex(8, 6).terrain = Hex.Terrain.WOODS;
        getHex(9, 6).terrain = Hex.Terrain.WOODS;
        getHex(8, 1).terrain = Hex.Terrain.WOODS;
        getHex(8, 2).terrain = Hex.Terrain.WOODS;

        getHex(5, 2).terrain = Hex.Terrain.TOWN;
        getHex(11, 7).terrain = Hex.Terrain.TOWN;

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

        getHex(3, 5).roads = (S | N);
        getHex(4, 5).roads = (S | N);
        getHex(5, 5).roads = (S | NE);
        getHex(5, 4).roads = (SW | N);
        getHex(6, 4).roads = (S | SE | N);
        getHex(7, 4).roads = (S | N);
        getHex(8, 4).roads = (S | NW | NE);
        getHex(8, 3).roads = (SW | N);
        getHex(9, 3).roads = (S | N);
        getHex(10, 3).roads = (S | N);

        getHex(9, 5).roads = (SE | NW);
        getHex(10, 6).roads = (SE | NW);
        getHex(11, 7).roads = (SE | SW);
        getHex(11, 8).roads = (NE | SW);

        getHex(5, 3).roads = (NE | NW);
        getHex(5, 2).roads = (SE | SW);
        getHex(4, 1).roads = (NE | NW);
        getHex(4, 0).roads = (SW | NE | NW);
    }
}
