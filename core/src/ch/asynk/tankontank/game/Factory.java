package ch.asynk.tankontank.game;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;

public class Factory implements Board.TileBuilder, Disposable
{
    private TextureAtlas pawnAtlas;

    public Factory(AssetManager manager)
    {
        pawnAtlas = manager.get("data/assets.atlas", TextureAtlas.class);
    }

    @Override
    public void dispose()
    {
        pawnAtlas.dispose();
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
        Unit.UnitType ut = Unit.UnitType.HARD_TARGET;
        Unit.UnitType utHq = Unit.UnitType.HARD_TARGET_HQ;
        switch(t) {
            case GE_AT_GUN:
                ut = Unit.UnitType.AT_GUN;
                u = new Unit(Army.GE, ut, 3, 8, 9, 1, pawnAtlas, "ge-at-gun", "head");
                break;
            case GE_INFANTRY:
                ut = Unit.UnitType.INFANTRY;
                u = new Unit(Army.GE, ut, 1, 7, 10, 1, pawnAtlas, "ge-infantry", "head");
                break;
            case GE_KINGTIGER:
                u = new Unit(Army.GE, ut, 3, 12, 1, pawnAtlas, "ge-kingtiger", "head");
                break;
            case GE_PANZER_IV:
                u = new Unit(Army.GE, ut, 2, 9, 2, pawnAtlas, "ge-panzer-iv", "head");
                break;
            case GE_PANZER_IV_HQ:
                u = new Unit(Army.GE, utHq, 2, 9, 2, pawnAtlas, "ge-panzer-iv-hq", "head");
                break;
            case GE_TIGER:
                u = new Unit(Army.GE, ut, 3, 11, 1, pawnAtlas, "ge-tiger", "head");
                break;
            case GE_WESPE:
                u = new Unit(Army.GE, ut, 5, 8, 1, pawnAtlas, "ge-wespe", "head");
                break;
            case US_AT_GUN:
                ut = Unit.UnitType.AT_GUN;
                u = new Unit(Army.US, ut, 1, 7, 10, 1, pawnAtlas, "us-at-gun", "head");
                break;
            case US_INFANTRY:
                ut = Unit.UnitType.INFANTRY;
                u = new Unit(Army.US, ut, 1, 7, 10, 1, pawnAtlas, "us-infantry", "head");
                break;
            case US_PERSHING:
                u = new Unit(Army.US, ut, 3, 10, 2, pawnAtlas, "us-pershing", "head");
                break;
            case US_PERSHING_HQ:
                u = new Unit(Army.US, utHq, 3, 10, 2, pawnAtlas, "us-pershing-hq", "head");
                break;
            case US_PRIEST:
                u = new Unit(Army.US, ut, 5, 8, 1, pawnAtlas, "us-priest", "head");
                break;
            case US_SHERMAN:
                u = new Unit(Army.US, ut, 2, 9, 2, pawnAtlas, "us-sherman", "us-sherman-head");
                break;
            case US_SHERMAN_HQ:
                u = new Unit(Army.US, utHq, 2, 9, 2, pawnAtlas, "us-sherman-hq", "head");
                break;
            case US_WOLVERINE:
                u = new Unit(Army.US, ut, 3, 8, 3, pawnAtlas, "us-wolverine", "head");
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

    public Map getMap(Ctrl ctrl, AssetManager manager, MapType t)
    {
        Board.Config cfg = config();

        Map m = null;
        switch(t) {
            case MAP_A:
                m = new MapA(ctrl, this, config(), manager.get("data/map_a.png", Texture.class));
                break;
            case MAP_B:
                m = new MapB(ctrl, this, config(), manager.get("data/map_b.png", Texture.class));
                break;
        }

        return m;
    }

    public Player getPlayer(Army army)
    {
        if (army == Army.US)
            return new Player(Army.US, pawnAtlas, "us-flag", 10);
        else
            return new Player(Army.GE, pawnAtlas, "ge-flag", 10);
    }

    public Hex getNewTile(float cx, float cy)
    {
        return new Hex(cx, cy, pawnAtlas);
    }

    public Player fakeSetup(Map map, Player gePlayer, Player usPlayer)
    {
        Orientation o = Orientation.NORTH;
        GridPoint2 p = new GridPoint2();

        gePlayer.addUnit(map.setPawnAt(getUnit(UnitType.GE_AT_GUN), p.set(4, 7), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitType.GE_INFANTRY), p.set(3, 6), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitType.GE_KINGTIGER), p.set(3, 5), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitType.GE_PANZER_IV), p.set(2, 4), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitType.GE_PANZER_IV_HQ), p.set(2, 3), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitType.GE_TIGER), p.set(1, 2), o));
        gePlayer.addUnit(map.setPawnAt(getUnit(UnitType.GE_WESPE), p.set(1, 1), o));

        o = Orientation.SOUTH;
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitType.US_AT_GUN), p.set(12, 7), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitType.US_INFANTRY), p.set(11, 6), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitType.US_PERSHING), p.set(11, 5), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitType.US_PERSHING_HQ), p.set(10, 4), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitType.US_PRIEST), p.set(10, 3), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitType.US_SHERMAN), p.set(9, 2), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitType.US_SHERMAN_HQ), p.set(9, 1), o));
        usPlayer.addUnit(map.setPawnAt(getUnit(UnitType.US_WOLVERINE), p.set(8, 0), o));

        return usPlayer;
    }
}
