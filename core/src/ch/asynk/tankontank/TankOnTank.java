package ch.asynk.tankontank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.screens.LoadScreen;
import ch.asynk.tankontank.screens.OptionsScreen;
import ch.asynk.tankontank.screens.GameScreen;
import ch.asynk.tankontank.game.Ctrl;
import ch.asynk.tankontank.game.Config;
import ch.asynk.tankontank.game.battles.Factory;

public class TankOnTank extends Game
{
    public AssetManager manager;
    public Factory factory;
    public Ctrl ctrl;
    public Config config;

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
        debug("TankOnTank", "create() [" + Gdx.graphics.getWidth() + ";" + Gdx.graphics.getHeight() + "]");

        manager = new AssetManager();
        factory = new Factory(this);
        config = new Config();

        this.setScreen(new LoadScreen(this));
    }

    public void loadAssets()
    {
        debug("TankOnTank", "  load assets : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        manager.load("data/map_a.png", Texture.class);
        manager.load("data/map_b.png", Texture.class);
        manager.load("data/hex.png", Texture.class);
        manager.load("data/ui.atlas", TextureAtlas.class);
        manager.load("data/hud.atlas", TextureAtlas.class);
        manager.load("data/units.atlas", TextureAtlas.class);
        manager.load("data/unit-overlays.atlas", TextureAtlas.class);
        manager.load("data/hex-overlays.atlas", TextureAtlas.class);
        manager.load("data/dice.png", Texture.class);
        manager.load("data/infantry_fire.png", Texture.class);
        manager.load("data/tank_fire.png", Texture.class);
        manager.load("data/explosions.png", Texture.class);
        manager.load("sounds/dice.mp3", Sound.class);
        manager.load("sounds/move.mp3", Sound.class);
        manager.load("sounds/infantry_fire.mp3", Sound.class);
        manager.load("sounds/tank_fire.mp3", Sound.class);
        manager.load("sounds/tank_fire_short.mp3", Sound.class);
        manager.load("sounds/explosion.mp3", Sound.class);
        manager.load("sounds/explosion_short.mp3", Sound.class);
        manager.load("sounds/promote.mp3", Sound.class);
    }

    public void unloadAssets()
    {
        debug("TankOnTank", "unload assets : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        debug("TankOnTank", "diagnostics:\n" + manager.getDiagnostics() );
        manager.unload("data/map_a.png");
        manager.unload("data/map_b.png");
        manager.unload("data/hex.png");
        manager.unload("data/ui.atlas");
        manager.unload("data/hud.atlas");
        manager.unload("data/units.atlas");
        manager.unload("data/unit-overlays.atlas");
        manager.unload("data/hex-overlays.atlas");
        manager.unload("data/dice.png");
        manager.unload("data/infantry_fire.png");
        manager.unload("data/tank_fire.png");
        manager.unload("data/explosions.png");
        manager.unload("sounds/dice.mp3");
        manager.unload("sounds/move.mp3");
        manager.unload("sounds/infantry_fire.mp3");
        manager.unload("sounds/tank_fire.mp3");
        manager.unload("sounds/tank_fire_short.mp3");
        manager.unload("sounds/explosion.mp3");
        manager.unload("sounds/explosion_short.mp3");
        manager.unload("sounds/promote.mp3");
        debug("TankOnTank", "diagnostics:\n" + manager.getDiagnostics() );
    }

    public void switchToOptions()
    {
        factory.assetsLoaded();
        setScreen(new OptionsScreen(this));
    }

    public void switchToGame()
    {
        setScreen(new GameScreen(this));
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
    //     debug("TankOnTank", "resize(" + width + ", " + height + ")");
    //     super.resize(width, height);
    // }

    @Override
    public void dispose()
    {
        debug("TankOnTank", "dispose()");
        getScreen().dispose();
        factory.dispose();
        unloadAssets();
    }

    // @Override
    // public void pause()
    // {
    //     debug("TankOnTank", "pause()");
    // }

    // @Override
    // public void resume()
    // {
    //     debug("TankOnTank", "resume()");
    // }
}
