package ch.asynk.tankontank.engine;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public abstract class Board implements Disposable
{
    private final Tile neighbours[] = new Tile[6];

    public interface TileBuilder
    {
        public Tile getNewTile(float x, float y, int col, int row);
    }

    public interface TileCollection extends Collection<Tile>
    {
        public Tile first();
        public void enable(int i, boolean enable);
    }

    public interface PawnCollection extends Collection<Pawn>
    {
        public Pawn first();
        public void enable(int i, boolean enable);
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

    private final Pool<Vector3> vector3Pool = new Pool<Vector3>() {
        @Override
        protected Vector3 newObject() {
            return new Vector3();
        }
    };

    private Config cfg;
    private Tile[] tiles;
    private SearchBoard searchBoard;
    private Image image;
    private Orientation sides[];

    private boolean transform;
    private Matrix4 prevTransform;
    private Matrix4 nextTransform;

    private int tileCount = 0;
    private int pawnCount = 0;
    private int animationCount = 0;
    private final ArrayList<Animation> animations = new ArrayList<Animation>(2);
    private final ArrayList<Animation> nextAnimations = new ArrayList<Animation>(2);
    private final LinkedHashSet<Tile> tilesToDraw = new LinkedHashSet<Tile>();

    protected Board(int cols, int rows)
    {
        searchBoard = new SearchBoard(this, cols, rows);
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
                this.tiles[idx] = tileBuilder.getNewTile(x, y, (j + ((i + 1) / 2)), i);
                idx += 1;
                x += cfg.w;
            }
            y += cfg.h;
            evenRow = !evenRow;
        }

        // TODO parametrized with Config ?
        this.sides = new Orientation[6];
        sides[0] = Orientation.NORTH;
        sides[1] = Orientation.NORTH_EAST;
        sides[2] = Orientation.SOUTH_EAST;
        sides[3] = Orientation.SOUTH;
        sides[4] = Orientation.SOUTH_WEST;
        sides[5] = Orientation.NORTH_WEST;
    }

    @Override
    public void dispose()
    {
        image.dispose();
        for (int i = 0; i < (cfg.cols * cfg.rows); i++)
            tiles[i].dispose();
        tilesToDraw.clear();
        for (int i = 0, n = nextAnimations.size(); i < n; i++)
            nextAnimations.get(i).dispose();
        animations.clear();
        for (int i = 0, n = animations.size(); i < n; i++)
            animations.get(i).dispose();
        animations.clear();
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

    public Orientation getSide(int i)
    {
        return sides[i];
    }

    protected Tile getTile(int col, int row)
    {
        int colOffset = ((row + 1) / 2);
        if ((col < colOffset) || (row < 0) || (row >= cfg.rows) || ((col - colOffset) >= cfg.cols))
            return null;

        return tiles[((col - colOffset)) + (row * cfg.cols)];
    }

    public void setAdjacentTiles(Tile tile, Tile tiles[])
    {
        tiles[0] = getTile((tile.col - 1), (tile.row));
        tiles[1] = getTile((tile.col),     (tile.row + 1));
        tiles[2] = getTile((tile.col + 1), (tile.row + 1));
        tiles[3] = getTile((tile.col + 1), (tile.row));
        tiles[4] = getTile((tile.col),     (tile.row - 1));
        tiles[5] = getTile((tile.col - 1), (tile.row - 1));
    }

    protected void addAnimation(Animation a)
    {
        nextAnimations.add(a);
    }

    private void stats()
    {
        boolean print = false;

        if (tileCount != tilesToDraw.size()) {
            tileCount = tilesToDraw.size();
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
            if (a.animate(delta))
                iter.remove();
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
        while (tileIter.hasNext())
            tileIter.next().draw(batch);

        Iterator<Animation> animationIter = animations.iterator();
        while (animationIter.hasNext())
            animationIter.next().draw(batch);

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
        while (iter.hasNext())
            iter.next().drawDebug(debugShapes);

        Iterator<Animation> animationIter = animations.iterator();
        while (animationIter.hasNext())
            animationIter.next().drawDebug(debugShapes);

        if (transform)
            debugShapes.setTransformMatrix(prevTransform);
    }

    protected int collectPossibleMoves(Pawn pawn, TileCollection moves)
    {
        return searchBoard.possibleMovesFrom(pawn, moves);
    }

    protected int collectPossibleTargets(Pawn pawn, PawnCollection targets)
    {
        return searchBoard.possibleTargetsFrom(pawn, targets);
    }

    protected int collectPossibleTargets(Pawn pawn, Iterator<Pawn> units, PawnCollection targets)
    {
        targets.clear();
        while (units.hasNext()) {
            Pawn target = units.next();
            if (pawn.canAttack(target) && searchBoard.collectAttacks(pawn, target, true))
                targets.add(target);
        }

        return targets.size();
    }

    protected int collectMoveAssists(Pawn pawn, PawnCollection assists)
    {
        assists.clear();
        setAdjacentTiles(pawn.getTile(), neighbours);
        for (int i = 0; i < 6; i++) {
            Tile t = neighbours[i];
            if (t != null) {
                Iterator<Pawn> pawns = t.iterator();
                while(pawns.hasNext()) {
                    Pawn p = pawns.next();
                    if (!pawn.isEnemy(p) && p.canMove())
                        assists.add(p);
                }
            }
        }
        return assists.size();
    }

    protected int collectAttackAssists(Pawn pawn, Pawn target, Iterator<Pawn> units, PawnCollection assists)
    {
        assists.clear();
        while (units.hasNext()) {
            Pawn p = units.next();
            if (p.canAttack(target) && searchBoard.collectAttacks(p, target, !p.canAssistAttackWithoutLos()))
                assists.add(p);
        }

        return assists.size();
    }

    public void enableOverlayOn(Tile tile, int i, boolean enable)
    {
        if(tile.enableOverlay(i, enable))
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    public void enableOverlayOn(Tile tile, int i, Orientation o, boolean enable)
    {
        if(tile.enableOverlay(i, enable, o.r()))
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    private int pushPawnOnto(Pawn pawn, Tile tile)
    {
        tilesToDraw.add(tile);
        return tile.push(pawn);
    }

    public int removePawn(Pawn pawn)
    {
        Tile tile = pawn.getTile();
        int n = tile.remove(pawn);
        if (!tile.mustBeDrawn())
            tilesToDraw.remove(tile);
        return n;
    }

    public Pawn setPawnOnto(Pawn pawn, Tile tile, Orientation o)
    {
        return setPawnOnto(pawn, tile, o.r());
    }

    public Pawn setPawnOnto(Pawn pawn, Tile tile, float r)
    {
        pawn.setOnTile(tile, r);
        pushPawnOnto(pawn, tile);
        return pawn;
    }

    protected void movePawn(final Pawn pawn, PossiblePaths possiblePaths, RunnableAnimation whenDone)
    {
        removePawn(pawn);

        AnimationSequence seq = pawn.getMoveAnimation(possiblePaths.iterator(), possiblePaths.pathSteps(0) + 2);
        seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                // FIXME pawn.getTile() is not ok
                Vector2 center = pawn.getCenter();
                setPawnOnto(pawn, getTileAt(center.x, center.y), pawn.getRotation());
            }
        }));
        seq.addAnimation(whenDone);
        addAnimation(seq);
        pawn.move(possiblePaths.pathCost(0));
    }

    protected void rotatePawn(final Pawn pawn, Orientation o, RunnableAnimation whenDone)
    {
        Vector3 p = pawn.getPosition();
        Vector3 v = vector3Pool.obtain();
        v.set(p.x, p.y, o.r());
        AnimationSequence seq = pawn.getRotateAnimation(v, 1);
        seq.addAnimation(whenDone);
        addAnimation(seq);
        vector3Pool.free(v);
        pawn.rotate(o);
    }

    protected void revertLastPawnMove(final Pawn pawn, RunnableAnimation whenDone)
    {
        removePawn(pawn);

        AnimationSequence seq = pawn.getRevertLastMoveAnimation(2);
        seq.addAnimation(RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                pushPawnOnto(pawn, pawn.getTile());
            }
        }));
        seq.addAnimation(whenDone);
        addAnimation(seq);
        pawn.revertLastMove();
    }

    public Tile getTileAt(float mx, float my)
    {
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
            return null;

        return getTile(col, row);
    }

    public int distance(Tile from, Tile to)
    {
        return distance(from.col, from.row, to.col, to.row);
    }

    public int distance(int col0, int row0, int col1, int row1)
    {
        int dx = Math.abs(col1 - col0);
        int dy = Math.abs(row1 - row0);
        int dz = Math.abs((col0 - row0) - (col1 - row1));

        if (dx > dy) {
            if (dx > dz)
                return dx;
            else
                return dz;
        } else {
            if (dy > dz)
                return dy;
            else
                return dz;
        }
    }
}

