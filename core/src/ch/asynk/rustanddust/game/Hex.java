package ch.asynk.rustanddust.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.rustanddust.engine.Pawn;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Orientation;

public class Hex extends Tile
{
    public enum Terrain implements TileTerrain
    {
        OFFMAP,
        BLOCKED,
        CLEAR,
        HILLS,
        WOODS,
        TOWN,
        DEPRESSION
    }

    public static final int FOG         = 0;
    public static final int SELECT      = 1;
    public static final int AREA        = 2;
    public static final int MOVE        = 3;
    public static final int DIRECTIONS  = 4;
    public static final int EXIT        = 5;
    public static final int OBJECTIVE       = 6;
    public static final int OBJECTIVE_HOLD  = 7;
    public static final int OBJECTIVE_GE    = 8;
    public static final int OBJECTIVE_US    = 9;

    public Terrain terrain;
    public int roads;

    public String toString()
    {
        return String.format("(%d;%d) [%f;%f] t:%s r:%d", col, row, x, y, terrain, roads);
    }

    public String toShort()
    {
        return String.format("(%d;%d)", col, row);
    }

    public Hex(float x, float y, int col, int row, int capacity, Army defaultArmy, TextureAtlas atlas)
    {
        super(x, y, col, row, capacity, defaultArmy, atlas);
        this.terrain = Terrain.CLEAR;
        this.roads = 0;
    }

    public Unit getUnit()
    {
        return (Unit) getTopPawn();
    }

    @Override
    public boolean isA(TileTerrain terrain)
    {
        return (this.terrain == terrain);
    }

    @Override
    public boolean isOffMap()
    {
        return isA(Terrain.OFFMAP);
    }

    @Override
    public boolean blockLineOfSight(Tile from, Tile to)
    {
        if (isA(Terrain.DEPRESSION))
            return false;

        if (isA(Terrain.CLEAR) && (!hasUnits() || from.isA(Terrain.HILLS) || to.isA(Terrain.HILLS)))
            return false;

        return true;
    }

    @Override
    public boolean atLeastOneMove(Pawn pawn)
    {
        if (hasUnits() || isA(Terrain.BLOCKED) || isA(Terrain.OFFMAP))
            return false;
        return true;
    }

    @Override
    public boolean road(Orientation side)
    {
        return (side.s == (roads & side.s));
    }

    @Override
    public int exitCost()
    {
        return 1;
    }

    @Override
    public int costFrom(Pawn pawn, Orientation side)
    {
        if (side == Orientation.KEEP) return 0;
        if (hasUnits()) return (Integer.MAX_VALUE / 2);
        if (road(side)) return 1;

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
                c = (Integer.MAX_VALUE / 2);
                break;
        }

        return c;
    }

    @Override
    public int defense()
    {
        return (isA(Terrain.TOWN) ? 1 : 0);
    }
}
