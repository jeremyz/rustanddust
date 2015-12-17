package ch.asynk.rustanddust.game.battles;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.Board;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Map;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Unit.UnitId;
import ch.asynk.rustanddust.game.Unit.UnitType;
import ch.asynk.rustanddust.game.Battle;
import ch.asynk.rustanddust.game.battles.BattleTest;

public class Factory implements Board.TileBuilder, Disposable
{
    public static final String FLAG_US = "us-flag";
    public static final String FLAG_GE = "ge-flag";
    public static final String HUD_TURNS = "turns";
    public static final String HUD_STARS = "stars";
    public static final String HUD_APS = "aps";
    public static final String PNG_ATTACK = "attack";
    public static final String PNG_DEFENSE = "defense";
    public static final String ACT_DONE = "ok";
    public static final String ACT_ABORT  = "cancel";
    public static final String ACT_PROMOTE = "promote";
    public static final String DISABLED = "disabled";
    public static final String REINFORCEMENT = "reinforcement";
    public static final String REINFORCEMENT_SELECTED = "reinforcement-selected";

    public enum MapType
    {
        MAP_00,
    }

    public enum Scenarios
    {
        FAKE
    }

    public boolean assetsLoaded;
    public TextureAtlas hudAtlas;
    public TextureAtlas unitsAtlas;
    public TextureAtlas unitOverlaysAtlas;
    public TextureAtlas hexOverlaysAtlas;
    public Battle battles[];
    private final RustAndDust game;

    public Factory(final RustAndDust game)
    {
        this.game = game;
        this.assetsLoaded = false;
        battles = new Battle[] {
            new BattleTest(this),
        };
    }

    public void assetsLoaded()
    {
        if (assetsLoaded) return;
        int i = game.config.graphics.i;
        this.hudAtlas = game.manager.get(game.ATLAS_HUD, TextureAtlas.class);
        this.hexOverlaysAtlas = game.manager.get(game.ATLAS_HEX_OVERLAYS, TextureAtlas.class);
        this.unitsAtlas = game.manager.get(String.format(game.ATLAS_UNITS, i), TextureAtlas.class);
        this.unitOverlaysAtlas = game.manager.get(String.format(game.ATLAS_UNIT_OVERLAYS, i), TextureAtlas.class);
        this.assetsLoaded = true;
    }

    @Override
    public void dispose()
    {
        if (!assetsLoaded) return;
        hudAtlas.dispose();
        unitsAtlas.dispose();
        unitOverlaysAtlas.dispose();
        hexOverlaysAtlas.dispose();
        this.assetsLoaded = false;
    }

    public AtlasRegion getHudRegion(String s)
    {
        return hudAtlas.findRegion(s);
    }

    public AtlasRegion getFlag(Army army)
    {
        return hudAtlas.findRegion(army.flag());
    }

    public Map getMap(MapType t)
    {
        Map m = null;
        switch(t) {
            case MAP_00:
                m = new Map00(game, game.PNG_MAP_00, game.PNG_SELECTED);
                break;
        }

        return m;
    }

    public Player getPlayer(Army army)
    {
        if (army == Army.US)
            return new Player(game, Army.US, 10);
        else
            return new Player(game, Army.GE, 10);
    }

    public Unit getUnit(UnitId id, boolean hq, boolean ace)
    {
        Unit u = null;
        UnitType ut = UnitType.HARD_TARGET;
        switch(id) {
            case GE_AT_GUN:
                ut = UnitType.AT_GUN;
                u = buildUnit(Army.GE, id, ut, hq, ace, 3, 8, 9, 1, "ge-at-gun");
                break;
            case GE_INFANTRY:
                ut = UnitType.INFANTRY;
                u = buildUnit(Army.GE, id, ut, hq, ace, 1, 7, 10, 1, "ge-infantry");
                break;
            case GE_KINGTIGER:
                u = buildUnit(Army.GE, id, ut, hq, ace, 3, 12, -1, 1, "ge-kingtiger");
                break;
            case GE_PANZER_IV:
                u = buildUnit(Army.GE, id, ut, hq, ace, 2, 9, -1, 2, "ge-panzer-iv");
                break;
            case GE_TIGER:
                u = buildUnit(Army.GE, id, ut, hq, ace, 3, 11, -1, 1, "ge-tiger");
                break;
            case GE_WESPE:
                ut = UnitType.ARTILLERY;
                u = buildUnit(Army.GE, id, ut, hq, ace, 5, 8, -1, 1, "ge-wespe");
                break;
            case US_AT_GUN:
                ut = UnitType.AT_GUN;
                u = buildUnit(Army.US, id, ut, hq, ace, 1, 7, 10, 1, "us-at-gun");
                break;
            case US_INFANTRY:
                ut = UnitType.INFANTRY;
                u = buildUnit(Army.US, id, ut, hq, ace, 1, 7, 10, 1, "us-infantry");
                break;
            case US_PERSHING:
                u = buildUnit(Army.US, id, ut, hq, ace, 3, 10, -1, 3, "us-m26-pershing");
                break;
            case US_PRIEST:
                ut = UnitType.ARTILLERY;
                u = buildUnit(Army.US, id, ut, hq, ace, 5, 8, -1, 1, "us-m7-priest");
                break;
            case US_SHERMAN:
                u = buildUnit(Army.US, id, ut, hq, ace, 2, 9, -1, 2, "us-m4-sherman");
                break;
            case US_WOLVERINE:
                u = buildUnit(Army.US, id, ut, hq, ace, 3, 8, -1, 3, "us-m10-wolverine");
                break;
        }

        return u;
    }

    private Unit buildUnit(Army army, UnitId id, UnitType ut, boolean hq, boolean ace, int a, int d, int cd, int m, String body)
    {
        return new Unit(army, id, ut, hq, ace, a, d, cd, m, getUnitRegion(body), getHead(army, body), unitOverlaysAtlas);
    }

    private AtlasRegion getUnitRegion(String s)
    {
        return unitsAtlas.findRegion(s);
    }

    private AtlasRegion getHead(Army army, String body)
    {
        String head = null;
        switch(game.config.graphics) {
            case CHITS:
                head = ((army == Army.US) ? "us-head" : "ge-head");
                break;
            case TANKS:
                head = body + "-head";
                break;
        }
        return getUnitRegion(head);
    }

    public Hex getNewTile(float x, float y, int col, int row, boolean offmap)
    {
        Hex hex = new Hex(x, y, col, row, hexOverlaysAtlas);
        if (offmap) hex.terrain = Hex.Terrain.OFFMAP;
        return hex;
    }
}
