package ch.asynk.tankontank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ch.asynk.tankontank.screens.LoadScreen;

public class TankOnTank extends Game
{
    public AssetManager manager;

    @Override
    public void create ()
    {
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        Gdx.app.debug("TankOnTank", "create() [" + Gdx.graphics.getWidth() + ";" + Gdx.graphics.getHeight() + "]");

        manager = new AssetManager();

        this.setScreen(new LoadScreen(this));
    }

    public void loadAssets()
    {
        Gdx.app.debug("TankOnTank", "  load assets : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        manager.load("images/map_a.png", Texture.class);
        manager.load("images/map_b.png", Texture.class);
        manager.load("images/dice.pack", TextureAtlas.class);
        manager.load("images/cup.jpg", Texture.class);
        manager.load("images/back.png", Texture.class);
        manager.load("images/small.png", Texture.class);
        manager.load("images/turn_back.png", Texture.class);
        manager.load("images/turn_front.png", Texture.class);
        manager.load("images/ace.png", Texture.class);
        manager.load("images/ap_spent.png", Texture.class);
        manager.load("images/ap_2.png", Texture.class);
        manager.load("images/ap_3.png", Texture.class);
        manager.load("images/ap_4.png", Texture.class);
        manager.load("images/ge_atgun.png", Texture.class);
        manager.load("images/ge_infantry.png", Texture.class);
        manager.load("images/ge_tiger.png", Texture.class);
        manager.load("images/ge_kingtiger.png", Texture.class);
        manager.load("images/ge_panzer_iv.png", Texture.class);
        manager.load("images/ge_panzer_iv_hq.png", Texture.class);
        manager.load("images/ge_wespe.png", Texture.class);
        manager.load("images/us_atgun.png", Texture.class);
        manager.load("images/us_infantry.png", Texture.class);
        manager.load("images/us_pershing.png", Texture.class);
        manager.load("images/us_pershing_hq.png", Texture.class);
        manager.load("images/us_priest.png", Texture.class);
        manager.load("images/us_sherman.png", Texture.class);
        manager.load("images/us_sherman_hq.png", Texture.class);
        manager.load("images/us_wolverine.png", Texture.class);
    }

    public void unloadAssets()
    {
        Gdx.app.debug("TankOnTank", "unload assets : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        manager.unload("images/map_a.png");
        manager.unload("images/map_b.png");
        manager.unload("images/dice.pack");
        manager.unload("images/cup.jpg");
        manager.unload("images/back.png");
        manager.unload("images/small.png");
        manager.unload("images/turn_back.png");
        manager.unload("images/turn_front.png");
        manager.unload("images/ace.png");
        manager.unload("images/ap_spent.png");
        manager.unload("images/ap_2.png");
        manager.unload("images/ap_3.png");
        manager.unload("images/ap_4.png");
        manager.unload("images/ge_atgun.png");
        manager.unload("images/ge_infantry.png");
        manager.unload("images/ge_tiger.png");
        manager.unload("images/ge_kingtiger.png");
        manager.unload("images/ge_panzer_iv.png");
        manager.unload("images/ge_panzer_iv_hq.png");
        manager.unload("images/ge_wespe.png");
        manager.unload("images/us_atgun.png");
        manager.unload("images/us_infantry.png");
        manager.unload("images/us_pershing.png");
        manager.unload("images/us_pershing_hq.png");
        manager.unload("images/us_priest.png");
        manager.unload("images/us_sherman.png");
        manager.unload("images/us_sherman_hq.png");
        manager.unload("images/us_wolverine.png");
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
