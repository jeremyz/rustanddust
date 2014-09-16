package ch.asynk.tankontank.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class HexMapFactory
{
    public enum MapType
    {
        MAP_A,
        MAP_B
    }

    private static HexMap.Config config()
    {
        HexMap.Config cfg = new HexMap.Config();
        cfg.cols = 11;
        cfg.rows = 9;
        cfg.x0 = 83;
        cfg.y0 = 182;
        cfg.h = 110;
        cfg.dh = 53.6f;
        cfg.w = 189;
        cfg.dw = 94;
        cfg.H = cfg.h + cfg.dh;
        cfg.slope = (cfg.dh / (float) cfg.dw);

        return cfg;
    }

    public static HexMap getMap(AssetManager manager, MapType t)
    {
        HexMap.Config cfg = config();

        Hex[][] board = new Hex[cfg.rows][];
        for (int i = 0; i < cfg.rows; i++) {
            int c = cfg.cols;
            if ((i % 2) == 1) c -= 1;
            board[i] = new Hex[c];
            for ( int j = 0; j < c; j ++)
                board[i][j] = new MapHex(MapHex.Terrain.CLEAR);
        }

        HexMap m = null;
        switch(t) {
            case MAP_A:
                m = new HexMapImage(config(), board, manager.get("images/map_a.png", Texture.class));
                break;
            case MAP_B:
                m = new HexMapImage(config(), board, manager.get("images/map_b.png", Texture.class));
                break;
        }

        return m;
    }
}
