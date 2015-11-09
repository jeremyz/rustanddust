package ch.asynk.rustanddust;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import ch.asynk.rustanddust.screens.MenuScreen;
import ch.asynk.rustanddust.screens.GameScreen;
import ch.asynk.rustanddust.game.Ctrl;
import ch.asynk.rustanddust.game.Config;
import ch.asynk.rustanddust.game.battles.Factory;

public class RustAndDust extends Game
{
    public AssetManager manager;
    public Factory factory;
    public Ctrl ctrl;
    public Config config;
    public int hudCorrection;

    public TextureAtlas uiAtlas;
    public TextureAtlas menuAtlas;
    public BitmapFont fontB;
    public BitmapFont fontW;

    public enum State
    {
        MENU,
        GAME,
        NONE
    }
    private State state;

    public static void debug(String msg)
    {
        debug("", msg);
    }

    public static void debug(String dom, String msg)
    {
        Gdx.app.debug(dom, msg);
    }

    @Override
    public void create ()
    {
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        this.hudCorrection = ((int) (125 * Gdx.graphics.getDensity()) - 75);
        debug("RustAndDust", "create() [" + Gdx.graphics.getWidth() + ";" + Gdx.graphics.getHeight() + "] " + Gdx.graphics.getDensity() + " -> " + hudCorrection);

        manager = new AssetManager();
        factory = new Factory(this);
        config = new Config();

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
        if (config.battle.getMapType() == Factory.MapType.MAP_A)
            manager.load("data/map_a.png", Texture.class);
        if (config.battle.getMapType() == Factory.MapType.MAP_B)
            manager.load("data/map_b.png", Texture.class);
        int i = config.graphics.i;
        manager.load(String.format("data/units%d.atlas",i), TextureAtlas.class);
        manager.load(String.format("data/unit-overlays%d.atlas", i), TextureAtlas.class);
        manager.load("data/hex.png", Texture.class);
        manager.load("data/hud.atlas", TextureAtlas.class);
        manager.load("data/hex-overlays.atlas", TextureAtlas.class);
        manager.load("data/dice.png", Texture.class);
        manager.load("data/infantry_fire.png", Texture.class);
        manager.load("data/tank_fire.png", Texture.class);
        manager.load("data/explosions.png", Texture.class);
        manager.load("sounds/dice.mp3", Sound.class);
        manager.load("sounds/tank_move.mp3", Sound.class);
        manager.load("sounds/infantry_move.mp3", Sound.class);
        manager.load("sounds/infantry_fire.mp3", Sound.class);
        manager.load("sounds/tank_fire.mp3", Sound.class);
        manager.load("sounds/tank_fire_short.mp3", Sound.class);
        manager.load("sounds/explosion.mp3", Sound.class);
        manager.load("sounds/explosion_short.mp3", Sound.class);
        manager.load("sounds/promote_us.mp3", Sound.class);
        manager.load("sounds/promote_ge.mp3", Sound.class);
        debug("RustAndDust", "  assets loaded : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
    }

    private void unloadGameAssets()
    {
        if (config.battle.getMapType() == Factory.MapType.MAP_A)
            manager.unload("data/map_a.png");
        if (config.battle.getMapType() == Factory.MapType.MAP_B)
            manager.unload("data/map_b.png");
        int i = config.graphics.i;
        manager.unload(String.format("data/units%d.atlas",i));
        manager.unload(String.format("data/unit-overlays%d.atlas", i));
        manager.unload("data/hex.png");
        manager.unload("data/hud.atlas");
        manager.unload("data/hex-overlays.atlas");
        manager.unload("data/dice.png");
        manager.unload("data/infantry_fire.png");
        manager.unload("data/tank_fire.png");
        manager.unload("data/explosions.png");
        manager.unload("sounds/dice.mp3");
        manager.unload("sounds/tank_move.mp3");
        manager.unload("sounds/infantry_move.mp3");
        manager.unload("sounds/infantry_fire.mp3");
        manager.unload("sounds/tank_fire.mp3");
        manager.unload("sounds/tank_fire_short.mp3");
        manager.unload("sounds/explosion.mp3");
        manager.unload("sounds/explosion_short.mp3");
        manager.unload("sounds/promote_us.mp3");
        manager.unload("sounds/promote_ge.mp3");
        debug("RustAndDust", "  assets unloaded : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
    }

    private void loadUiAssets()
    {
        manager.load("data/ui.atlas", TextureAtlas.class);
        manager.finishLoading();
        uiAtlas = manager.get("data/ui.atlas", TextureAtlas.class);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/veteran-typewriter.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = Math.max((int) (Gdx.graphics.getHeight() * 0.04f), 15);
        parameter.color = Color.BLACK;
        fontB = generator.generateFont(parameter);
        parameter.color = Color.WHITE;
        fontW = generator.generateFont(parameter);
    }

    private void unloadUiAssets()
    {
        fontB.dispose();
        fontW.dispose();
        manager.unload("data/ui.atlas");
    }

    private void loadMenuAssets()
    {
        manager.load("data/map_a.png", Texture.class);
        manager.load("data/menu.atlas", TextureAtlas.class);
        manager.finishLoading();
        menuAtlas = manager.get("data/menu.atlas", TextureAtlas.class);
    }

    private void unloadMenuAssets()
    {
        manager.unload("data/map_a.png");
        manager.unload("data/menu.atlas");
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

    // @Override
    // public void pause()
    // {
    //     debug("RustAndDust", "pause()");
    // }

    // @Override
    // public void resume()
    // {
    //     debug("RustAndDust", "resume()");
    // }
}
