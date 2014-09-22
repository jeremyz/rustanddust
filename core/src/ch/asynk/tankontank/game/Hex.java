package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.Board;

public class Hex extends Tile
{
    public enum Terrain
    {
        CLEAR,
        HILLS,
        WOODS,
        TOWN
    }

    public Terrain terrain;
    public int roads;

    public Hex(Terrain t, TextureAtlas atlas)
    {
        super(atlas);
        this.terrain = t;
        this.roads = 0;
    }

    public Hex(Terrain t, int roads, TextureAtlas atlas)
    {
        super(atlas);
        this.terrain = t;
        this.roads = roads;
    }

    public int costFrom(Board.Orientation side)
    {
        if (side.s == (roads & side.s)) return 1;

        int c = 0;
        switch(terrain) {
            case CLEAR:
            case HILLS:
                c = 1;
                break;
            case WOODS:
            case TOWN:
                c = 2;
                break;
        }

        return c;
    }
}
