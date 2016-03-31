package ch.asynk.rustanddust.engine;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Matrix4;

import ch.asynk.rustanddust.engine.util.IterableArray;
import ch.asynk.rustanddust.engine.util.IterableSet;
import ch.asynk.rustanddust.engine.util.Collection;
import ch.asynk.rustanddust.engine.gfx.Moveable;
import ch.asynk.rustanddust.engine.gfx.Animation;
import ch.asynk.rustanddust.engine.gfx.animations.AnimationSequence;
import ch.asynk.rustanddust.engine.gfx.animations.RunnableAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.MoveToAnimation.MoveToAnimationCb;

public abstract class Board implements Disposable, Animation
{
    private int cols;
    private int rows;
    private final Tile neighbours[] = new Tile[6];

    public interface TileBuilder
    {
        public Tile getNewTile(float x, float y, int col, int row, boolean offmap);
    }

    public static class Config
    {
        public int cols;
        public int rows;
        public int x0;          // bottom left x offset
        public int y0;          // bottom left y offset
        public float w;         // hex width
        public float dw;        // half hex : w/2
        public float s;         // hex side
        public float dh;        // hex top : s/2
        public float h;         // square height : s + dh
        public float slope;     // north-west side slope : (dh / (float) dw)
    }

    private Config cfg;
    private final Tile[] tiles;
    private final SearchBoard searchBoard;
    private final Sprite board;
    private final Orientation sides[];

    private boolean transform;
    private Matrix4 prevTransform;
    private Matrix4 nextTransform;

    private int tileCount = 0;
    private int pawnCount = 0;
    private int animationCount = 0;
    private final IterableArray<Animation> animations = new IterableArray<Animation>(10);
    private final IterableArray<Animation> nextAnimations = new IterableArray<Animation>(10);
    private final IterableSet<Tile> tilesToDraw = new IterableSet<Tile>(20);

    protected SelectedTile selectedTile;

    abstract protected Config getConfig();

    protected Board(int cols, int rows)
    {
        // add a frame of OFFMAP Tiles
        this.cols = (cols + 2);
        this.rows = (rows + 2);
        this.searchBoard = new SearchBoard(this, cols, rows);
        this.sides = new Orientation[6];
        initSides();
        this.tiles = null;
        this.board = null;
    }

    public Board(TileBuilder tileBuilder, Texture boardTexture,  SelectedTile selectedTile)
    {
        this.cfg = getConfig();
        // add a frame of OFFMAP Tiles
        this.cols = (cfg.cols + 2);
        this.rows = (cfg.rows + 2);
        this.sides = new Orientation[6];
        this.board = new Sprite(boardTexture);
        this.tiles = new Tile[this.cols * this.rows];
        this.searchBoard = new SearchBoard(this, cfg.cols, cfg.rows);

        int idx = 0;
        boolean evenRow = false;
        float y = cfg.y0 - cfg.dh + cfg.s - cfg.h;
        for (int i = -1; i < (cfg.rows + 1); i++) {
            float x = cfg.x0 + cfg.dw - cfg.w;
            if (!evenRow) x += cfg.dw;
            for ( int j = -1; j < (cfg.cols + 1); j ++) {
                boolean offmap = ((j < 0) || (i < 0) || (j >= cfg.cols) || (i >= cfg.rows));
                this.tiles[idx] = tileBuilder.getNewTile(x, y, (j + ((i + 1) / 2)), i, offmap);
                idx += 1;
                x += cfg.w;
            }
            y += cfg.h;
            evenRow = !evenRow;
        }

        initSides();

        this.selectedTile = selectedTile;
    }

    private final void initSides()
    {
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
        for (int i = 0; i < (this.cols * this.rows); i++)
            tiles[i].dispose();
        tilesToDraw.clear();
        for (Animation a : nextAnimations)
            a.dispose();
        animations.clear();
        for (Animation a : animations)
            a.dispose();
        animations.clear();
        if (selectedTile != null)
            selectedTile.dispose();
        Move.clearPool();
        Path.clearPool();
    }

    public float getWidth()
    {
        return board.getWidth();
    }

    public float getHeight()
    {
        return board.getHeight();
    }

    public void setPosition(float x, float y)
    {
        board.setPosition(x, y);
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

    protected int getTileOffset(int col, int row)
    {
        col = (col + 1 - ((row + 1) / 2));
        row = (row + 1);
        if ((col < 0) || (row < 0) || (row >= this.rows) || (col >= this.cols))
            return -1;

        return (col + (row * this.cols));
    }

    protected Tile getTile(int col, int row)
    {
        int offset = getTileOffset(col, row);
        if (offset < 0)
            return null;
        return tiles[offset];
    }

    public void setAdjacentTiles(Tile tile, Tile tiles[])
    {
        tiles[0] = getAdjTileAt(tile, sides[0].opposite());
        tiles[1] = getAdjTileAt(tile, sides[1].opposite());
        tiles[2] = getAdjTileAt(tile, sides[2].opposite());
        tiles[3] = getAdjTileAt(tile, sides[3].opposite());
        tiles[4] = getAdjTileAt(tile, sides[4].opposite());
        tiles[5] = getAdjTileAt(tile, sides[5].opposite());
    }

    public Tile getAdjTileAt(Tile tile, Orientation o)
    {
        Tile t = null;
        switch(o) {
            case NORTH:
                t = getTile((tile.col + 1), tile.row);
                break;
            case NORTH_EAST:
                t = getTile(tile.col, (tile.row - 1));
                break;
            case SOUTH_EAST:
                t = getTile((tile.col - 1), (tile.row - 1));
                break;
            case SOUTH:
                t = getTile((tile.col - 1), tile.row);
                break;
            case SOUTH_WEST:
                t = getTile(tile.col, (tile.row + 1));
                break;
            case NORTH_WEST:
                t = getTile((tile.col + 1), (tile.row + 1));
                break;
        }
        return t;
    }

    protected abstract void animationsDone();

    protected void addAnimation(Animation a)
    {
        nextAnimations.add(a);
    }

    public int animationCount()
    {
        return animations.size();
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

    public boolean animate(float delta)
    {
        boolean over = (animations.size() > 0);
        Iterator<Animation> iter = animations.iterator();
        while (iter.hasNext()) {
            Animation a = iter.next();
            if (a.animate(delta)) {
                iter.remove();
                a.dispose();
            }
        }
        if (over && (animations.size() == 0))
            animationsDone();

        for (Animation a : nextAnimations)
            animations.add(a);
        nextAnimations.clear();

        selectedTile.animate(delta);

        return true;
    }

    public void draw(Batch batch)
    {
        board.draw(batch);

        if (transform) {
            prevTransform.set(batch.getTransformMatrix());
            batch.setTransformMatrix(nextTransform);
        }

        for (Tile tile : tilesToDraw)
            tile.draw(batch);

        for (Animation a : animations)
            a.draw(batch);

        selectedTile.draw(batch);

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

        for (Tile tile : tilesToDraw)
            tile.drawDebug(debugShapes);

        for (Animation a : animations)
            a.drawDebug(debugShapes);

        if (transform)
            debugShapes.setTransformMatrix(prevTransform);
    }

    protected int collectPossibleMoves(Pawn pawn, Collection<Tile> moves)
    {
        return searchBoard.possibleMovesFrom(pawn, moves);
    }

    protected int collectPossibleTargets(Pawn pawn, Collection<Pawn> targets)
    {
        return searchBoard.possibleTargetsFrom(pawn, targets);
    }

    protected int collectPossibleTargets(Pawn pawn, Collection<Pawn> units, Collection<Pawn> targets)
    {
        targets.clear();
        for (Pawn target : units) {
            if (pawn.canEngage(target) && searchBoard.canAttack(pawn, target, true))
                targets.add(target);
        }

        return targets.size();
    }

    protected int collectMoveAssists(Pawn pawn, Collection<Pawn> assists)
    {
        assists.clear();
        setAdjacentTiles(pawn.getTile(), neighbours);
        for (int i = 0; i < 6; i++) {
            Tile tile = neighbours[i];
            if (tile != null) {
                for (Pawn p : tile) {
                    if (!pawn.isEnemy(p) && p.canMove())
                        assists.add(p);
                }
            }
        }
        return assists.size();
    }

    protected int collectAttackAssists(Pawn pawn, Pawn target, Collection<Pawn> units, Collection<Pawn> assists)
    {
        assists.clear();
        for (Pawn p : units) {
            if ((p != pawn) && p.canEngage(target) && searchBoard.canAttack(p, target, !p.canAssistEngagementWithoutLos()))
                assists.add(p);
        }

        return assists.size();
    }

    public Orientation findBestEntry(Pawn pawn, Tile to, int allowedMoves)
    {
        Orientation entry = Orientation.KEEP;
        int cost = Integer.MAX_VALUE;
        boolean road = false;

        setAdjacentTiles(to, neighbours);
        for (int i = 0; i < 6; i++) {
            Tile t = neighbours[i];
            if (t.isOffMap()) {
                Orientation o = Orientation.fromAdj(t.col, t.row, to.col, to.row);
                if (o.isInSides(allowedMoves)) {
                    o = o.opposite();
                    boolean r = to.road(o);
                    int c = to.costFrom(pawn, o);
                    if ((c < cost) || (r && (c == cost))) {
                        entry = o;
                        cost = c;
                        road = r;
                    }
                }
            }
        }

        return entry.opposite();
    }

    public int objectivesCount(Faction faction)
    {
        int n = 0;
        for (Tile tile : tiles) {
            if (tile.isOwnedObjective(faction))
                n += 1;
        }
        return n;
    }

    public void claim(Moveable moveable, Tile tile)
    {
        int o = tile.belongsTo().overlay();
        if (tile.claim(moveable.getFaction())) {
            if (tile.isObjective()) {
                tile.enableOverlay(o, false);
                tile.enableOverlay(moveable.getFaction().overlay(), true);
                tilesToDraw.add(tile);
            }
        }
    }

    public void unclaim(Moveable moveable, Tile tile)
    {
        if (tile.unclaim()) {
            if (tile.isObjective()) {
                tile.enableOverlay(moveable.getFaction().overlay(), false);
                tile.enableOverlay(tile.belongsTo().overlay(), true);
                tilesToDraw.add(tile);
            }
        }
    }

    public void revertclaim(Pawn pawn, Tile tile)
    {
        int o = pawn.getTile().revertClaim().overlay();
        tile.enableOverlay(pawn.getFaction().overlay(), false);
        enableOverlayOn(tile ,o, true);
    }

    public void enableOverlayOn(Tile tile, int i, boolean enable)
    {
        if (tile.enableOverlay(i, enable))
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    public void enableOverlayOn(Tile tile, int i, Orientation o, boolean enable)
    {
        if (tile.enableOverlay(i, enable, o.r()))
            tilesToDraw.add(tile);
        else
            tilesToDraw.remove(tile);
    }

    private int pushPawnOnto(Pawn pawn, Tile tile)
    {
        if (!tile.isOffMap())
            tilesToDraw.add(tile);
        return tile.push(pawn);
    }

    public int removePawn(Pawn pawn)
    {
        Tile tile = pawn.getTile();
        if (tile == null)
            return 0;
        int n = tile.remove(pawn);
        if (!tile.mustBeDrawn())
            tilesToDraw.remove(tile);
        return n;
    }

    public Pawn setPawnOnto(Pawn pawn, Move move)
    {
        pawn.move(move);
        return setPawnOnto(pawn, move.to, move.orientation);
    }

    public Pawn setPawnOnto(Pawn pawn, Tile tile, Orientation o)
    {
        pawn.setOnTile(tile, o.r());
        pushPawnOnto(pawn, tile);
        return pawn;
    }

    private RunnableAnimation getSetPawnOntoAnimation(final Pawn pawn)
    {
        return RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                Tile to = pawn.move.to;
                if (!to.isOffMap())
                    setPawnOnto(pawn, to, pawn.move.orientation);
            }
        });
    }

    protected void movePawn(final Pawn pawn, Move move, MoveToAnimationCb cb)
    {
        pawn.move(move);
        removePawn(pawn);

        AnimationSequence seq = pawn.getMoveAnimation(move.iterator(), (move.steps() + 1), cb);
        seq.addAnimation(getSetPawnOntoAnimation(pawn));
        addAnimation(seq);
    }

    protected void enterPawn(final Pawn pawn, Move move)
    {
        pawn.move(move);
        setPawnOnto(pawn, move.to, move.orientation);
    }

    protected void revertLastPawnMove(final Pawn pawn, final Move move)
    {
        removePawn(pawn);

        revertclaim(pawn, move.to);
        for (Tile tile : move.tiles)
            revertclaim(pawn, tile);
        claim(pawn, move.from);

        addAnimation(RunnableAnimation.get(pawn, new Runnable() {
            @Override
            public void run() {
                pushPawnOnto(pawn, pawn.getTile());
            }
        }));

        pawn.revertLastMove();
    }

    public void attack(final Pawn pawn, final Pawn target, boolean clearVisibility)
    {
        if (!pawn.canEngage(target) || !searchBoard.canAttack(pawn, target, clearVisibility))
            throw new RuntimeException(String.format("%s cannot attack %s", pawn, target));
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

        int colOffset = ((row + 1) / 2);

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

