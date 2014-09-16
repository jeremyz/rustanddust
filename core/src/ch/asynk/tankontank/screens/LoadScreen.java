package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.loading.LoadingBar;

public class LoadScreen implements Screen
{
    private final TankOnTank game;

    private Stage stage;

    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;

    private final int loadingBgWidth = 450;
    private float startX, endX;
    private float percent;

    private Actor loadingBar;

    private float delay = 0.0f;

    public LoadScreen(final TankOnTank game)
    {
        this.game = game;
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.manager.update()) {
            delay += delta;
            if (delay >= 0.2f) {
                Gdx.app.debug("LoadScreen", "assets loaded : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
                game.setScreen(new GameScreen(game));
                dispose();
            }
        }

        percent = Interpolation.linear.apply(percent, game.manager.getProgress(), 0.1f);

        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(loadingBgWidth - loadingBgWidth * percent);
        loadingBg.invalidate();

        stage.act();
        stage.draw();
    }

    @Override
    public void show()
    {
        Gdx.app.debug("LoadScreen", "show()");
        game.manager.load("loading.pack", TextureAtlas.class);
        game.manager.finishLoading();

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        TextureAtlas atlas = game.manager.get("loading.pack", TextureAtlas.class);

        // logo = new Image(atlas.findRegion("libgdx-logo"));
        logo = new Image(atlas.findRegion("tank-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        Animation anim = new Animation(0.05f, atlas.findRegions("loading-bar-anim") );
        anim.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));

        stage.addActor(screenBg);
        stage.addActor(logo);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);

        game.loadAssets();
    }

    @Override
    public void resize(int width, int height)
    {
        Gdx.app.debug("LoadScreen", "resize (" + width + "," + height + ")");

        stage.getViewport().update(width, height, true);

        screenBg.setSize(stage.getWidth(), stage.getHeight());

        logo.setX((stage.getWidth() - logo.getWidth()) / 2);
        logo.setY(Math.min((stage.getHeight() / 2), (stage.getHeight() - logo.getHeight() - 10)));

        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY(logo.getY() - loadingFrame.getHeight() - 20);

        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        startX = loadingBarHidden.getX();
        endX = loadingBgWidth - 10;

        loadingBg.setSize(loadingBgWidth, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    @Override
    public void dispose()
    {
        Gdx.app.debug("LoadScreen", "dispose()");
        stage.dispose();
    }

    @Override
    public void hide()
    {
        Gdx.app.debug("LoadScreen", "hide()");
        game.manager.unload("loading.pack");
    }

    @Override
    public void pause()
    {
        Gdx.app.debug("LoadScreen", "pause()");
    }

    @Override
    public void resume()
    {
        Gdx.app.debug("LoadScreen", "resume()");
    }
}
