package ch.asynk.tankontank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ch.asynk.tankontank.screens.LoadScreen;

public class TankOnTank extends Game
{
    public AssetManager manager;
    public Skin skin;

    @Override
    public void create ()
    {
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        Gdx.app.debug("TankOnTank", "create() [" + Gdx.graphics.getWidth() + ";" + Gdx.graphics.getHeight() + "]");

        manager = new AssetManager();
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        this.setScreen(new LoadScreen(this));

    }

    public void loadAssets()
    {
        Gdx.app.debug("TankOnTank", "  load assets : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        manager.load("images/map_a.png", Texture.class);
        // manager.load("images/map_b.png", Texture.class);
        manager.load("images/dice.pack", TextureAtlas.class);
        manager.load("images/counters.pack", TextureAtlas.class);
        manager.load("images/ge.atlas", TextureAtlas.class);
        manager.load("images/us.atlas", TextureAtlas.class);
        manager.load("images/hex.atlas", TextureAtlas.class);
        // manager.load("images/cup.jpg", Texture.class);
        // manager.load("images/small.png", Texture.class);
    }

    public void unloadAssets()
    {
        Gdx.app.debug("TankOnTank", "unload assets : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        Gdx.app.debug("TankOnTank", "diagnostics:\n" + manager.getDiagnostics() );
        manager.unload("images/map_a.png");
        // manager.unload("images/map_b.png");
        manager.unload("images/dice.pack");
        manager.unload("images/counters.pack");
        manager.unload("images/ge.atlas");
        manager.unload("images/us.atlas");
        manager.unload("images/hex.atlas");
        // manager.unload("images/cup.jpg");
        // manager.unload("images/small.png");
        Gdx.app.debug("TankOnTank", "diagnostics:\n" + manager.getDiagnostics() );
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
    //     Gdx.app.debug("TankOnTank", "resize(" + width + ", " + height + ")");
    //     super.resize(width, height);
    // }

    @Override
    public void dispose()
    {
        Gdx.app.debug("TankOnTank", "dispose()");
        getScreen().dispose();
    }

    // @Override
    // public void pause()
    // {
    //     Gdx.app.debug("TankOnTank", "pause()");
    // }

    // @Override
    // public void resume()
    // {
    //     Gdx.app.debug("TankOnTank", "resume()");
    // }
}
