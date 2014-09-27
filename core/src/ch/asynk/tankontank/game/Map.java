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

    public Map(GameFactory gameFactory, Board.Config cfg, Texture texture)
    {
        super(gameFactory, cfg, texture);
        setup();
    }

    protected Hex getHex(int col, int row)
    {
        return (Hex) getTile(col, row);
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

    public void showMoves(float x, float y)
    {
        for(GridPoint2 hex : areaPoints)
            enableOverlayOn(hex.x, hex.y, Hex.GREEN, false);

        getHexAt(currentHex, x, y);
        Pawn pawn = getTopPawnAt(currentHex);
        if (pawn == null) return;
        for(GridPoint2 hex : reachableFrom(pawn, currentHex.x, currentHex.y))
            enableOverlayOn(hex.x, hex.y, Hex.GREEN, true);
        for(GridPoint2 hex : openToAttackFrom(pawn, currentHex.x, currentHex.y))
            enableOverlayOn(hex.x, hex.y, Hex.RED, true);
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
                t = Hex.Terrain.OFFMAP;
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
            } else if (t == Hex.Terrain.OFFMAP) {
                o = Hex.FOG;
                t = Hex.Terrain.CLEAR;
            }
        }

        for (int j = 0; j < cfg.rows; j++) {
            for (int i = 0; i < cfg.cols; i++) {
                Hex hex = getHex(i,j);
                disableOverlaysOn(i, j);
                if (hexOn) {
                    if (roadsOn) {
                        if (hex.roads != 0)
                            enableOverlayOn(i, j, o, true);
                    } else if (hex.terrain == t)
                        enableOverlayOn(i, j, o, true);
                }
            }
        }
    }
}
