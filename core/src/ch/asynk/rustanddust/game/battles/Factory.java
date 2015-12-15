package ch.asynk.rustanddust.game.battles;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

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
    public TextureAtlas pawnsAtlas;
    public TextureAtlas pawnOverlaysAtlas;
    public TextureAtlas tileOverlaysAtlas;
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
        this.hudAtlas = game.manager.get("data/hud.atlas", TextureAtlas.class);
        this.tileOverlaysAtlas = game.manager.get("data/hex-overlays.atlas", TextureAtlas.class);
        this.pawnsAtlas = game.manager.get(String.format("data/units%d.atlas", i), TextureAtlas.class);
        this.pawnOverlaysAtlas = game.manager.get(String.format("data/unit-overlays%d.atlas", i), TextureAtlas.class);
        this.assetsLoaded = true;
    }

    @Override
    public void dispose()
    {
        if (!assetsLoaded) return;
        hudAtlas.dispose();
        pawnsAtlas.dispose();
        pawnOverlaysAtlas.dispose();
        tileOverlaysAtlas.dispose();
        this.assetsLoaded = false;
    }

    public Map getMap(MapType t)
    {
        Map m = null;
        switch(t) {
            case MAP_00:
                m = new Map00(game, "data/map_00.png", "data/selected.png");
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
                u = new Unit(Army.GE, id, ut, hq, ace, 3, 8, 9, 1, "ge-at-gun", "ge-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_INFANTRY:
                ut = UnitType.INFANTRY;
                u = new Unit(Army.GE, id, ut, hq, ace, 1, 7, 10, 1, "ge-infantry", "ge-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_KINGTIGER:
                u = new Unit(Army.GE, id, ut, hq, ace, 3, 12, -1, 1, "ge-kingtiger", "ge-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_PANZER_IV:
                u = new Unit(Army.GE, id, ut, hq, ace, 2, 9, -1, 2, "ge-panzer-iv", "ge-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_TIGER:
                u = new Unit(Army.GE, id, ut, hq, ace, 3, 11, -1, 1, "ge-tiger", "ge-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_WESPE:
                ut = UnitType.ARTILLERY;
                u = new Unit(Army.GE, id, ut, hq, ace, 5, 8, -1, 1, "ge-wespe", "ge-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_AT_GUN:
                ut = UnitType.AT_GUN;
                u = new Unit(Army.US, id, ut, hq, ace, 1, 7, 10, 1, "us-at-gun", "us-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_INFANTRY:
                ut = UnitType.INFANTRY;
                u = new Unit(Army.US, id, ut, hq, ace, 1, 7, 10, 1, "us-infantry", "us-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_PERSHING:
                u = new Unit(Army.US, id, ut, hq, ace, 3, 10, -1, 2, "us-pershing", "us-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_PRIEST:
                ut = UnitType.ARTILLERY;
                u = new Unit(Army.US, id, ut, hq, ace, 5, 8, -1, 1, "us-priest", "us-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_SHERMAN:
                    u = new Unit(Army.US, id, ut, hq, ace, 2, 9, -1, 2, "us-sherman", "us-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_WOLVERINE:
                u = new Unit(Army.US, id, ut, hq, ace, 3, 8, -1, 3, "us-wolverine", "us-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
        }

        return u;
    }

    public Hex getNewTile(float x, float y, int col, int row, boolean offmap)
    {
        Hex hex = new Hex(x, y, col, row, tileOverlaysAtlas);
        if (offmap) hex.terrain = Hex.Terrain.OFFMAP;
        return hex;
    }
}
