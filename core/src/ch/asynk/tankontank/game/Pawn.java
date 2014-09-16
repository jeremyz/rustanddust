package ch.asynk.tankontank.game;

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

    public void pushMove(float x, float y, int z, Tile.Orientation o);

    public void resetMoves(Runnable cb);

    public void moveDone();
}
