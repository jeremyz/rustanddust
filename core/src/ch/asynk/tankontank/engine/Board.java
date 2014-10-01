package ch.asynk.tankontank.engine;

import java.util.Set;
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

public abstract class Board implements Disposable
{
    public interface TileBuilder
    {
        public Tile getNewTile(float cx, float cy);
    }

    public enum Orientation
    {
        ALL(0, 63),
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

        public boolean isInSides(int sides)
        {
            return ((sides & s) == s);
        }

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

        public Orientation opposite()
        {
            return left().left().left();
        }

        public int allBut()
        {
            return ALL.s & (s ^ 0xFFFF);
        }

        public int getFrontSides()
        {
            return s | left().s | right().s;
        }

        public int getBackSides()
        {
            return opposite().getFrontSides();
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
    protected SearchBoard searchBoard;
    private Image image;

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

    protected Board()
    {
    }

    public Board(TileBuilder tileBuilder, Config cfg, Texture texture)
    {
        image = new Image(texture);
        this.cfg = cfg;
        this.tiles = new Tile[cfg.cols * cfg.rows];
        searchBoard = new SearchBoard(this, cfg.cols, cfg.rows);

        int idx = 0;
        boolean evenRow = true;
        float y = cfg.y0 - cfg.dh + cfg.s;
        for (int i = 0; i < cfg.rows; i++) {
            float x = cfg.x0 + cfg.dw;
            if (!evenRow) x += cfg.dw;
            for ( int j = 0; j < cfg.cols; j ++) {
                this.tiles[idx] = tileBuilder.getNewTile(x, y);
                idx += 1;
                x += cfg.w;
            }
            y += cfg.h;
            evenRow = !evenRow;
        }
    }

    @Override
    public void dispose()
    {
        image.dispose();
    }

    public Tile getTile(int col, int row)
    {
        int idx = ((col - ((row + 1) / 2))) + (row * cfg.cols);
        // Gdx.app.debug("Board", " getTile: " + col + " ; " + row + " -> " + idx);
        return tiles[idx];
    }

    public float getWidth()
    {
        return image.getWidth();
    }

    public float getHeight()
    {
        return image.getHeight();
    }

    public void setPosition(float x, float y)
    {
        image.setPosition(x, y);
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

    public void draw(Batch batch)
    {
        image.draw(batch);

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

    private void nodesToPoints(List<SearchBoard.Node> nodes, Vector<GridPoint2> points)
    {
        // for (GridPoint2 point : points)
        //     gridPoint2Pool.free(point);
        // points.clear();

        // for (SearchBoard.Node node : nodes) {
        //     GridPoint2 point = gridPoint2Pool.obtain();
        //     point.set(node.col, node.row);
        //     points.add(point);
        // }

        int ns = nodes.size();
        int ps = points.size();

        if (ps > ns) {
            for (int i = ns; i < ps; i++)
                gridPoint2Pool.free(points.get(i));
        }

        int i = 0;
        for (SearchBoard.Node node : nodes) {
            if (i < ps) {
                points.get(i).set(node.col, node.row);
            } else {
                GridPoint2 point = gridPoint2Pool.obtain();
                point.set(node.col, node.row);
                points.add(point);
            }
            i += 1;
        }
        points.setSize(ns);
    }

    public void possibleMovesFrom(Pawn pawn, int col, int row, Vector<GridPoint2> moves)
    {
        List<SearchBoard.Node> nodes = searchBoard.possibleMovesFrom(pawn, col, row);
        nodesToPoints(nodes, moves);
    }

    public void possibleTargetsFrom(Pawn pawn, int col, int row, Vector<GridPoint2> targets)
    {
        List<SearchBoard.Node> nodes = searchBoard.possibleTargetsFrom(pawn, col, row);
        nodesToPoints(nodes, targets);
    }

    public int nodesToSet(List<Vector<SearchBoard.Node>> nodes, Set<GridPoint2> points)
    {
        // FIXME : optimize this
        for (GridPoint2 point : points)
            gridPoint2Pool.free(point);
        points.clear();

        for (Vector<SearchBoard.Node> path : nodes) {
            for (int i = 0, n = path.size(); i < n; i++) {
                // FIXME : optimize this
                GridPoint2 point = gridPoint2Pool.obtain();
                SearchBoard.Node node = path.get(i);
                point.set(node.col, node.row);
                if (!points.add(point))
                    gridPoint2Pool.free(point);
            }
        }

        return nodes.size();
    }

    public int possiblePaths(Pawn pawn, int col0, int row0, int col1, int row1, Set<GridPoint2> points)
    {
        List<Vector<SearchBoard.Node>> paths = searchBoard.possiblePaths(pawn, col0, row0, col1, row1);
        return nodesToSet(paths, points);
    }

    public int possiblePathsFilterAdd(int col, int row, Set<GridPoint2> points)
    {
        List<Vector<SearchBoard.Node>> paths = searchBoard.possiblePathsFilterAdd(col, row);
        return nodesToSet(paths, points);
    }

    public void disableOverlaysOn(int col, int row)
    {
        disableOverlaysOn(getTile(col, row));
    }

    public void disableOverlaysOn(Tile tile)
    {
        if (tile.disableOverlays())
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

    public Vector2 getTileCenter(int col, int row)
    {
        return getTile(col, row).getCenter();
    }

    public Vector2 getPawnPosAt(Pawn pawn, GridPoint2 tile)
    {
        return getPawnPosAt(pawn, tile.x, tile.y);
    }

    private Vector2 getPawnPosAt(Pawn pawn, int col, int row)
    {
        Vector2 center = getTile(col, row).getCenter();
        float x = (center.x - (pawn.getWidth() / 2));
        float y = (center.y - (pawn.getHeight() / 2));
        return new Vector2(x, y);
    }

    public void setPawnAt(Pawn pawn, int col, int row, Orientation o)
    {
        Vector2 pos = getPawnPosAt(pawn, col, row);
        pawn.pushMove(pos.x, pos.y, o);
        pushPawnAt(pawn, col, row);
    }

    public void movePawnTo(Pawn pawn, GridPoint2 hex)
    {
        movePawnTo(pawn, hex.x, hex.y, Orientation.KEEP);
    }

    public void movePawnTo(Pawn pawn, int col, int row, Orientation o)
    {
        GridPoint2 prev = getHexAt(pawn.getLastPosition());
        removePawnFrom(pawn, prev.x, prev.y);

        pushPawnAt(pawn, col, row);
        Vector2 pos = getPawnPosAt(pawn, col, row);
        pawn.pushMove(pos.x, pos.y, o);
    }

    public void resetPawnMoves(final Pawn pawn)
    {
        GridPoint2 prev = getHexAt(pawn.getLastPosition());
        removePawnFrom(pawn, prev.x, prev.y);

        AnimationSequence seq = pawn.getResetMovesAnimation();
        seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                GridPoint2 hex = getHexAt(pawn.getLastPosition());
                pushPawnAt(pawn, hex.x, hex.y);
            }
        }));
        addPawnAnimation(pawn, seq);
    }

    private GridPoint2 getHexAt(Vector3 v)
    {
        if (v == null) return null;
        return getHexAt(null, v.x, v.y);
    }

    public GridPoint2 getHexAt(GridPoint2 hex, float mx, float my)
    {
        if (hex == null) hex = new GridPoint2();

        // compute row
        float y = (my - cfg.y0);
        int row = (int) (y / cfg.h);
        boolean oddRow = ((row % 2) == 1);
        if (y < 0.f) {
            row = -1;
            oddRow = true;
        }

        // compute col
        float x = (mx - cfg.x0);
        if (oddRow) x -= cfg.dw;
        int col = (int) (x / cfg.w);
        if (x < 0.f)
            col = -1;

        int colOffset = ((row +1) / 2);

        // check upper boundaries
        float dy = (y - (row * cfg.h));
        if (dy > cfg.s) {
            dy -= cfg.s;
            float dx = (x - (col * cfg.w));
            col += colOffset;
            if (dx < cfg.dw) {
                if ((dx * cfg.slope) < dy) {
                    // upper left corner
                    row += 1;
                    colOffset = ((row +1) / 2);
                }
            } else {
                if (((cfg.w - dx) * cfg.slope) < dy) {
                    // upper right corner
                    row += 1;
                    col += 1;
                    colOffset = ((row +1) / 2);
                }
            }
        } else
            col += colOffset;


        // validate hex
        if ((col < colOffset) || (row < 0) || (row >= cfg.rows) || ((col - colOffset) >= cfg.cols))
            hex.set(-1, -1);
        else
            hex.set(col, row);

        // Gdx.app.debug("Board", " hex: " + hex.x + " ; " + hex.y);
        return hex;
    }
}

