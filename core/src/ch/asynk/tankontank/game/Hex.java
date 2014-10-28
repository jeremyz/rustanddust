package ch.asynk.tankontank.game;

import java.util.List;
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

    public static final int FOG         = 0;
    public static final int SELECT      = 1;
    public static final int MOVE1       = 2;
    public static final int TARGET      = 3;
    public static final int MOVE2       = 4;
    public static final int DIRECTIONS  = 5;
    public static final int ASSIST      = 6;
    public static final int ORIENTATION = 7;

    public Terrain terrain;
    public int roads;

    public String toString()
    {
        return "(" + col + ";" + row + ") [" + x + ";" + y + "]  " + "t:" + terrain + " r:" + roads;
    }

    public Hex(float x, float y, int col, int row, TextureAtlas atlas)
    {
        super(x, y, col, row, atlas);
        this.terrain = Terrain.CLEAR;
        this.roads = 0;
    }

    public Unit getUnit()
    {
        // FIXME ??????
        return (Unit) getTopPawn();
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

    @Override
    public int defenseFor(Pawn target, List<Pawn> foes)
    {
        Unit u = (Unit) target;
        boolean terrainBonus = true;

        for (Pawn foe : foes) {
            if (((Unit) foe).type == Unit.UnitType.INFANTRY)
                terrainBonus = false;
        }

        int tdef = 0;
        int def = u.def;
        switch(terrain) {
            case HILLS:
                if (u.type != Unit.UnitType.HARD_TARGET)
                    def = u.cdef;
                break;
            case WOODS:
            case TOWN:
                if (u.type != Unit.UnitType.HARD_TARGET)
                    def = u.cdef;
                if (terrainBonus)
                    tdef = 1;
                break;
            default:
                def = ((Unit) target).def;
                break;
        }

        System.err.println(" >= " + def + " + " + tdef);
        return (def + tdef);
    }
}
