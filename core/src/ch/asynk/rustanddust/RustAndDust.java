package ch.asynk.rustanddust;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import ch.asynk.rustanddust.screens.MenuScreen;
import ch.asynk.rustanddust.screens.GameScreen;
import ch.asynk.rustanddust.util.DB;
import ch.asynk.rustanddust.util.Backend;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Config;
import ch.asynk.rustanddust.game.Factory;
import ch.asynk.rustanddust.ui.Bg;

public class RustAndDust extends Game
{
    public static final String NINEPATCH = "typewriter";
    public static final String TTF_FONT = "skin/veteran-typewriter.ttf";
    public static final String ATLAS_UI = "data/ui.atlas";
    public static final String ATLAS_HUD = "data/hud.atlas";
    public static final String ATLAS_UNITS = "data/units%d.atlas";
    public static final String ATLAS_UNIT_OVERLAYS = "data/unit-overlays%d.atlas";
    public static final String ATLAS_HEX_OVERLAYS = "data/hex-overlays.atlas";
    public static final String PNG_SELECTED = "data/selected.png";
    public static final String PNG_MAP_00= "data/map_00.png";
    public static final String PNG_DICE = "data/dice.png";
    public static final String PNG_INF_FIRE = "data/infantry_fire.png";
    public static final String PNG_TANK_FIRE = "data/tank_fire.png";
    public static final String PNG_EXPLOSIONS = "data/explosions.png";
    public static final String SND_DICE = "sounds/dice.mp3";
    public static final String SND_TANK_MOVE = "sounds/tank_move.mp3";
    public static final String SND_INF_MOVE = "sounds/infantry_move.mp3";
    public static final String SND_INF_FIRE = "sounds/infantry_fire.mp3";
    public static final String SND_TANK_FIRE = "sounds/tank_fire.mp3";
    public static final String SND_TANK_FIRE_SHORT = "sounds/tank_fire_short.mp3";
    public static final String SND_EXPLOSION = "sounds/explosion.mp3";
    public static final String SND_EXPLOSION_SHORT = "sounds/explosion_short.mp3";
    public static final String SND_PROMOTE_US = "sounds/promote_us.mp3";
    public static final String SND_PROMOTE_GE = "sounds/promote_ge.mp3";

    public static final String UI_OK = "ok";
    public static final String UI_CANCEL = "cancel";
    public static final String UI_FROM = "from";
    public static final String UI_TO = "to";
    public static final String UI_MOVE = "move";
    public static final String UI_UNIT = "unit";
    public static final String UI_US_FLAG = "us-flag";
    public static final String UI_GE_FLAG = "ge-flag";

    public static final String DOM = "RustAndDust";

    public static final String DB_FILE = "data/rustanddust.sqlite";

    public AssetManager manager;
    public Factory factory;
    public Ctrl ctrl;
    public Config config;
    public int hudCorrection;
    public Backend backend;
    public DB db;

    public TextureAtlas uiAtlas;
    public BitmapFont font;
    public NinePatch ninePatch;

    public enum State
    {
        MENU,
        GAME,
        NONE
    }
    private State state;

    public RustAndDust(Backend backend)
    {
        this.backend = backend;
    }

    public static void error(String msg)
    {
        Gdx.app.error(DOM, msg);
    }

    public static void debug(String msg)
    {
        Gdx.app.debug(DOM, msg);
    }

    public static void debug(String info, String msg)
    {
        Gdx.app.debug(DOM, String.format("%s : %s", info, msg));
    }

    public TextureAtlas.AtlasRegion getUiRegion(String s)
    {
        return uiAtlas.findRegion(s);
    }

    @Override
    public void create ()
    {
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        this.hudCorrection = ((int) (125 * Gdx.graphics.getDensity()) - 75);
        debug("RustAndDust", "create() [" + Gdx.graphics.getWidth() + ";" + Gdx.graphics.getHeight() + "] " + Gdx.graphics.getDensity() + " -> " + hudCorrection);

        db = new DB(Gdx.files.internal(DB_FILE).path());
        db.setup();

        manager = new AssetManager();
        factory = new Factory(this);
        config = new Config();
        backend.init(this);

        state = State.NONE;
        loadUiAssets();
        switchToMenu();
    }

    public void switchToMenu()
    {
        if (state == State.GAME) {
            unloadGameAssets();
            factory.dispose();
            ctrl.dispose();
            getScreen().dispose();
        }
        loadMenuAssets();
        state = State.MENU;
        setScreen(new MenuScreen(this));
    }

    public void switchToGame()
    {
        unloadMenuAssets();
        getScreen().dispose();
        factory.assetsLoaded();
        state = State.GAME;
        setScreen(new GameScreen(this));
    }

    public void loadGameAssets()
    {
        if (config.battle.getMapType() == Factory.MapType.MAP_00)
            manager.load(PNG_MAP_00, Texture.class);
        int i = config.graphics.i;
        manager.load(String.format(ATLAS_UNITS, i), TextureAtlas.class);
        manager.load(String.format(ATLAS_UNIT_OVERLAYS, i), TextureAtlas.class);
        manager.load(ATLAS_HUD, TextureAtlas.class);
        manager.load(ATLAS_HEX_OVERLAYS, TextureAtlas.class);
        manager.load(PNG_SELECTED, Texture.class);
        manager.load(PNG_DICE, Texture.class);
        manager.load(PNG_INF_FIRE, Texture.class);
        manager.load(PNG_TANK_FIRE, Texture.class);
        manager.load(PNG_EXPLOSIONS, Texture.class);
        manager.load(SND_DICE, Sound.class);
        manager.load(SND_TANK_MOVE, Sound.class);
        manager.load(SND_INF_MOVE, Sound.class);
        manager.load(SND_INF_FIRE, Sound.class);
        manager.load(SND_TANK_FIRE, Sound.class);
        manager.load(SND_TANK_FIRE_SHORT, Sound.class);
        manager.load(SND_EXPLOSION, Sound.class);
        manager.load(SND_EXPLOSION_SHORT, Sound.class);
        manager.load(SND_PROMOTE_US, Sound.class);
        manager.load(SND_PROMOTE_GE, Sound.class);
        debug("RustAndDust", "  assets loaded : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
    }

    private void unloadGameAssets()
    {
        if (config.battle.getMapType() == Factory.MapType.MAP_00)
            manager.unload(PNG_MAP_00);
        int i = config.graphics.i;
        manager.unload(String.format(ATLAS_UNITS, i));
        manager.unload(String.format(ATLAS_UNIT_OVERLAYS, i));
        manager.unload(ATLAS_HUD);
        manager.unload(ATLAS_HEX_OVERLAYS);
        manager.unload(PNG_SELECTED);
        manager.unload(PNG_DICE);
        manager.unload(PNG_INF_FIRE);
        manager.unload(PNG_TANK_FIRE);
        manager.unload(PNG_EXPLOSIONS);
        manager.unload(SND_DICE);
        manager.unload(SND_TANK_MOVE);
        manager.unload(SND_INF_MOVE);
        manager.unload(SND_INF_FIRE);
        manager.unload(SND_TANK_FIRE);
        manager.unload(SND_TANK_FIRE_SHORT);
        manager.unload(SND_EXPLOSION);
        manager.unload(SND_EXPLOSION_SHORT);
        manager.unload(SND_PROMOTE_US);
        manager.unload(SND_PROMOTE_GE);
        debug("RustAndDust", "  assets unloaded : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
    }

    private void loadUiAssets()
    {
        manager.load(ATLAS_UI, TextureAtlas.class);
        manager.finishLoading();
        uiAtlas = manager.get(ATLAS_UI, TextureAtlas.class);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(TTF_FONT));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        float h = Gdx.graphics.getHeight();
        Bg.setScale(Math.max((h * 0.00125f), 1.0f));
        parameter.size = Math.max((int) (h * 0.04f), 16);
        parameter.color = Color.BLACK;
        font = generator.generateFont(parameter);
        ninePatch = uiAtlas.createPatch(NINEPATCH);
    }

    private void unloadUiAssets()
    {
        font.dispose();
        manager.unload(ATLAS_UI);
    }

    private void loadMenuAssets()
    {
        manager.load(PNG_MAP_00, Texture.class);
        manager.finishLoading();
    }

    private void unloadMenuAssets()
    {
        manager.unload(PNG_MAP_00);
    }

    // @Override
    // public void render ()
    // {
    //     Gdx.gl.glClearColor(0, 0, 0, 1);
    //     Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    //     super.render();
    // }

    // @Override
    // public void resize(int width, int height)
    // {
    //     debug("RustAndDust", "resize(" + width + ", " + height + ")");
    //     super.resize(width, height);
    // }

    @Override
    public void dispose()
    {
        debug("RustAndDust", "dispose()");
        debug("RustAndDust", "diagnostics:\n" + manager.getDiagnostics() );
        getScreen().dispose();
        unloadUiAssets();
        switch(state) {
            case MENU:
                unloadMenuAssets();
                break;
            case GAME:
                unloadGameAssets();
                factory.dispose();
                ctrl.dispose();
                break;
        }
        debug("RustAndDust", "diagnostics:\n" + manager.getDiagnostics() );
        manager.clear();
        manager.dispose();
    }

    @Override
    public void pause()
    {
        debug("RustAndDust", "pause()");
        getScreen().pause();
    }

    @Override
    public void resume()
    {
        debug("RustAndDust", "resume()");
        getScreen().resume();
    }
}
