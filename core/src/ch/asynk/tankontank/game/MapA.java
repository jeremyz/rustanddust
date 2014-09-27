package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Board;

public class MapA extends Map
{
    public MapA(GameFactory gameFactory, Board.Config cfg, Texture texture)
    {
        super(gameFactory, cfg, texture);
    }

    @Override
    protected void setup()
    {
        getHex(4, 1).terrain = Hex.Terrain.HILLS;
        getHex(5, 3).terrain = Hex.Terrain.HILLS;
        getHex(3, 8).terrain = Hex.Terrain.HILLS;
        getHex(4, 8).terrain = Hex.Terrain.HILLS;

        getHex(5, 0).terrain = Hex.Terrain.WOODS;
        getHex(6, 0).terrain = Hex.Terrain.WOODS;
        getHex(1, 3).terrain = Hex.Terrain.WOODS;
        getHex(2, 3).terrain = Hex.Terrain.WOODS;
        getHex(6, 7).terrain = Hex.Terrain.WOODS;
        getHex(7, 7).terrain = Hex.Terrain.WOODS;
        getHex(7, 8).terrain = Hex.Terrain.WOODS;

        getHex(5, 1).terrain = Hex.Terrain.TOWN;
        getHex(1, 2).terrain = Hex.Terrain.TOWN;
        getHex(4, 4).terrain = Hex.Terrain.TOWN;
        getHex(7, 5).terrain = Hex.Terrain.TOWN;
        getHex(1, 6).terrain = Hex.Terrain.TOWN;
        getHex(3, 7).terrain = Hex.Terrain.TOWN;

        getHex(9, 1).terrain = Hex.Terrain.OFFMAP;
        getHex(9, 3).terrain = Hex.Terrain.OFFMAP;
        getHex(9, 5).terrain = Hex.Terrain.OFFMAP;
        getHex(9, 7).terrain = Hex.Terrain.OFFMAP;

        int N = Map.Orientation.NORTH.s;
        int S = Map.Orientation.SOUTH.s;
        int NE = Map.Orientation.NORTH_EAST.s;
        int NW = Map.Orientation.NORTH_WEST.s;
        int SE = Map.Orientation.SOUTH_EAST.s;
        int SW = Map.Orientation.SOUTH_WEST.s;

        getHex(5, 1).roads = (NW | SW);
        for (int i = 0; i < 10; i++) {
            if (i == 5)
                getHex(i, 2).roads = (NE | S | SW);
            else if (i == 6)
                getHex(i, 2).roads = (N | SE);
            else
                getHex(i, 2).roads = (N | S);
        }
        getHex(4, 3).roads = (NE | SW);
        getHex(4, 4).roads = (N | NE | SW);
        getHex(5, 4).roads = (N | S);
        getHex(6, 4).roads = (NW | S);
        getHex(3, 5).roads = (NE | SW);
        getHex(5, 5).roads = (N | SW);
        getHex(6, 5).roads = (N | S | NE);
        getHex(7, 5).roads = (N | S);
        getHex(8, 5).roads = (N | S);
        getHex(0, 6).roads = (N | S);
        getHex(1, 6).roads = (N | S);
        getHex(2, 6).roads = (N | S);
        getHex(3, 6).roads = (NE | NW | S);
        getHex(5, 6).roads = (NE | SW);
        getHex(3, 7).roads = (N | SE);
        getHex(4, 7).roads = (NE | S);
    }
}
