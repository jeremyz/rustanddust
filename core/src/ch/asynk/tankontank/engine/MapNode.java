package ch.asynk.tankontank.engine;

import java.util.Vector;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;

import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Animation;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public class MapNode extends Image implements Map
{
    private Map.Config cfg;
    private int cols;
    private int rows;
    private Tile[][] board;

    boolean transform;
    private Matrix4 prevTransform;
    private Matrix4 nextTransform;

    private Pawn currentPawn;
    private GridPoint2 currentHex = new GridPoint2(-1, -1);

    private final Vector<Animation> animations = new Vector<Animation>(2);
    private final Vector<Animation> nextAnimations = new Vector<Animation>(2);
    private final LinkedHashSet<Tile> tilesToDraw = new LinkedHashSet<Tile>();
    private final LinkedHashSet<Pawn> pawnsToDraw = new LinkedHashSet<Pawn>();

    public MapNode(Map.Config cfg, Tile[][] board, Texture texture)
    {
        super(texture);
        this.cfg = cfg;
        this.board = board;
        this.cols = cfg.cols - 1;
        this.rows = cfg.rows - 1;
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

    @Override
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
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);

        if (transform) {
            prevTransform.set(batch.getTransformMatrix());
            batch.setTransformMatrix(nextTransform);
        }

        Iterator<Tile> tileIter = tilesToDraw.iterator();
        while (tileIter.hasNext()) {
            tileIter.next().draw(batch, parentAlpha);
        }

        Iterator<Pawn> pawnIter = pawnsToDraw.iterator();
        while (pawnIter.hasNext()) {
            pawnIter.next().draw(batch, parentAlpha);
        }

        if (transform)
            batch.setTransformMatrix(prevTransform);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
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

    @Override
    public Pawn getTopPawnAt(GridPoint2 cell)
    {
        return getTopPawnAt(cell.x, cell.y);
    }

    private Pawn getTopPawnAt(int col, int row)
    {
        return board[row][col].getTopPawn();
    }

    private int pushPawnAt(Pawn pawn, int col, int row)
    {
        Tile tile = board[row][col];
        tilesToDraw.add(tile);
        return tile.push(pawn);
    }

    private int removePawnFrom(Pawn pawn, int col, int row)
    {
        Tile tile = board[row][col];
        int n = tile.remove(pawn);
        if (!tile.mustBeDrawn())
            tilesToDraw.remove(tile);
        return n;
    }

    @Override
    public Vector2 getHexCenterAt(GridPoint2 cell)
    {
        float x = cfg.x0 + ((cell.x * cfg.w) + (cfg.w / 2));
        float y = cfg.y0 + ((cell.y * cfg.h) + (cfg.s / 2));
        if ((cell.y % 2) == 1) x += cfg.dw;
        return new Vector2(x, y);
    }

    @Override
    public Vector2 getPawnPosAt(Pawn pawn, GridPoint2 cell)
    {
        return getPawnPosAt(pawn, cell.x, cell.y);
    }

    private Vector2 getPawnPosAt(Pawn pawn, int col, int row)
    {
        float x = cfg.x0 + ((col * cfg.w) + ((cfg.w - pawn.getHeight()) / 2));
        float y = cfg.y0 + ((row * cfg.h) + ((cfg.s - pawn.getWidth()) / 2));
        if ((row % 2) == 1) x += cfg.dw;
        return new Vector2(x, y);
    }

    @Override
    public void setPawnAt(final Pawn pawn, final int col, final int row, Pawn.Orientation o)
    {
        Vector2 pos = getPawnPosAt(pawn, col, row);
        pawn.pushMove(pos.x, pos.y, o);
        pushPawnAt(pawn, col, row);
    }

    @Override
    public void movePawnTo(Pawn pawn, Vector3 coords)
    {
        GridPoint2 hex = getHexAt(null, coords.x, coords.y);
        movePawnTo(pawn, hex.x, hex.y, Pawn.Orientation.KEEP);
    }

    @Override
    public void movePawnTo(Pawn pawn, GridPoint2 hex)
    {
        movePawnTo(pawn, hex.x, hex.y, Pawn.Orientation.KEEP);
    }

    @Override
    public void movePawnTo(final Pawn pawn, final int col, final int row, Pawn.Orientation o)
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

    @Override
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
        if ((col < 0) || (row < 0) || (row > rows) || (col > cols) || (oddRow && ((col + 1)> cols)))
            hex.set(-1, -1);
        else
            hex.set(col, row);

        return hex;
    }

    @Override
    public boolean drag(float dx, float dy)
    {
        if (currentPawn == null) return false;
        currentPawn.translate(dx, dy);
        return true;
    }

    @Override
    public void touchDown(float x, float y)
    {
        getHexAt(currentHex, x, y);
        if (currentHex.x != -1) {
            currentPawn = getTopPawnAt(currentHex);
        }
    }

    @Override
    public void touchUp(float x, float y)
    {
        getHexAt(currentHex, x, y);
        if (currentPawn != null) {
            movePawnTo(currentPawn, currentHex);
        }
    }
}

