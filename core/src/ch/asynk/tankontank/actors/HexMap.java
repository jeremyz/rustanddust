package ch.asynk.tankontank.actors;

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
    private ArrayDeque<Tile>[][] cells;

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

    public Tile getTopTileAt(GridPoint2 cell)
    {
        return getTopTileAt(cell.x, cell.y);
    }

    private Tile getTopTileAt(int col, int row)
    {
        if ((col < 0) || (row < 0)) return null;
        ArrayDeque<Tile> st = cells[row][col];
        if ((st == null) || (st.size() == 0)) return null;
        return st.getFirst();
    }

    public Vector2 getTilePosAt(Tile tile, GridPoint3 cell)
    {
        return getTilePosAt(tile, cell.x, cell.y);
    }

    private Vector2 getTilePosAt(Tile tile, int col, int row)
    {
        float x = x0 + ((col * w) + ((w - tile.getHeight()) / 2));
        float y = y0 + ((row * H) + ((h - tile.getWidth()) / 2));
        if ((row % 2) == 1) x += dw;
        return new Vector2(x, y);
    }

    private void removeTileFrom(Tile tile, int col, int row)
    {
        if ((col> 0) && (row > 0)) {
            ArrayDeque<Tile> st = cells[row][col];
            if ((st == null) || (st.size() == 0))
                Gdx.app.error("GameScreen", "remove tile from " + col + ";" + row + " but tile stack is empty");
            else
                st.remove(tile);
        }
    }

    public void setTileOn(Tile tile, GridPoint3 cell)
    {
        setTileOn(tile, cell.x, cell.y, cell.z);
    }

    private void setTileOn(Tile tile, int col, int row, int angle)
    {
        GridPoint3 prev = tile.cell;
        if (prev != null) removeTileFrom(tile, prev.x, prev.y);

        Vector2 pos = getTilePosAt(tile, col, row);
        tile.setPosition(pos.x, pos.y);
        tile.setRotation(angle);

        ArrayDeque<Tile> st = cells[row][col];
        if (st == null) st = cells[row][col] = new ArrayDeque<Tile>();
        st.push(tile);
        tile.setZIndex(st.size());
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
