package ch.asynk.tankontank.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;

public interface Map
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

    public void setPawnAt(Pawn pawn, int col, int row, Tile.Orientation o);

    public void movePawnTo(Pawn pawn, int col, int row, Tile.Orientation o);

    public class Config
    {
        public int cols;
        public int rows;
        public int x0 = 83;       // map offset
        public int y0 = 182;      // map offset
        public int h = 110;       // hex side
        public float dh = 53.6f;  // hex top     should be h/2
        public int w = 189;       // hex width
        public int dw = 94;       // half hex    should be w/2
        public float H = h + dh;  // total height
        public float slope = (dh / (float) dw);
    }
}
