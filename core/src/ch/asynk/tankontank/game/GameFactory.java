package ch.asynk.tankontank.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ch.asynk.tankontank.engine.Board;

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

    private static Board.Config config()
    {
        Board.Config cfg = new Board.Config();
        cfg.cols = 10;
        cfg.rows = 9;
        cfg.x0 = 272;
        cfg.y0 = 182;
        cfg.w = 189;
        cfg.dw = 94;
        cfg.s = 110;
        cfg.dh = 53.6f;
        cfg.h = cfg.s + cfg.dh;
        cfg.slope = (cfg.dh / (float) cfg.dw);

        return cfg;
    }

    public static Hex[][] createEmptyBoard(Board.Config cfg)
    {
        Hex[][] board = new Hex[cfg.rows][];
        boolean evenRow = true;
        for (int i = 0; i < cfg.rows; i++) {
            float y = cfg.y0 + (i * cfg.h) - cfg.dh;
            int c = (evenRow ? cfg.cols : cfg.cols - 1);
            board[i] = new Hex[c];
            for ( int j = 0; j < c; j ++) {
                float x = cfg.x0 + (j * cfg.w);
                if (!evenRow) x += cfg.dw;
                Hex hex = new Hex(Hex.Terrain.CLEAR, hexAtlas);
                hex.setPosition(x, y, 0);
                board[i][j] = hex;
            }
            evenRow = !evenRow;
        }
        return board;
    }

    public static Map getMap(AssetManager manager, MapType t)
    {
        Board.Config cfg = config();

        Hex[][] board = createEmptyBoard(cfg);

        Map m = null;
        switch(t) {
            case MAP_A:
                m = new Map(config(), board, manager.get("images/map_a.png", Texture.class));
                break;
            case MAP_B:
                m = new Map(config(), board, manager.get("images/map_b.png", Texture.class));
                break;
        }

        return m;
    }

    public static void feedMapA(Hex[][] board)
    {
        // board[ row ][ col ]
        board[1][4].terrain = Hex.Terrain.HILLS;
        board[3][5].terrain = Hex.Terrain.HILLS;
        board[8][3].terrain = Hex.Terrain.HILLS;
        board[8][4].terrain = Hex.Terrain.HILLS;

        board[0][5].terrain = Hex.Terrain.WOODS;
        board[0][6].terrain = Hex.Terrain.WOODS;
        board[3][1].terrain = Hex.Terrain.WOODS;
        board[3][2].terrain = Hex.Terrain.WOODS;
        board[7][6].terrain = Hex.Terrain.WOODS;
        board[7][7].terrain = Hex.Terrain.WOODS;
        board[8][7].terrain = Hex.Terrain.WOODS;

        board[1][5].terrain = Hex.Terrain.TOWN;
        board[2][1].terrain = Hex.Terrain.TOWN;
        board[4][4].terrain = Hex.Terrain.TOWN;
        board[5][7].terrain = Hex.Terrain.TOWN;
        board[6][1].terrain = Hex.Terrain.TOWN;
        board[7][3].terrain = Hex.Terrain.TOWN;

        int N = Map.Orientation.NORTH.s;
        int S = Map.Orientation.SOUTH.s;
        int NE = Map.Orientation.NORTH_EAST.s;
        int NW = Map.Orientation.NORTH_WEST.s;
        int SE = Map.Orientation.SOUTH_EAST.s;
        int SW = Map.Orientation.SOUTH_WEST.s;

        board[1][5].roads = (NW | SW);
        for (int i = 0; i < 10; i++) {
            if (i == 5)
                board[2][i].roads = (NE | S | SW);
            else if (i == 6)
                board[2][i].roads = (N | SE);
            else
                board[2][i].roads = (N | S);
        }
        board[3][4].roads = (NE | SW);
        board[4][4].roads = (N | NE | SW);
        board[4][5].roads = (N | S);
        board[4][6].roads = (NW | S);
        board[5][3].roads = (NE | SW);
        board[5][5].roads = (N | SW);
        board[5][6].roads = (N | S | NE);
        board[5][7].roads = (N | S);
        board[5][8].roads = (N | S);
        board[6][0].roads = (N | S);
        board[6][1].roads = (N | S);
        board[6][2].roads = (N | S);
        board[6][3].roads = (NE | NW | S);
        board[6][5].roads = (NE | SW);
        board[7][3].roads = (N | SE);
        board[7][4].roads = (NE | S);
    }
}
