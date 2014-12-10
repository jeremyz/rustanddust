package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.viewport.FitViewport;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.loading.LoadingBar;

public class LoadScreen implements Screen
{
    private final TankOnTank game;

    private float percent;
    private float delay = 0.0f;
    private float[] xPath = { 68, 164, 260, 356, 452, 404, 356, 452, 548, 596, 692};
    private float[] yPath = { 148,148, 148, 148, 148, 231, 314, 314, 314, 397, 397};

    private boolean ready;
    private boolean assetsLoaded;
    private Texture bg;
    private Texture unit;
    private float dx;
    private float dy;

    private final Camera camera;
    private final SpriteBatch batch;
    private final FitViewport viewport;

    public LoadScreen(final TankOnTank game)
    {
        this.game = game;
        this.batch = new SpriteBatch();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        this.camera = new OrthographicCamera(width, height);
        this.viewport = new FitViewport(width, height, camera);
        this.ready = false;
        this.assetsLoaded = false;
        this.dx = ((width - 800) / 2f);
        this.dy = ((height - 600) / 4f);
    }

    @Override
    public void render(float delta)
    {
        if (game.manager.update()) {
            delay += delta;
            if (delay >= 0.6f)
                assetsLoadingCompleted();
        }

        if (!ready) return;

        percent = Interpolation.linear.apply(percent, game.manager.getProgress(), 0.1f);
        int idx = (int) (percent * 10);
        float fraction = ((percent * 100 ) % 10 / 10);
        float x = (xPath[idx] + ((xPath[idx + 1] - xPath[idx]) * fraction));
        float y = (yPath[idx] + ((yPath[idx + 1] - yPath[idx]) * fraction));

        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bg, dx, dy);
        batch.draw(unit, (x + dx), (y + dy));
        batch.end();
    }

    @Override
    public void show()
    {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        load();
        game.loadAssets();
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose()
    {
        unload();
    }

    @Override
    public void hide()
    {
        unload();
    }

    @Override
    public void pause()
    {
        unload();
    }

    @Override
    public void resume()
    {
        load();
    }

    private void load()
    {
        game.manager.load("data/unit.png", Texture.class);
        game.manager.load("data/loading.png", Texture.class);
        game.manager.finishLoading();
        bg = game.manager.get("data/loading.png", Texture.class);
        unit = game.manager.get("data/unit.png", Texture.class);
        ready = true;
    }

    private void unload()
    {
        if (!ready) return;
        bg.dispose();
        unit.dispose();
        game.manager.unload("data/loading.png");
        game.manager.unload("data/unit.png");
        ready = false;
    }

    private void assetsLoadingCompleted()
    {
        if (assetsLoaded) return;
        assetsLoaded = true;
        TankOnTank.debug("LoadScreen", "assets ready : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        game.onLoaded();
        game.setScreen(new OptionsScreen(game));
        dispose();
    }
}
