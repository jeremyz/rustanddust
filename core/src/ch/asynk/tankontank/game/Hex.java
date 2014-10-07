package ch.asynk.tankontank.game;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.Orientation;

public class Hex extends Tile
{
    public enum Terrain
    {
        OFFMAP,
        BLOCKED,
        CLEAR,
        HILLS,
        WOODS,
        TOWN
    }

    public static final int FOG = 0;
    public static final int BLUE = 1;
    public static final int GREEN = 2;
    public static final int RED = 3;
    public static final int MOVE = 4;
    public static final int ROSE = 5;

    public Terrain terrain;
    public int roads;

    public String toString()
    {
        return "[" + getCenter().x + ";" + getCenter().y + "]  " + "t:" + terrain + " r:" + roads;
    }

    public Hex(float x, float y, TextureAtlas atlas)
    {
        super(x, y, atlas);
        this.terrain = Terrain.CLEAR;
        this.roads = 0;
    }

    @Override
    public boolean isOffMap()
    {
        return terrain == Terrain.OFFMAP;
    }

    @Override
    public boolean blockLineOfSightFrom(Tile tile)
    {
        if ((terrain == Terrain.CLEAR) && !hasUnits())
            return false;

        if ((((Hex) tile).terrain == Terrain.HILLS) && (terrain == Terrain.CLEAR))
            return false;

        return true;
    }

    @Override
    public boolean atLeastOneMove(Pawn pawn)
    {
        if (hasUnits() || (terrain == Terrain.BLOCKED) || (terrain == Terrain.OFFMAP))
            return false;
        return true;
    }

    @Override
    public boolean road(Orientation side)
    {
        return (side.s == (roads & side.s));
    }

    @Override
    public int costFrom(Pawn pawn, Orientation side, boolean road)
    {
        if (hasUnits()) return Integer.MAX_VALUE;
        if (road) return 1;

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
            case OFFMAP:
            case BLOCKED:
                c = Integer.MAX_VALUE;
                break;
        }

        return c;
    }
}
