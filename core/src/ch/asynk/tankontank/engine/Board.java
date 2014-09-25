package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public abstract class Board extends Image implements Disposable
{
    public enum Orientation
    {
        KEEP(0, 0),
        NORTH(270, 1),
        NORTH_EAST(210, 2),
        SOUTH_EAST(150, 4),
        SOUTH(90, 8),
        SOUTH_WEST (30, 16),
        NORTH_WEST(330, 32);

        public static int offset = 0;
        public static float delta = 5f;
        private final int r;
        public final int s;

        Orientation(int r, int s) { this.r = r; this.s = s; }

        public float r() { return offset + r; }

        public Orientation left()
        {
            if (this == NORTH) return NORTH_WEST;
            else return fromSide(s >> 1);
        }

        public Orientation right()
        {
            if (this == NORTH_WEST) return NORTH;
            else return fromSide(s << 1);
        }

        public Orientation oppositeSide()
        {
            return left().left().left();
        }

        public int getFrontSides()
        {
            return s | left().s | right().s;
        }

        public int getBackSides()
        {
            return oppositeSide().getFrontSides();
        }

        public static Orientation fromSide(int s)
        {
            if (s == 1) return NORTH;
            else if (s == NORTH_EAST.s) return NORTH_EAST;
            else if (s == SOUTH_EAST.s) return SOUTH_EAST;
            else if (s == SOUTH.s) return SOUTH;
            else if (s == SOUTH_WEST.s) return SOUTH_WEST;
            else if (s == NORTH_WEST.s) return NORTH_WEST;
            else return KEEP;
        }

        public static Orientation fromRotation(float r)
        {
            if (r < 0) r += 360f;
            if ((r > (NORTH.r - 5f)) && (r < (NORTH.r + 5f))) return NORTH;
            else if ((r > (NORTH_EAST.r - delta)) && (r < (NORTH_EAST.r + delta))) return NORTH_EAST;
            else if ((r > (SOUTH_EAST.r - delta)) && (r < (SOUTH_EAST.r + delta))) return SOUTH_EAST;
            else if ((r > (SOUTH.r - delta)) && (r < (SOUTH.r + delta))) return SOUTH;
            else if ((r > (SOUTH_WEST.r - delta)) && (r < (SOUTH_WEST.r + delta))) return SOUTH_WEST;
            else if ((r > (NORTH_WEST.r - delta)) && (r < (NORTH_WEST.r + delta))) return NORTH_WEST;
            else return KEEP;
        }
    }

    public static class Config
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

    private final Pool<GridPoint2> gridPoint2Pool = new Pool<GridPoint2>() {
        @Override
        protected GridPoint2 newObject() {
            return new GridPoint2();
        }
    };

    protected Config cfg;
    private Tile[] tiles;
    private SearchBoard searchBoard;

    boolean transform;
    private Matrix4 prevTransform;
    private Matrix4 nextTransform;

    private int tileCount = 0;
    private int pawnCount = 0;
    private int animationCount = 0;
    private final Vector<Animation> animations = new Vector<Animation>(2);
    private final Vector<Animation> nextAnimations = new Vector<Animation>(2);
    private final LinkedHashSet<Tile> tilesToDraw = new LinkedHashSet<Tile>();
    protected final LinkedHashSet<Pawn> pawnsToDraw = new LinkedHashSet<Pawn>();

    protected final List<GridPoint2> areaPoints = new Vector<GridPoint2>(10);

    public Board(Config cfg, Texture texture, Tile tileBuilder)
    {
        super(texture);
        this.cfg = cfg;
        this.tiles = new Tile[cfg.cols * cfg.rows];
        searchBoard = new SearchBoard(this, cfg.cols, cfg.rows);

        boolean evenRow = true;
        int idx = 0;
        for (int i = 0; i < cfg.rows; i++) {
            float y = cfg.y0 + (i * cfg.h) - cfg.dh;
            for ( int j = 0; j < cfg.cols; j ++) {
                float x = cfg.x0 + (j * cfg.w);
                if (!evenRow) x += cfg.dw;
                this.tiles[idx] = tileBuilder.getNewAt(x, y);
                idx += 1;
            }
            evenRow = !evenRow;
        }
    }

    public Tile getTile(int col, int row)
    {
        return tiles[col + (row * cfg.cols)];
    }

    public int distance(int col0, int row0, int col1, int row1)
    {
        int a = (row1 - row0);
        // transform into a system where all tiles in the same row have the same value of X
        // and all tiles in the same column have the same value of Y non-staggering coordinates
        int b = ((col1 + ((row1 + 1) / 2)) - (col0 + ((row0 + 1) / 2)));
        int c = (b - a);
        int aa = Math.abs(a);
        int ab = Math.abs(b);
        int ac = Math.abs(c);
        if (ac > aa) {
            if (ac > ab)
                return ac;
            else
                return ab;
        } else {
            if (aa > ab)
                return aa;
            else
                return ab;
        }
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        if ((x != 0.0f) || (y != 0.0f)) {
            transform = true;
            prevTransform = new Matrix4();
            nextTransform = new Matrix4();
            nextTransform.translate(x, y, 0);
        } else
            transform = false;
    }

    private void addPawnAnimation(Pawn pawn, AnimationSequence seq)
    {
        pawnsToDraw.add(pawn);
        nextAnimations.add(seq);
    }

    private void stats()
    {
        boolean print = false;

        if (tileCount != tilesToDraw.size()) {
            tileCount = tilesToDraw.size();
            print = true;
        }

        if (pawnCount != pawnsToDraw.size()) {
            pawnCount = pawnsToDraw.size();
            print = true;
        }

        if (animationCount != animations.size()) {
            animationCount = animations.size();
            print = true;
        }

        if (print)
            Gdx.app.debug("Board", " tiles:" + tileCount + " pawns:" + pawnCount + " animations:" + animationCount);
    }

    public void animate(float delta)
    {

        Iterator<Animation> iter = animations.iterator();
        while (iter.hasNext()) {
            Animation a = iter.next();
            Pawn p = a.getPawn();
            if (a.animate(delta)) {
                iter.remove();
                pawnsToDraw.remove(p);
            }
        }

        for (int i = 0, n = nextAnimations.size(); i < n; i++)
            animations.add(nextAnimations.get(i));
        nextAnimations.clear();
    }

    @Override
    public void draw(Batch batch)
    {
        super.draw(batch);

        if (transform) {
            prevTransform.set(batch.getTransformMatrix());
            batch.setTransformMatrix(nextTransform);
        }

        Iterator<Tile> tileIter = tilesToDraw.iterator();
        while (tileIter.hasNext()) {
            tileIter.next().draw(batch);
        }

        Iterator<Pawn> pawnIter = pawnsToDraw.iterator();
        while (pawnIter.hasNext()) {
            pawnIter.next().draw(batch);
        }

        if (transform)
            batch.setTransformMatrix(prevTransform);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        stats();
        if (transform) {
            prevTransform.set(debugShapes.getTransformMatrix());
            debugShapes.setTransformMatrix(nextTransform);
        }

        Iterator<Tile> iter = tilesToDraw.iterator();
        while (iter.hasNext()) {
            iter.next().drawDebug(debugShapes);
        }

        if (transform)
            debugShapes.setTransformMatrix(prevTransform);
    }

    public List<GridPoint2> reachableFrom(Pawn pawn, int col, int row)
    {
        for (GridPoint2 p : areaPoints)
            gridPoint2Pool.free(p);
        areaPoints.clear();

        for (SearchBoard.Node node : searchBoard.reachableFrom(pawn, col, row)) {
            GridPoint2 point = gridPoint2Pool.obtain();
            point.set(node.col, node.row);
            areaPoints.add(point);
        }

        return areaPoints;
    }

    public void clearOverlaysOn(int col, int row)
    {
        clearOverlaysOn(getTile(col, row));
    }

    public void clearOverlaysOn(Tile tile)
    {
        if (tile.clearOverlays())
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    public void enableOverlayOn(int col, int row, int i, boolean enable)
    {
        enableOverlayOn(getTile(col, row), i, enable);
    }

    public void enableOverlayOn(Tile tile, int i, boolean enable)
    {
        if(tile.enableOverlay(i, enable))
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    public Pawn removeTopPawnFrom(GridPoint2 tile)
    {
        return removeTopPawnFrom(tile.x, tile.y);
    }

    public Pawn removeTopPawnFrom(int col, int row)
    {
        Pawn pawn = getTopPawnAt(col, row);
        if (pawn != null)
            removePawnFrom(pawn, col, row);
        return pawn;
    }

    public Pawn getTopPawnAt(GridPoint2 tile)
    {
        return getTopPawnAt(tile.x, tile.y);
    }

    private Pawn getTopPawnAt(int col, int row)
    {
        return getTile(col, row).getTopPawn();
    }

    private int pushPawnAt(Pawn pawn, int col, int row)
    {
        Tile tile = getTile(col, row);
        tilesToDraw.add(tile);
        return tile.push(pawn);
    }

    private int removePawnFrom(Pawn pawn, int col, int row)
    {
        Tile tile = getTile(col, row);
        int n = tile.remove(pawn);
        if (!tile.mustBeDrawn())
            tilesToDraw.remove(tile);
        return n;
    }

    public Vector2 getHexCenterAt(GridPoint2 tile)
    {
        // FIXME could ask it to the Tile
        float x = cfg.x0 + ((tile.x * cfg.w) + (cfg.w / 2));
        float y = cfg.y0 + ((tile.y * cfg.h) + (cfg.s / 2));
        if ((tile.y % 2) == 1) x += cfg.dw;
        return new Vector2(x, y);
    }

    public Vector2 getPawnPosAt(Pawn pawn, GridPoint2 tile)
    {
        return getPawnPosAt(pawn, tile.x, tile.y);
    }

    private Vector2 getPawnPosAt(Pawn pawn, int col, int row)
    {
        // FIXME could ask it to the Tile
        float x = cfg.x0 + ((col * cfg.w) + ((cfg.w - pawn.getHeight()) / 2));
        float y = cfg.y0 + ((row * cfg.h) + ((cfg.s - pawn.getWidth()) / 2));
        if ((row % 2) == 1) x += cfg.dw;
        return new Vector2(x, y);
    }

    public void setPawnAt(final Pawn pawn, final int col, final int row, Orientation o)
    {
        Vector2 pos = getPawnPosAt(pawn, col, row);
        pawn.pushMove(pos.x, pos.y, o);
        pushPawnAt(pawn, col, row);
    }

    public void movePawnTo(Pawn pawn, GridPoint2 hex)
    {
        movePawnTo(pawn, hex.x, hex.y, Orientation.KEEP);
    }

    public void movePawnTo(final Pawn pawn, final int col, final int row, Orientation o)
    {
        GridPoint2 prev = getHexAt(pawn.getLastPosition());
        removePawnFrom(pawn, prev.x, prev.y);

        if ((col < 0) || (row < 0)) {
            AnimationSequence seq = pawn.getResetMovesAnimation();
            seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
                @Override
                public void run() {
                    GridPoint2 hex = getHexAt(pawn.getLastPosition());
                    pushPawnAt(pawn, hex.x, hex.y);
                }
            }));
            addPawnAnimation(pawn, seq);
        } else {
            pushPawnAt(pawn, col, row);
            Vector2 pos = getPawnPosAt(pawn, col, row);
            pawn.pushMove(pos.x, pos.y, o);
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
            row = (int) (y / cfg.h);
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
        float dy = (y - (row * cfg.h));
        if (dy > cfg.s) {
            dy -= cfg.s;
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
        if ((col < 0) || (row < 0) || (row >= cfg.rows) || (col >= cfg.cols) || (oddRow && ((col + 1) >= cfg.cols)))
            hex.set(-1, -1);
        else
            hex.set(col, row);

        return hex;
    }
}

