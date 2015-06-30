package ch.asynk.creepingarmor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Interpolation;

import ch.asynk.creepingarmor.CreepingArmor;
import ch.asynk.creepingarmor.ui.Position;
import ch.asynk.creepingarmor.menu.MainMenu;
import ch.asynk.creepingarmor.menu.OptionsMenu;
import ch.asynk.creepingarmor.menu.ScenariosMenu;
import ch.asynk.creepingarmor.menu.TutorialsMenu;

public class MenuScreen implements Screen
{
    private final CreepingArmor game;

    private final int OFFSET = 20;
    private final int V_WIDTH = 1600;
    private final int V_HEIGHT = 1125;
    private final int V_CENTER_X = 1000;
    private final int V_CENTER_Y = 890;

    private float percent;
    private float delay = 0.0f;
    private float dx;
    private float dy;
    private int[] xPath = { 369, 558, 747, 936, 1125, 1030, 936, 1125, 1314, 1408, 1597};
    private int[] yPath = { 565, 565, 565, 565,  565,  729, 892,  892,  892, 1056, 1056};
    private int n = xPath.length;

    private boolean ready;
    private boolean gameAssetsLoading;
    private Texture bg;

    private Sprite unit;
    private Sprite move;
    private Sprite from;
    private Sprite to;
    private Sprite geFlag;
    private Sprite usFlag;

    private MainMenu mainMenu;
    private OptionsMenu optionsMenu;
    private ScenariosMenu scenariosMenu;
    private TutorialsMenu tutorialsMenu;

    private final MenuCamera camera;
    private final SpriteBatch batch;
    private Vector3 touch = new Vector3();

    public MenuScreen(final CreepingArmor game)
    {
        this.game = game;
        this.batch = new SpriteBatch();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        this.camera = new MenuCamera(V_CENTER_X, V_CENTER_Y, V_WIDTH, V_HEIGHT, game.hudCorrection);

        this.gameAssetsLoading = false;

        this.bg = game.manager.get("data/map_a.png", Texture.class);

        this.unit = new Sprite(game.menuAtlas.findRegion("unit"));
        this.move = new Sprite(game.menuAtlas.findRegion("move"));
        this.from = new Sprite(game.menuAtlas.findRegion("from"));
        this.to = new Sprite(game.menuAtlas.findRegion("to"));
        this.usFlag = new Sprite(game.menuAtlas.findRegion("us-flag"));
        this.geFlag = new Sprite(game.menuAtlas.findRegion("ge-flag"));

        this.mainMenu = new MainMenu(game.fontB, game.uiAtlas);
        this.optionsMenu = new OptionsMenu(game, game.fontB, game.uiAtlas);
        this.scenariosMenu = new ScenariosMenu(game, game.fontB, game.uiAtlas);
        this.tutorialsMenu = new TutorialsMenu(game, game.fontB, game.uiAtlas);

        this.game.config.battle = null;

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                camera.uiUnproject(x, y, touch);
                return hit(touch.x, touch.y);
            }
        });
    }

    private boolean hit(float x, float y)
    {
        if (mainMenu.hit(x, y)) {
            mainMenu.visible = false;
            showNextMenu();
            return true;
        } else if (optionsMenu.hit(x, y)) {
            mainMenu.visible = true;
            optionsMenu.visible = false;
            return true;
        } else if (scenariosMenu.hit(x, y)) {
            mainMenu.visible = true;
            scenariosMenu.visible = false;
            if (scenariosMenu.launch)
                startLoading();
            return true;
        } else if (tutorialsMenu.hit(x, y)) {
            mainMenu.visible = true;
            tutorialsMenu.visible = false;
            return true;
        }

        return false;
    }

    private void showNextMenu()
    {
        MainMenu.Items item = mainMenu.getMenu();

        if (item == MainMenu.Items.OPTIONS)
            optionsMenu.visible = true;
        else if (item == MainMenu.Items.SCENARIOS)
            scenariosMenu.visible = true;
        else if (item == MainMenu.Items.TUTORIALS)
            tutorialsMenu.visible = true;
    }

    private void startLoading()
    {
        mainMenu.visible = false;
        game.loadGameAssets();
        gameAssetsLoading = true;
    }

    private void gameAssetsLoadingCompleted()
    {
        CreepingArmor.debug("LoadScreen", "assets ready : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        game.switchToGame();
        dispose();
    }

    @Override
    public void render(float delta)
    {
        float x = xPath[0];
        float y = yPath[0];
        if (gameAssetsLoading) {
            if (game.manager.update()) {
                delay += delta;
                if (delay >= 0.6f)
                    gameAssetsLoadingCompleted();
            }

            percent = Interpolation.linear.apply(percent, game.manager.getProgress(), 0.1f);
            int idx = (int) (percent * 10);
            float fraction = ((percent * 100 ) % 10 / 10);
            x = (xPath[idx] + ((xPath[idx + 1] - xPath[idx]) * fraction));
            y = (yPath[idx] + ((yPath[idx + 1] - yPath[idx]) * fraction));
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bg, 0, 0);
        from.draw(batch);
        to.draw(batch);
        usFlag.draw(batch);
        geFlag.draw(batch);
        for (int i = 1; i < (n - 1); i++)
            drawCentered(batch, move, xPath[i], yPath[i]);
        drawCentered(batch, unit, (int) (x + dx), (int) (y + dy));
        batch.end();

        batch.setProjectionMatrix(camera.uiCombined());
        batch.begin();
        mainMenu.draw(batch);
        optionsMenu.draw(batch);
        scenariosMenu.draw(batch);
        tutorialsMenu.draw(batch);
        batch.end();
    }

    private void drawCentered(SpriteBatch batch, TextureRegion region, int x, int y)
    {
        batch.draw(region, (x - (region.getRegionWidth() / 2f)), (y - (region.getRegionHeight() / 2f)));
    }

    private void setCenteredPosition(Sprite sprite, int x, int y)
    {
        sprite.setPosition((x - (sprite.getWidth() / 2f)), (y - (sprite.getHeight() / 2f)));
    }

    private void update(int width, int height)
    {
        camera.updateViewport(width, height);
        Position.update(camera.getHudLeft(), camera.getHudBottom(), camera.getHudWidth(), camera.getHudHeight());

        setCenteredPosition(from, xPath[0], yPath[0]);
        setCenteredPosition(to, xPath[n - 1], yPath[n - 1]);
        setCenteredPosition(usFlag, xPath[0], yPath[0]);
        setCenteredPosition(geFlag, xPath[n - 1], yPath[n - 1]);

        mainMenu.setPosition();
        optionsMenu.setPosition();
        scenariosMenu.setPosition();
        tutorialsMenu.setPosition();
    }

    @Override
    public void resize(int width, int height)
    {
        update(width, height);
    }

    @Override
    public void dispose()
    {
        mainMenu.dispose();
        optionsMenu.dispose();
        scenariosMenu.dispose();
        tutorialsMenu.dispose();
    }

    @Override
    public void show()
    {
        int width = (int) Gdx.graphics.getWidth();
        int height = (int) Gdx.graphics.getHeight();
        update(width, height);
    }

    @Override
    public void hide()
    {
        // CreepingArmor.debug("MenuScreen", "hide()");
    }

    @Override
    public void pause()
    {
        // CreepingArmor.debug("MenuScreen", "pause()");
    }

    @Override
    public void resume()
    {
        // CreepingArmor.debug("MenuScreen", "resume()");
    }
}
