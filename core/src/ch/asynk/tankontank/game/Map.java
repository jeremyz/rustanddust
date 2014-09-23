package ch.asynk.tankontank.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Pawn;

public abstract class Map extends Board
{
    private boolean roadsOn = false;
    private boolean hexOn = false;
    private Hex.Terrain t = Hex.Terrain.CLEAR;

    private Pawn currentPawn;
    private GridPoint2 currentHex = new GridPoint2(-1, -1);

    protected abstract void setup();

    public Map(Board.Config cfg, Texture texture, TextureAtlas hexAtlas)
    {
        super(cfg, texture, new Hex(hexAtlas));
        setup();
    }

    protected Hex getHex(int col, int row)
    {
        return (Hex) board[row][col];
    }

    public boolean drag(float dx, float dy)
    {
        if (currentPawn == null) return false;
        currentPawn.translate(dx, dy);
        return true;
    }

    public void touchDown(float x, float y)
    {
        getHexAt(currentHex, x, y);
        if (currentHex.x != -1) {
            currentPawn = removeTopPawnFrom(currentHex);
            if (currentPawn != null) pawnsToDraw.add(currentPawn);
        }
    }

    public void touchUp(float x, float y)
    {
        getHexAt(currentHex, x, y);
        if (currentPawn != null) {
            pawnsToDraw.remove(currentPawn);
            movePawnTo(currentPawn, currentHex);
            currentPawn = null;
        } else {
            debugMap();
        }
    }

    private void debugMap()
    {
        int o = Hex.FOG;
        if (hexOn && (t == Hex.Terrain.CLEAR)) {
            hexOn = false;
        } else {
            hexOn = true;
            if (roadsOn) {
                roadsOn = false;
                t = Hex.Terrain.CLEAR;
            } else if (t == Hex.Terrain.CLEAR) {
                o = Hex.GREEN;
                t = Hex.Terrain.WOODS;
            } else if (t == Hex.Terrain.WOODS) {
                o = Hex.BLUE;
                t = Hex.Terrain.HILLS;
            } else if (t == Hex.Terrain.HILLS) {
                o = Hex.RED;
                t = Hex.Terrain.TOWN;
            } else if (t == Hex.Terrain.TOWN) {
                o = Hex.FOG;
                roadsOn = true;
            }
        }

        boolean evenRow = true;
        for (int j = 0; j < cfg.rows; j++) {
            int c = (evenRow ? cfg.cols : cfg.cols - 1);
            for (int i = 0; i < c; i++) {
                Hex hex = getHex(i,j);
                clearOverlaysOn(i, j);
                if (hexOn) {
                    if (roadsOn) {
                        if (hex.roads != 0)
                            enableOverlayOn(i, j, o, true);
                    } else if (hex.terrain == t)
                        enableOverlayOn(i, j, o, true);
                }
            }
            evenRow = !evenRow;
        }
    }
}
