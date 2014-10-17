package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.game.Unit.UnitId;
import ch.asynk.tankontank.game.Unit.UnitType;

public class Factory implements Board.TileBuilder, Disposable
{
    public enum MapType
    {
        MAP_A,
        MAP_B
    }

    public enum Scenarios
    {
        FAKE
    }

    private TextureAtlas atlas;
    private final TankOnTank game;

    public Factory(final TankOnTank game)
    {
        this.game = game;
    }

    public void assetsLoaded()
    {
        this.atlas = game.manager.get("data/assets.atlas", TextureAtlas.class);
    }

    @Override
    public void dispose()
    {
        atlas.dispose();
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

    public Map getMap(MapType t)
    {
        Board.Config cfg = config();

        Map m = null;
        switch(t) {
            case MAP_A:
                m = new MapA(game, config(), "data/map_a.png");
                break;
            case MAP_B:
                m = new MapB(game, config(), "data/map_b.png");
                break;
        }

        return m;
    }

    public Player getPlayer(Army army)
    {
        if (army == Army.US)
            return new Player(Army.US, atlas, "us-flag", 10);
        else
            return new Player(Army.GE, atlas, "ge-flag", 10);
    }

    public Unit getUnit(UnitId id)
    {
        Unit u = null;
        UnitType ut = UnitType.HARD_TARGET;
        UnitType utHq = UnitType.HARD_TARGET_HQ;
        switch(id) {
            case GE_AT_GUN:
                ut = UnitType.AT_GUN;
                u = new Unit(Army.GE, id, ut, 3, 8, 9, 1, atlas, "ge-at-gun", "head");
                break;
            case GE_INFANTRY:
                ut = UnitType.INFANTRY;
                u = new Unit(Army.GE, id, ut, 1, 7, 10, 1, atlas, "ge-infantry", "head");
                break;
            case GE_KINGTIGER:
                u = new Unit(Army.GE, id, ut, 3, 12, 1, atlas, "ge-kingtiger", "head");
                break;
            case GE_PANZER_IV:
                u = new Unit(Army.GE, id, ut, 2, 9, 2, atlas, "ge-panzer-iv", "head");
                break;
            case GE_PANZER_IV_HQ:
                u = new Unit(Army.GE, id, utHq, 2, 9, 2, atlas, "ge-panzer-iv-hq", "head");
                break;
            case GE_TIGER:
                u = new Unit(Army.GE, id, ut, 3, 11, 1, atlas, "ge-tiger", "head");
                break;
            case GE_WESPE:
                ut = UnitType.ARTILLERY;
                u = new Unit(Army.GE, id, ut, 5, 8, 1, atlas, "ge-wespe", "head");
                break;
            case US_AT_GUN:
                ut = UnitType.AT_GUN;
                u = new Unit(Army.US, id, ut, 1, 7, 10, 1, atlas, "us-at-gun", "head");
                break;
            case US_INFANTRY:
                ut = UnitType.INFANTRY;
                u = new Unit(Army.US, id, ut, 1, 7, 10, 1, atlas, "us-infantry", "head");
                break;
            case US_PERSHING:
                u = new Unit(Army.US, id, ut, 3, 10, 2, atlas, "us-pershing", "head");
                break;
            case US_PERSHING_HQ:
                u = new Unit(Army.US, id, utHq, 3, 10, 2, atlas, "us-pershing-hq", "head");
                break;
            case US_PRIEST:
                ut = UnitType.ARTILLERY;
                u = new Unit(Army.US, id, ut, 5, 8, 1, atlas, "us-priest", "head");
                break;
            case US_SHERMAN:
                u = new Unit(Army.US, id, ut, 2, 9, 2, atlas, "us-sherman", "us-sherman-head");
                break;
            case US_SHERMAN_HQ:
                u = new Unit(Army.US, id, utHq, 2, 9, 2, atlas, "us-sherman-hq", "head");
                break;
            case US_WOLVERINE:
                u = new Unit(Army.US, id, ut, 3, 8, 3, atlas, "us-wolverine", "head");
                break;
        }

        return u;
    }

    public Hex getNewTile(float cx, float cy)
    {
        return new Hex(cx, cy, atlas);
    }

    public Player fakeSetup(Map map, Player gePlayer, Player usPlayer)
    {
        Orientation o = Orientation.NORTH;
        GridPoint2 p = new GridPoint2();

        gePlayer.addUnit(map.setPawnAt(getUnit(UnitId.GE_TIGER), p.set(4, 7), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitId.GE_TIGER), p.set(3, 6), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitId.GE_PANZER_IV), p.set(3, 5), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitId.GE_PANZER_IV_HQ), p.set(2, 4), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitId.GE_PANZER_IV), p.set(2, 3), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitId.GE_PANZER_IV), p.set(1, 2), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitId.GE_PANZER_IV_HQ), p.set(1, 1), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitId.GE_PANZER_IV), p.set(0, 0), o));

        o = Orientation.SOUTH;
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_WOLVERINE), p.set(13, 8), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_WOLVERINE), p.set(12, 7), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_PRIEST), p.set(12, 6), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_SHERMAN), p.set(11, 5), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_SHERMAN_HQ), p.set(11, 4), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_SHERMAN), p.set(10, 3), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_SHERMAN), p.set(10, 2), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_SHERMAN_HQ), p.set(9, 1), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitId.US_SHERMAN), p.set(9, 0), o));

        return usPlayer;
    }
}
