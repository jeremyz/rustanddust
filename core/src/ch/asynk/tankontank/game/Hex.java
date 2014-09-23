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

    public static final int FOG = 1;
    public static final int RED = 3;
    public static final int GREEN = 2;
    public static final int BLUE = 0;

    public static TextureAtlas atlas = null;

    public Terrain terrain;
    public int roads;

    @Override
    public Hex getNewAt(float x, float y)
    {
        Hex hex = new Hex(atlas);
        hex.setPosition(x, y, 0);
        return hex;
    }

    public Hex(TextureAtlas atlas)
    {
        super(atlas);
        this.terrain = Terrain.CLEAR;
        this.roads = 0;
        Hex.atlas = atlas;
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
