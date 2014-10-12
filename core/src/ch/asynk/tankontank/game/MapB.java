package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;

public class MapB extends Map
{
    public MapB(Ctrl ctrl, GameFactory factory, Board.Config cfg, Texture texture)
    {
        super(ctrl, factory, cfg, texture);
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
