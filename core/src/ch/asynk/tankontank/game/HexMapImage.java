package ch.asynk.tankontank.game;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;

public class HexMapImage extends Image implements HexMap
{
    private HexMap.Config cfg;
    private int cols;
    private int rows;
    private ArrayDeque<Pawn>[][] cells;

    @SuppressWarnings("unchecked")
    public HexMapImage(HexMap.Config cfg, Texture texture)
    {
        super(texture);
        cells = new ArrayDeque[cfg.rows][];
        for (int i = 0; i < cfg.rows; i++) {
            if ((i % 2) == 1) cells[i] = new ArrayDeque[cfg.cols - 1];
            else cells[i] = new ArrayDeque[cfg.cols];
        }
        this.cfg = cfg;
        this.cols = cfg.cols - 1;
        this.rows = cfg.rows - 1;
    }

    public Pawn getTopPawnAt(GridPoint2 cell)
    {
        return getTopPawnAt(cell.x, cell.y);
    }

    private Pawn getTopPawnAt(int col, int row)
    {
        if ((col < 0) || (row < 0)) return null;
        ArrayDeque<Pawn> st = cells[row][col];
        if ((st == null) || (st.size() == 0)) return null;
        return st.getFirst();
    }

    public Vector2 getHexCenterAt(GridPoint2 cell)
    {
        float x = cfg.x0 + ((cell.x * cfg.w) + (cfg.w / 2));
        float y = cfg.y0 + ((cell.y * cfg.H) + (cfg.h / 2));
        if ((cell.y % 2) == 1) x += cfg.dw;
        return new Vector2(x, y);
    }

    public Vector2 getPawnPosAt(Pawn pawn, GridPoint2 cell)
    {
        return getPawnPosAt(pawn, cell.x, cell.y);
    }

    private Vector2 getPawnPosAt(Pawn pawn, int col, int row)
    {
        float x = cfg.x0 + ((col * cfg.w) + ((cfg.w - pawn.getHeight()) / 2));
        float y = cfg.y0 + ((row * cfg.H) + ((cfg.h - pawn.getWidth()) / 2));
        if ((row % 2) == 1) x += cfg.dw;
        return new Vector2(x, y);
    }

    private int pushPawnAt(Pawn pawn, int col, int row)
    {
        ArrayDeque<Pawn> st = cells[row][col];
        if (st == null) st = cells[row][col] = new ArrayDeque<Pawn>();
        st.push(pawn);
        return st.size();
    }

    private void removePawnFrom(Pawn pawn, int col, int row)
    {
        if ((col> 0) && (row > 0)) {
            ArrayDeque<Pawn> st = cells[row][col];
            if ((st == null) || (st.size() == 0))
                Gdx.app.error("GameScreen", "remove pawn from " + col + ";" + row + " but pawn stack is empty");
            else
                st.remove(pawn);
        }
    }

    public void movePawnTo(Pawn pawn, Vector3 coords)
    {
        GridPoint2 p = getHexAt(null, coords.x, coords.y);
        movePawnTo(pawn, p.x, p.y, HexOrientation.KEEP);
    }

    public void setPawnAt(final Pawn pawn, final int col, final int row, HexOrientation o)
    {
        int z = pushPawnAt(pawn, col, row);
        Vector2 pos = getPawnPosAt(pawn, col, row);
        pawn.pushMove(pos.x, pos.y, z, o);
    }

    public void movePawnTo(final Pawn pawn, final int col, final int row, HexOrientation o)
    {
        GridPoint2 prev = getHexAt(pawn.getLastPosition());
        if (prev != null) removePawnFrom(pawn, prev.x, prev.y);

        if ((col < 0) || (row < 0)) {
            pawn.resetMoves(new Runnable() {
                @Override
                public void run() {
                    GridPoint2 hex = getHexAt(pawn.getLastPosition());
                    pawn.setZIndex(pushPawnAt(pawn, hex.x, hex.y));
                }
            });
            return;
        } else {
            int z = pushPawnAt(pawn, col, row);
            Vector2 pos = getPawnPosAt(pawn, col, row);
            pawn.pushMove(pos.x, pos.y, z, o);
        }
    }

    private GridPoint2 getHexAt(Vector3 v)
    {
        if (v == null) return null;
        return getHexAt(null, v.x, v.y);
    }

    public GridPoint2 getHexAt(GridPoint2 hex, float cx, float cy)
    {
        if (hex == null) hex = new GridPoint2();

        // compute row
        int row;
        boolean oddRow = true;
        float y = (cy - cfg.y0);
        if (y < 0.f) {
            row = -1;
        } else {
            row = (int) (y / cfg.H);
            oddRow = ((row % 2) == 1);
        }

        // compute col
        int col;
        float x = (cx - cfg.x0);
        if (oddRow) x -= cfg.dw;
        if (x < 0.f) {
            col = -1;
        } else {
            col = (int) (x / cfg.w);
        }

        // check upper boundaries
        float dy = (y - (row * cfg.H));
        if (dy > cfg.h) {
            dy -= cfg.h;
            float dx = (x - (col * cfg.w));
            if (dx < cfg.dw) {
                if ((dx * cfg.slope) < dy) {
                    row += 1;
                    if (!oddRow) col -= 1;
                    oddRow = !oddRow;
                }
            } else {
                if (((cfg.w - dx) * cfg.slope) < dy) {
                    row += 1;
                    if (oddRow) col += 1;
                    oddRow = !oddRow;
                }
            }
        }

        // validate hex
        if ((col < 0) || (row < 0) || (row > rows) || (col > cols) || (oddRow && ((col +1)> cols)))
            hex.set(-1, -1);
        else
            hex.set(col, row);

        return hex;
    }
}
