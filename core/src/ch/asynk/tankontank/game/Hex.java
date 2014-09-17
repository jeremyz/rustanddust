package ch.asynk.tankontank.game;

import java.util.ArrayDeque;

import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.Pawn;

public class Hex implements Tile
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
    private ArrayDeque<Pawn> stack;

    public Hex(Terrain t)
    {
        this.terrain = t;
        this.roads = 0;
        this.stack = null;
    }

    public Hex(Terrain t, int roads)
    {
        this.terrain = t;
        this.roads = roads;
        this.stack = null;
    }

    public int costFrom(Side side)
    {
        if (side.v == (roads & side.v)) return 1;

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

    public int push(Pawn pawn)
    {
        if (stack == null) stack = new ArrayDeque<Pawn>();
        stack.push(pawn);
        return stack.size();
    }

    public void remove(Pawn pawn)
    {
        stack.remove(pawn);
    }

    public Pawn getTop()
    {
        if ((stack == null) || (stack.size() == 0)) return null;
        return stack.getFirst();
    }
}
