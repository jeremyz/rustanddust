package ch.asynk.tankontank.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;

public interface HexMap
{
    // Gfx related

    public float getHeight();

    public float getWidth();

    // Map related

    public Pawn getTopPawnAt(GridPoint2 hex);

    public Vector2 getHexCenterAt(GridPoint2 hex);

    public Vector2 getPawnPosAt(Pawn pawn, GridPoint3 hex);

    public void setPawnAt(Pawn pawn, GridPoint3 hex);

    public GridPoint2 getHexAt(GridPoint2 hex, float x, float y);
}
