package ch.asynk.tankontank.game;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;

public class HexMap extends Image
{
    static final int x0 = 83;       // map offset
    static final int y0 = 182;      // map offset
    static final int h = 110;       // hex side
    static final float dh = 53.6f;  // hex top     should be h/2
    static final int w = 189;       // hex width
    static final int dw = 94;       // half hex    should be w/2
    static final float H = h + dh;  // total height
    static final float slope = (dh / (float) dw);

    private int cols;
    private int rows;
    private ArrayDeque<Pawn>[][] cells;

    @SuppressWarnings("unchecked")
    public HexMap(int cols, int rows, Texture texture)
    {
        super(texture);
        cells = new ArrayDeque[rows][];
        for (int i = 0; i < rows; i++) {
            if ((i % 2) == 1) cells[i] = new ArrayDeque[cols - 1];
            else cells[i] = new ArrayDeque[cols];
        }
        this.cols = cols - 1;
        this.rows = rows - 1;
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

    public void setImageCenterAt(Image image, GridPoint2 cell)
    {
        float x = x0 + ((cell.x * w) + (w / 2));
        float y = y0 + ((cell.y * H) + (h / 2));
        if ((cell.y % 2) == 1) x += dw;
        image.setCenterPosition(x, y);
    }

    // public Vector2 getPawnPosAt(Pawn pawn, GridPoint2 cell)
    // {
    //     return getPawnPosAt(pawn, cell.x, cell.y);
    // }

    public Vector2 getPawnPosAt(Pawn pawn, GridPoint3 cell)
    {
        return getPawnPosAt(pawn, cell.x, cell.y);
    }

    private Vector2 getPawnPosAt(Pawn pawn, int col, int row)
    {
        float x = x0 + ((col * w) + ((w - pawn.getHeight()) / 2));
        float y = y0 + ((row * H) + ((h - pawn.getWidth()) / 2));
        if ((row % 2) == 1) x += dw;
        return new Vector2(x, y);
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

    public void setPawnOn(Pawn pawn, GridPoint3 cell)
    {
        setPawnOn(pawn, cell.x, cell.y, cell.z);
    }

    private void setPawnOn(Pawn pawn, int col, int row, int angle)
    {
        GridPoint3 prev = pawn.getBoardPosition();
        if (prev != null) removePawnFrom(pawn, prev.x, prev.y);

        Vector2 pos = getPawnPosAt(pawn, col, row);
        pawn.setPosition(pos.x, pos.y);
        pawn.setRotation(angle);

        ArrayDeque<Pawn> st = cells[row][col];
        if (st == null) st = cells[row][col] = new ArrayDeque<Pawn>();
        st.push(pawn);
        pawn.setZIndex(st.size());
    }

    public GridPoint2 getCellAt(GridPoint2 cell, float cx, float cy)
    {
        // compute row
        int row;
        boolean oddRow = true;
        float y = (cy - y0);
        if (y < 0.f) {
            row = -1;
        } else {
            row = (int) (y / H);
            oddRow = ((row % 2) == 1);
        }

        // compute col
        int col;
        float x = (cx - x0);
        if (oddRow) x -= dw;
        if (x < 0.f) {
            col = -1;
        } else {
            col = (int) (x / w);
        }

        // check upper boundaries
        float dy = (y - (row * H));
        if (dy > h) {
            dy -= h;
            float dx = (x - (col * w));
            if (dx < dw) {
                if ((dx * slope) < dy) {
                    row += 1;
                    if (!oddRow) col -= 1;
                    oddRow = !oddRow;
                }
            } else {
                if (((w - dx) * slope) < dy) {
                    row += 1;
                    if (oddRow) col += 1;
                    oddRow = !oddRow;
                }
            }
        }

        // validate cell
        if ((col < 0) || (row < 0) || (row > rows) || (col > cols) || (oddRow && ((col +1)> cols)))
            cell.set(-1, -1);
        else
            cell.set(col, row);

        return cell;
    }
}
