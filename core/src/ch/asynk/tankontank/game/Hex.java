package ch.asynk.tankontank.game;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.Board;

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

    @Override
    public boolean atLeastOneMove(Pawn pawn)
    {
        if (occupied() || (terrain == Terrain.BLOCKED) || (terrain == Terrain.OFFMAP))
            return false;
        return true;
    }

    @Override
    public boolean road(Board.Orientation side)
    {
        return (side.s == (roads & side.s));
    }

    @Override
    public int costFrom(Pawn pawn, Board.Orientation side, boolean road)
    {
        if (occupied()) return Integer.MAX_VALUE;
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
    public boolean hasTargetsFor(Pawn pawn)
    {
        if (!occupied()) return false;

        Iterator<Pawn> itr = stack.iterator();
        while(itr.hasNext())
            if (itr.next().isEnemy(pawn)) return true;

        return false;
    }
}
