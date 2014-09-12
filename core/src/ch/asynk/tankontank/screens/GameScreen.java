package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

import ch.asynk.tankontank.TankOnTank;

public class GameScreen extends AbstractScreen
{
    static private final int MOVE_STEP = 3;
    static private final float ZOOM_MAX = 0.2f;
    static private final float ZOOM_GESTURE_FACTOR = 300.f;
    static private final float ZOOM_SCROLL_FACTOR = 10.0f;

    private OrthographicCamera cam;
    private FitViewport viewport;

    private SpriteBatch batch;
    private BitmapFont font;

    private Sprite mapSprite;

    private int touchX;
    private int touchY;
    private float maxZoomOut;

    public GameScreen(final TankOnTank game)
    {
        super(game);

        batch = new SpriteBatch();
        font = new BitmapFont();

        final Texture mapTexture = game.manager.get("images/map_a.png", Texture.class);
        mapSprite = new Sprite(mapTexture);
        mapSprite.setPosition(0, 0);
        mapSprite.setSize(mapTexture.getWidth(), mapTexture.getHeight());

        cam = new OrthographicCamera();
        cam.position.set((mapSprite.getWidth()/2), (mapSprite.getHeight()/2), 0);
        cam.zoom = 2f;

        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cam);

        Gdx.input.setInputProcessor(getMultiplexer());
    }

    private InputMultiplexer getMultiplexer()
    {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new GestureDetector(new GestureAdapter() {
            @Override
            public boolean zoom(float initialDistance, float distance)
            {
                cam.zoom += ((initialDistance - distance) / ZOOM_GESTURE_FACTOR);
                cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_MAX, maxZoomOut);
                clampCameraPos();
                return true;
            }
        }));
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int x, int y, int pointer)
            {
                cam.translate((touchX - x) * cam.zoom * MOVE_STEP, (y - touchY) * cam.zoom * MOVE_STEP, 0);
                touchX = x;
                touchY = y;
                clampCameraPos();
                return true;
            }
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (button == Input.Buttons.LEFT) {
                    touchX = x;
                    touchY = y;
                }
                return true;
            }
            @Override
            public boolean scrolled(int amount)
            {
                cam.zoom += amount / ZOOM_SCROLL_FACTOR;
                cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_MAX, maxZoomOut);
                clampCameraPos();
                return true;
            }
        });

        return multiplexer;
    }

    private void clampCameraPos()
    {
        float cx = cam.viewportWidth * cam.zoom / 2f;
        float cy = cam.viewportHeight * cam.zoom / 2f;
        cam.position.x = MathUtils.clamp(cam.position.x, cx, (map.getWidth() - cx));
        cam.position.y = MathUtils.clamp(cam.position.y, cy, (map.getHeight() - cy));
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();

        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        mapSprite.draw(batch);
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 20, 30);
        batch.end();

        if(Gdx.input.isTouched()) {
            // TODO
        }
    }

    @Override
    public void resize(int width, int height)
    {
        Gdx.app.debug("GameScreen", "resize (" + width + "," + height + ")");
        viewport.update(width, height);
        maxZoomOut = Math.min((map.getWidth() / cam.viewportWidth), (map.getHeight() / cam.viewportHeight));
        cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_MAX, maxZoomOut);
    }

    @Override
    public void dispose()
    {
        Gdx.app.debug("GameScreen", "dispose()");
        batch.dispose();
        font.dispose();
    }

    @Override
    public void show()
    {
        Gdx.app.debug("GameScreen", "show()");
    }

    @Override
    public void hide()
    {
        Gdx.app.debug("GameScreen", "hide()");
    }

    @Override
    public void pause()
    {
        Gdx.app.debug("GameScreen", "pause()");
    }

    @Override
    public void resume()
    {
        Gdx.app.debug("GameScreen", "resume()");
    }
}
