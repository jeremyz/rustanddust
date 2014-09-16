package ch.asynk.tankontank.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;

public interface HexMap
{
    // libgdx

    public float getWidth();
    public float getHeight();

    // game

    public GridPoint2 getHexAt(GridPoint2 hex, float x, float y);

    public Pawn getTopPawnAt(GridPoint2 hex);

    public Vector2 getHexCenterAt(GridPoint2 hex);

    public Vector2 getPawnPosAt(Pawn pawn, GridPoint2 hex);

    public void movePawnTo(Pawn pawn, Vector3 coords);

    public void movePawnTo(Pawn pawn, int col, int row, HexOrientation o);

}
