package ch.asynk.tankontank.engine;

import com.badlogic.gdx.math.Vector3;

public interface Pawn
{
    // libgdx

    public float getWidth();
    public float getHeight();
    public void setZIndex(int z);

    // game

    public Vector3 getLastPosition();

    public void moveBy(float x, float y);

    public void pushMove(float x, float y, int z, Pawn.Orientation o);

    public void resetMoves(Runnable cb);

    public void moveDone();

    public enum Orientation
    {
        KEEP(0),
        WEST(-90),
        NORTH_WEST(-30),
        NORTH_EAST (30),
        EAST(90),
        SOUTH_EAST(150),
        SOUTH_WEST(-150);

        public final int v;
        Orientation(int v) { this.v = v; }
    }
}
