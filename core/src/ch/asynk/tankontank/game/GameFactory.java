package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ch.asynk.tankontank.engine.Board;

public class GameFactory implements Board.TileBuilder, Disposable
{
    private TextureAtlas usAtlas;
    private TextureAtlas geAtlas;
    private TextureAtlas hexAtlas;
    private TextureAtlas unitAtlas;

    public GameFactory(AssetManager manager)
    {
        usAtlas = manager.get("images/us.atlas", TextureAtlas.class);
        geAtlas = manager.get("images/ge.atlas", TextureAtlas.class);
        hexAtlas = manager.get("images/hex.atlas", TextureAtlas.class);
        unitAtlas = manager.get("images/unit.atlas", TextureAtlas.class);
    }

    @Override
    public void dispose()
    {
        usAtlas.dispose();
        geAtlas.dispose();
        hexAtlas.dispose();
        unitAtlas.dispose();
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

    public Unit getUnit(UnitType t)
    {
        Unit u = null;
        switch(t) {
            case GE_AT_GUN:
                u = new Unit(Army.GE, false, 3, 8, 9, 1, geAtlas.findRegion("at-gun"), geAtlas.findRegion("head"), unitAtlas);
                break;
            case GE_INFANTRY:
                u = new Unit(Army.GE, false, 1, 7, 10, 1, geAtlas.findRegion("infantry"), geAtlas.findRegion("head"), unitAtlas);
                break;
            case GE_KINGTIGER:
                u = new Unit(Army.GE, false, 3, 12, 1, geAtlas.findRegion("kingtiger"), geAtlas.findRegion("head"), unitAtlas);
                break;
            case GE_PANZER_IV:
                u = new Unit(Army.GE, false, 2, 9, 2, geAtlas.findRegion("panzer-iv"), geAtlas.findRegion("head"), unitAtlas);
                break;
            case GE_PANZER_IV_HQ:
                u = new Unit(Army.GE, true, 2, 9, 2, geAtlas.findRegion("panzer-iv-hq"), geAtlas.findRegion("head"), unitAtlas);
                break;
            case GE_TIGER:
                u = new Unit(Army.GE, false, 3, 11, 1, geAtlas.findRegion("tiger"), geAtlas.findRegion("head"), unitAtlas);
                break;
            case GE_WESPE:
                u = new Unit(Army.GE, false, 5, 8, 1, geAtlas.findRegion("wespe"), geAtlas.findRegion("head"), unitAtlas);
                break;
            case US_AT_GUN:
                u = new Unit(Army.US, false, 1, 7, 10, 1, usAtlas.findRegion("at-gun"), usAtlas.findRegion("head"), unitAtlas);
                break;
            case US_INFANTRY:
                u = new Unit(Army.US, false, 1, 7, 10, 1, usAtlas.findRegion("infantry"), usAtlas.findRegion("head"), unitAtlas);
                break;
            case US_PERSHING:
                u = new Unit(Army.US, false, 3, 10, 2, usAtlas.findRegion("pershing"), usAtlas.findRegion("head"), unitAtlas);
                break;
            case US_PERSHING_HQ:
                u = new Unit(Army.US, true, 3, 10, 2, usAtlas.findRegion("pershing-hq"), usAtlas.findRegion("head"), unitAtlas);
                break;
            case US_PRIEST:
                u = new Unit(Army.US, false, 5, 8, 1, usAtlas.findRegion("priest"), usAtlas.findRegion("head"), unitAtlas);
                break;
            case US_SHERMAN:
                u = new Unit(Army.US, false, 2, 9, 2, usAtlas.findRegion("sherman"), usAtlas.findRegion("head"), unitAtlas);
                break;
            case US_SHERMAN_HQ:
                u = new Unit(Army.US, true, 2, 9, 2, usAtlas.findRegion("sherman-hq"), usAtlas.findRegion("head"), unitAtlas);
                break;
            case US_WOLVERINE:
                u = new Unit(Army.US, false, 3, 8, 3, usAtlas.findRegion("wolverine"), usAtlas.findRegion("head"), unitAtlas);
                break;
        }

        return u;
    }

    public enum MapType
    {
        MAP_A,
        MAP_B
    }

    private Board.Config config()
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

    public Map getMap(AssetManager manager, MapType t)
    {
        Board.Config cfg = config();

        Map m = null;
        switch(t) {
            case MAP_A:
                m = new MapA(this, config(), manager.get("images/map_a.png", Texture.class));
                break;
            case MAP_B:
                // m = new Map(config(), manager.get("images/map_b.png", Texture.class), hexAtlas);
                break;
        }

        return m;
    }

    public Hex getNewTile(float cx, float cy)
    {
        return new Hex(cx, cy, hexAtlas);
    }
}
