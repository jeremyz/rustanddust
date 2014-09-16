package ch.asynk.tankontank.game;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;

public interface Pawn
{
    // Gfx related

    public float getHeight();

    public float getWidth();

    public void moveBy(float x, float y);

    public void setPosition(float x, float y);

    public void setRotation(float angle);

    public void setZIndex(int zIndex);

    // Board related

    public GridPoint3 getBoardPosition();

    public void moveTo(GridPoint2 hex);

    public void moveTo(int col, int row, int angle);

    public void resetMoves();

    public void moveDone();
}
