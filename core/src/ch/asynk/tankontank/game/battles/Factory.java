package ch.asynk.tankontank.game.battles;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.game.Player;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.Hex;
import ch.asynk.tankontank.game.Army;
import ch.asynk.tankontank.game.Unit;
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

    public TextureAtlas hudAtlas;
    public TextureAtlas pawnsAtlas;
    public TextureAtlas pawnOverlaysAtlas;
    public TextureAtlas tileOverlaysAtlas;
    private final TankOnTank game;

    public Factory(final TankOnTank game)
    {
        this.game = game;
    }

    public void assetsLoaded()
    {
        this.hudAtlas = game.manager.get("data/hud.atlas", TextureAtlas.class);
        this.pawnsAtlas = game.manager.get("data/pawns.atlas", TextureAtlas.class);
        this.pawnOverlaysAtlas = game.manager.get("data/pawn-overlays.atlas", TextureAtlas.class);
        this.tileOverlaysAtlas = game.manager.get("data/tile-overlays.atlas", TextureAtlas.class);
    }

    @Override
    public void dispose()
    {
        hudAtlas.dispose();
        pawnsAtlas.dispose();
        pawnOverlaysAtlas.dispose();
        tileOverlaysAtlas.dispose();
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
            return new Player(game, Army.US, game.skin.getFont("default-font"), hudAtlas, "us-flag", 10);
        else
            return new Player(game, Army.GE, game.skin.getFont("default-font"), hudAtlas, "ge-flag", 10);
    }

    public Unit getUnit(UnitId id)
    {
        Unit u = null;
        UnitType ut = UnitType.HARD_TARGET;
        UnitType utHq = UnitType.HARD_TARGET_HQ;
        switch(id) {
            case GE_AT_GUN:
                ut = UnitType.AT_GUN;
                u = new Unit(Army.GE, id, ut, 3, 8, 9, 1, "ge-at-gun", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_INFANTRY:
                ut = UnitType.INFANTRY;
                u = new Unit(Army.GE, id, ut, 1, 7, 10, 1, "ge-infantry", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_KINGTIGER:
                u = new Unit(Army.GE, id, ut, 3, 12, 1, "ge-kingtiger", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_PANZER_IV:
                u = new Unit(Army.GE, id, ut, 2, 9, 2, "ge-panzer-iv", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_PANZER_IV_HQ:
                u = new Unit(Army.GE, id, utHq, 2, 9, 2, "ge-panzer-iv-hq", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_TIGER:
                u = new Unit(Army.GE, id, ut, 3, 11, 1, "ge-tiger", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case GE_WESPE:
                ut = UnitType.ARTILLERY;
                u = new Unit(Army.GE, id, ut, 5, 8, 1, "ge-wespe", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_AT_GUN:
                ut = UnitType.AT_GUN;
                u = new Unit(Army.US, id, ut, 1, 7, 10, 1, "us-at-gun", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_INFANTRY:
                ut = UnitType.INFANTRY;
                u = new Unit(Army.US, id, ut, 1, 7, 10, 1, "us-infantry", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_PERSHING:
                u = new Unit(Army.US, id, ut, 3, 10, 2, "us-pershing", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_PERSHING_HQ:
                u = new Unit(Army.US, id, utHq, 3, 10, 2, "us-pershing-hq", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_PRIEST:
                ut = UnitType.ARTILLERY;
                u = new Unit(Army.US, id, ut, 5, 8, 1, "us-priest", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_SHERMAN:
                u = new Unit(Army.US, id, ut, 2, 9, 2, "us-sherman", "us-sherman-head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_SHERMAN_HQ:
                u = new Unit(Army.US, id, utHq, 2, 9, 2, "us-sherman-hq", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
            case US_WOLVERINE:
                u = new Unit(Army.US, id, ut, 3, 8, 3, "us-wolverine", "head", pawnsAtlas, pawnOverlaysAtlas);
                break;
        }

        return u;
    }

    public Hex getNewTile(float x, float y, int col, int row)
    {
        return new Hex(x, y, col, row, tileOverlaysAtlas);
    }
}
