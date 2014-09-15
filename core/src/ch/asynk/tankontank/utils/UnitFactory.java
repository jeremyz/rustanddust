package ch.asynk.tankontank.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ch.asynk.tankontank.actors.Unit;
import ch.asynk.tankontank.actors.HexMap;

public class UnitFactory
{
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

    private static HexMap map;
    private static TextureAtlas usAtlas;
    private static TextureAtlas geAtlas;

    public static void init(AssetManager manager, HexMap m)
    {
        map = m;
        usAtlas = manager.get("images/us.pack", TextureAtlas.class);
        geAtlas = manager.get("images/ge.pack", TextureAtlas.class);
    }

    public static void dispose()
    {
        usAtlas.dispose();
        geAtlas.dispose();
    }

    public static Unit getUnit(UnitType t)
    {
        Unit u = null;
        switch(t) {
            case GE_AT_GUN:
                u = new Unit(false, 3, 8, 9, 1, geAtlas.findRegion("at-gun"), map);
                break;
            case GE_INFANTRY:
                u = new Unit(false, 1, 7, 10, 1, geAtlas.findRegion("infantry"), map);
                break;
            case GE_KINGTIGER:
                u = new Unit(false, 3, 12, 1, geAtlas.findRegion("kingtiger"), map);
                break;
            case GE_PANZER_IV:
                u = new Unit(false, 2, 9, 2, geAtlas.findRegion("panzer-iv"), map);
                break;
            case GE_PANZER_IV_HQ:
                u = new Unit(true, 2, 9, 2, geAtlas.findRegion("panzer-iv-hq"), map);
                break;
            case GE_TIGER:
                u = new Unit(false, 3, 11, 1, geAtlas.findRegion("tiger"), map);
                break;
            case GE_WESPE:
                u = new Unit(false, 5, 8, 1, geAtlas.findRegion("wespe"), map);
                break;
            case US_AT_GUN:
                u = new Unit(false, 1, 7, 10, 1, usAtlas.findRegion("at-gun"), map);
                break;
            case US_INFANTRY:
                u = new Unit(false, 1, 7, 10, 1, usAtlas.findRegion("infantry"), map);
                break;
            case US_PERSHING:
                u = new Unit(false, 3, 10, 2, usAtlas.findRegion("pershing"), map);
                break;
            case US_PERSHING_HQ:
                u = new Unit(true, 3, 10, 2, usAtlas.findRegion("pershing-hq"), map);
                break;
            case US_PRIEST:
                u = new Unit(false, 5, 8, 1, usAtlas.findRegion("priest"), map);
                break;
            case US_SHERMAN:
                u = new Unit(false, 2, 9, 2, usAtlas.findRegion("sherman"), map);
                break;
            case US_SHERMAN_HQ:
                u = new Unit(true, 2, 9, 2, usAtlas.findRegion("sherman-hq"), map);
                break;
            case US_WOLVERINE:
                u = new Unit(false, 3, 8, 3, usAtlas.findRegion("wolverine"), map);
                break;
        }

        return u;
    }
}
