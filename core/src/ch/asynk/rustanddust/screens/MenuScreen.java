package ch.asynk.rustanddust.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Interpolation;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Label;
import ch.asynk.rustanddust.ui.Position;
import ch.asynk.rustanddust.ui.Widget;
import ch.asynk.rustanddust.menu.MainMenu;
import ch.asynk.rustanddust.menu.PlayMenu;
import ch.asynk.rustanddust.menu.OptionsMenu;
import ch.asynk.rustanddust.menu.TutorialsMenu;

public class MenuScreen implements Screen
{
    private final RustAndDust game;

    private final int V_WIDTH = 1600;
    private final int V_HEIGHT = 1125;
    private final int V_CENTER_X = 1000;
    private final int V_CENTER_Y = 890;

    private float percent;
    private float delay = 0.0f;
    private float dx;
    private float dy;
    private int[] xPath = { 907, 812, 908, 1098, 1288, 1384, 1481, 1578};
    private int[] yPath = { 491, 653, 818, 818, 818, 984, 1150, 1316};

    private int n = xPath.length;

    private boolean paused;
    private boolean ready;
    private boolean gameAssetsLoading;
    private Texture bg;

    private Sprite unit;
    private Sprite move;
    private Sprite from;
    private Sprite to;
    private Sprite geFlag;
    private Sprite usFlag;

    private Label versionLabel;
    private MainMenu mainMenu;
    private PlayMenu playMenu;
    private OptionsMenu optionsMenu;
    private TutorialsMenu tutorialsMenu;
    private Widget currentMenu;

    private final MenuCamera camera;
    private final SpriteBatch batch;
    private Vector3 touch = new Vector3();

    public MenuScreen(final RustAndDust game)
    {
        this.game = game;
        this.batch = new SpriteBatch();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        this.camera = new MenuCamera(V_CENTER_X, V_CENTER_Y, V_WIDTH, V_HEIGHT, game.hudCorrection);

        this.paused = false;
        this.gameAssetsLoading = false;

        this.bg = game.manager.get(game.PNG_MAP_00, Texture.class);

        this.unit = new Sprite(game.getUiRegion(game.UI_UNIT));
        this.move = new Sprite(game.getUiRegion(game.UI_MOVE));
        this.from = new Sprite(game.getUiRegion(game.UI_FROM));
        this.to = new Sprite(game.getUiRegion(game.UI_TO));
        this.usFlag = new Sprite(game.getUiRegion(game.UI_US_FLAG));
        this.geFlag = new Sprite(game.getUiRegion(game.UI_GE_FLAG));

        this.versionLabel = new Label(game.font);
        this.versionLabel.write("v21");
        this.mainMenu = new MainMenu(game);
        this.playMenu = new PlayMenu(game);
        this.optionsMenu = new OptionsMenu(game);
        this.tutorialsMenu = new TutorialsMenu(game);
        this.gamesMenu = new GamesMenu(game);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int x, int y, int pointer)
            {
                int dx = (int) (dragPos.x - x);
                int dy = (int) (dragPos.y - y);
                dragPos.set(x, y);
                camera.uiUnproject(x, y, touch);
                // return drag(touch.x, touch.y, -dx, dy);
                return false;
            }
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                dragPos.set(x, y);
                camera.uiUnproject(x, y, touch);
                return hit(touch.x, touch.y);
            }
        });

        currentMenu = mainMenu;
        currentMenu.visible = true;
    }

    private boolean hit(float x, float y)
    {
        if (currentMenu.hit(x, y)) {
            currentMenu.visible = false;
            if (currentMenu == mainMenu) {
                showNextMenu();
            } else if (currentMenu == playMenu) {
                currentMenu = mainMenu;
                if (playMenu.launch)
                    startLoading();
            } else {
                currentMenu = mainMenu;
            }
            currentMenu.visible = true;
            return true;
        }

        return false;
    }

    private void showNextMenu()
    {
        switch(mainMenu.getMenu()) {
            case PLAY: currentMenu = playMenu; break;
            case OPTIONS: currentMenu = optionsMenu; break;
            case TUTORIALS: currentMenu = tutorialsMenu; break;
        }
    }

    private void startLoading()
    {
        mainMenu.visible = false;
        game.loadGameAssets();
        gameAssetsLoading = true;
    }

    private void gameAssetsLoadingCompleted()
    {
        RustAndDust.debug("LoadScreen", "assets ready : " + (Gdx.app.getJavaHeap()/1024.0f) + "KB");
        game.switchToGame();
        dispose();
    }

    @Override
    public void render(float delta)
    {
        if (paused) return;

        float x = xPath[0];
        float y = yPath[0];
        if (gameAssetsLoading) {
            if (game.manager.update()) {
                delay += delta;
                if (delay >= 0.6f)
                    gameAssetsLoadingCompleted();
            }

            percent = Interpolation.linear.apply(percent, game.manager.getProgress(), 0.1f);
            float p = (percent * (xPath.length - 1));
            int idx = (int) p;
            float fraction = (p - idx);
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
        versionLabel.draw(batch);
        batch.end();

        batch.setProjectionMatrix(camera.uiCombined());
        batch.begin();
        mainMenu.draw(batch);
        playMenu.draw(batch);
        optionsMenu.draw(batch);
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

        versionLabel.setPosition(xPath[0] - 190, yPath[0]);
        mainMenu.setPosition();
        playMenu.setPosition();
        optionsMenu.setPosition();
        tutorialsMenu.setPosition();
    }

    @Override
    public void resize(int width, int height)
    {
        if (paused) return;

        update(width, height);
    }

    @Override
    public void dispose()
    {
        versionLabel.dispose();
        mainMenu.dispose();
        playMenu.dispose();
        optionsMenu.dispose();
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
        // RustAndDust.debug("MenuScreen", "hide()");
    }

    @Override
    public void pause()
    {
        paused = true;
    }

    @Override
    public void resume()
    {
        paused = false;
    }
}
