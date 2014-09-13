package ch.asynk.tankontank.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.badlogic.gdx.math.Vector2;

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
    public Vector2 cell = new Vector2();

    public HexMap(int cols, int rows, Texture texture)
    {
        super(texture);
        this.cols = cols;
        this.rows = rows;
    }

    public void selectCell(float cx, float cy)
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
    }
}
