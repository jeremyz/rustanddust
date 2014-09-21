package ch.asynk.tankontank.engine;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.engine.gfx.Drawable;

public interface Map extends Drawable, Disposable
{
    public void animate(float delta);

    public GridPoint2 getHexAt(GridPoint2 hex, float x, float y);

    public Pawn getTopPawnAt(GridPoint2 hex);

    public Vector2 getHexCenterAt(GridPoint2 hex);

    public Vector2 getPawnPosAt(Pawn pawn, GridPoint2 hex);

    public void setPawnAt(Pawn pawn, int col, int row, Pawn.Orientation o);

    public void movePawnTo(Pawn pawn, Vector3 coords);

    public void movePawnTo(Pawn pawn, GridPoint2 hex);

    public void movePawnTo(Pawn pawn, int col, int row, Pawn.Orientation o);

    public class Config
    {
        public int cols;
        public int rows;
        public int x0;          // bottom left x offset
        public int y0;          // bottom left y offset
        public int w;           // hex width
        public int dw;          // half hex : w/2
        public int s;           // hex side
        public float dh;        // hex top : s/2
        public float h;         // square height : s + dh
        public float slope;     // north-west side slope : (dh / (float) dw)
    }

    public void touchUp(float x, float y);
    public void touchDown(float x, float y);
    public boolean drag(float dx, float dy);
}
