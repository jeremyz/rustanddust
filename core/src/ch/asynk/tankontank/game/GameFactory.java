package ch.asynk.tankontank.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ch.asynk.tankontank.engine.Map;
import ch.asynk.tankontank.engine.MapNode;
import ch.asynk.tankontank.engine.Tile;

public class GameFactory
{
    private static TextureAtlas usAtlas;
    private static TextureAtlas geAtlas;
    private static TextureAtlas hexAtlas;

    public static void init(AssetManager manager)
    {
        usAtlas = manager.get("images/us.atlas", TextureAtlas.class);
        geAtlas = manager.get("images/ge.atlas", TextureAtlas.class);
        hexAtlas = manager.get("images/hex.atlas", TextureAtlas.class);
    }

    public static void dispose()
    {
        usAtlas.dispose();
        geAtlas.dispose();
        hexAtlas.dispose();
    }

    public enum UnitType
    {
        GE_AT_GUN,
        GE_INFANTRY,
        GE_KINGTIGER,
        GE_PANZER_IV,
        GE_PANZER_IV_HQ,
        GE_TIGER,
        GE_WESPE,

        US_AT_GUN,
        US_INFANTRY,
        US_PERSHING,
        US_PERSHING_HQ,
        US_PRIEST,
        US_SHERMAN,
        US_SHERMAN_HQ,
        US_WOLVERINE
    }

    public static Unit getUnit(UnitType t)
    {
        Unit u = null;
        switch(t) {
            case GE_AT_GUN:
                u = new Unit(Army.GE, false, 3, 8, 9, 1, geAtlas.findRegion("at-gun"));
                break;
            case GE_INFANTRY:
                u = new Unit(Army.GE, false, 1, 7, 10, 1, geAtlas.findRegion("infantry"));
                break;
            case GE_KINGTIGER:
                u = new Unit(Army.GE, false, 3, 12, 1, geAtlas.findRegion("kingtiger"));
                break;
            case GE_PANZER_IV:
                u = new Unit(Army.GE, false, 2, 9, 2, geAtlas.findRegion("panzer-iv"));
                break;
            case GE_PANZER_IV_HQ:
                u = new Unit(Army.GE, true, 2, 9, 2, geAtlas.findRegion("panzer-iv-hq"));
                break;
            case GE_TIGER:
                u = new Unit(Army.GE, false, 3, 11, 1, geAtlas.findRegion("tiger"));
                break;
            case GE_WESPE:
                u = new Unit(Army.GE, false, 5, 8, 1, geAtlas.findRegion("wespe"));
                break;
            case US_AT_GUN:
                u = new Unit(Army.US, false, 1, 7, 10, 1, usAtlas.findRegion("at-gun"));
                break;
            case US_INFANTRY:
                u = new Unit(Army.US, false, 1, 7, 10, 1, usAtlas.findRegion("infantry"));
                break;
            case US_PERSHING:
                u = new Unit(Army.US, false, 3, 10, 2, usAtlas.findRegion("pershing"));
                break;
            case US_PERSHING_HQ:
                u = new Unit(Army.US, true, 3, 10, 2, usAtlas.findRegion("pershing-hq"));
                break;
            case US_PRIEST:
                u = new Unit(Army.US, false, 5, 8, 1, usAtlas.findRegion("priest"));
                break;
            case US_SHERMAN:
                u = new Unit(Army.US, false, 2, 9, 2, usAtlas.findRegion("sherman"));
                break;
            case US_SHERMAN_HQ:
                u = new Unit(Army.US, true, 2, 9, 2, usAtlas.findRegion("sherman-hq"));
                break;
            case US_WOLVERINE:
                u = new Unit(Army.US, false, 3, 8, 3, usAtlas.findRegion("wolverine"));
                break;
        }

        return u;
    }

    public enum MapType
    {
        MAP_A,
        MAP_B
    }

    private static Map.Config config()
    {
        Map.Config cfg = new Map.Config();
        cfg.cols = 11;
        cfg.rows = 9;
        cfg.x0 = 83;
        cfg.y0 = 182;
        cfg.w = 189;
        cfg.dw = 94;
        cfg.s = 110;
        cfg.dh = 53.6f;
        cfg.h = cfg.s + cfg.dh;
        cfg.slope = (cfg.dh / (float) cfg.dw);

        return cfg;
    }

    public static Map getMap(AssetManager manager, MapType t)
    {
        Map.Config cfg = config();

        Tile[][] board = new Tile[cfg.rows][];
        boolean evenRow = true;
        for (int i = 0; i < cfg.rows; i++) {
            float y = cfg.y0 + (i * cfg.h) - cfg.dh;
            int c = (evenRow ? cfg.cols : cfg.cols - 1);
            board[i] = new Tile[c];
            for ( int j = 0; j < c; j ++) {
                float x = cfg.x0 + (j * cfg.w) ;//+ (cfg.w / 2f);
                if (!evenRow) x += cfg.dw;
                Hex hex = new Hex(Hex.Terrain.CLEAR, hexAtlas);
                hex.setPosition(x, y, 0);
                board[i][j] = hex;
            }
            evenRow = !evenRow;
        }

        Map m = null;
        switch(t) {
            case MAP_A:
                m = new MapNode(config(), board, manager.get("images/map_a.png", Texture.class));
                break;
            case MAP_B:
                m = new MapNode(config(), board, manager.get("images/map_b.png", Texture.class));
                break;
        }

        return m;
    }
}
