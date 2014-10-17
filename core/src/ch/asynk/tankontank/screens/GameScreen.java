package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ch.asynk.tankontank.TankOnTank;

import ch.asynk.tankontank.game.Ctrl;

public class GameScreen implements Screen
{
    private static final boolean DEBUG = false;

    private static final float INPUT_DELAY = 0.1f;
    private static final float ZOOM_IN_MAX = 0.2f;
    private static final float ZOOM_GESTURE_FACTOR = .01f;
    private static final float ZOOM_SCROLL_FACTOR = .1f;
    private static final int DRAGGED_Z_INDEX = 10;

    private float maxZoomOut;
    private float virtualWidth;
    private float virtualHeight;

    private final OrthographicCamera cam;
    private final FitViewport mapViewport;
    private final ScreenViewport hudViewport;

    private final Batch mapBatch;
    private final Batch hudBatch;
    private ShapeRenderer debugShapes = null;

    private final TankOnTank game;
    private Ctrl ctrl;

    private boolean blocked;
    private float inputDelay = 0f;
    private Vector2 dragPos = new Vector2();
    private Vector3 touchPos = new Vector3();
    private Vector2 screenToWorld = new Vector2();

    public GameScreen(final TankOnTank game)
    {
        this.game = game;

        this.ctrl = new Ctrl(game);

        virtualWidth = ctrl.map.getWidth();
        virtualHeight = ctrl.map.getHeight();

        cam = new OrthographicCamera(virtualWidth, virtualHeight);
        cam.setToOrtho(false);
        mapViewport = new FitViewport(virtualWidth, virtualHeight, cam);
        mapViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        hudViewport = new  ScreenViewport();
        hudViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        mapBatch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        if (DEBUG) debugShapes = new ShapeRenderer();

        Gdx.input.setInputProcessor(getMultiplexer());
    }


    private InputMultiplexer getMultiplexer()
    {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new GestureDetector(new GestureAdapter() {
            @Override
            public boolean zoom(float initialDistance, float distance)
            {
                if (initialDistance > distance)
                    cam.zoom += ZOOM_GESTURE_FACTOR;
                else
                    cam.zoom -= ZOOM_GESTURE_FACTOR;
                cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_IN_MAX, maxZoomOut);
                clampCameraPos();
                blocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
        }));
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int x, int y, int pointer)
            {
                float deltaX = ((x - dragPos.x) * cam.zoom * screenToWorld.x);
                float deltaY = ((dragPos.y - y) * cam.zoom * screenToWorld.y);
                dragPos.set(x, y);
                cam.translate(-deltaX, -deltaY, 0);
                clampCameraPos();
                blocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (inputDelay > 0f) return true;
                blocked = false;
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    if (ctrl.mayProcessTouch()) {
                        unprojectToHud(x, y, touchPos);
                        if (!ctrl.hud.touchDown(touchPos.x, touchPos.y)) {
                            unprojectToMap(x, y, touchPos);
                            ctrl.touchDown(touchPos.x, touchPos.y);
                        }
                    }
                }
                return true;
            }
            @Override
            public boolean touchUp(int x, int y, int pointer, int button)
            {
                if (blocked) return true;
                if (button == Input.Buttons.LEFT) {
                    if (ctrl.mayProcessTouch()) {
                        unprojectToHud(x, y, touchPos);
                        if (!ctrl.hud.touchUp(touchPos.x, touchPos.y)) {
                            unprojectToMap(x, y, touchPos);
                            ctrl.touchUp(touchPos.x, touchPos.y);
                        }
                    }
                }
                blocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
            @Override
            public boolean scrolled(int amount)
            {
                cam.zoom += amount * ZOOM_SCROLL_FACTOR;
                cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_IN_MAX, maxZoomOut);
                clampCameraPos();
                blocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
        });

        return multiplexer;
    }

    private void unprojectToMap(int x, int y, Vector3 v)
    {
        cam.unproject(v.set(x, y, 0), mapViewport.getScreenX(), mapViewport.getScreenY(),
                mapViewport.getScreenWidth(), mapViewport.getScreenHeight());
    }

    private void unprojectToHud(int x, int y, Vector3 v)
    {
        x -= mapViewport.getLeftGutterWidth();
        y += mapViewport.getBottomGutterHeight();
        hudViewport.getCamera().unproject(v.set(x, y, 0), hudViewport.getScreenX(), hudViewport.getScreenY(),
                mapViewport.getScreenWidth(), mapViewport.getScreenHeight());
    }

    private void clampCameraPos()
    {
        float cx = cam.viewportWidth * cam.zoom / 2f;
        float cy = cam.viewportHeight * cam.zoom / 2f;
        cam.position.x = MathUtils.clamp(cam.position.x, cx, (virtualWidth - cx));
        cam.position.y = MathUtils.clamp(cam.position.y, cy, (virtualHeight - cy));
    }

    @Override
    public void render(float delta)
    {
        if (inputDelay > 0f)
            inputDelay -= delta;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();

        ctrl.hud.animate(delta);
        ctrl.map.animate(delta);

        mapBatch.setProjectionMatrix(cam.combined);
        mapBatch.begin();
        ctrl.map.draw(mapBatch);
        mapBatch.end();

        if (DEBUG) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            debugShapes.setAutoShapeType(true);
            debugShapes.setProjectionMatrix(cam.combined);
            debugShapes.begin();
            ctrl.map.drawDebug(debugShapes);
            debugShapes.end();
        }

        Camera hudCam = hudViewport.getCamera();
        hudCam.update();
        hudBatch.setProjectionMatrix(hudCam.combined);
        hudBatch.begin();
        ctrl.hud.draw(hudBatch);
        hudBatch.end();
    }

    @Override
    public void resize(int width, int height)
    {
        // Gdx.app.debug("GameScreen", "resize (" + width + "," + height + ")");
        mapViewport.update(width, height);
        // hudViewport.update(width, height);

        maxZoomOut = Math.min((virtualWidth / cam.viewportWidth), (virtualHeight / cam.viewportHeight));
        cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_IN_MAX, maxZoomOut);

        screenToWorld.set((cam.viewportWidth / width), (cam.viewportHeight / height));
    }

    @Override
    public void dispose()
    {
        // Gdx.app.debug("GameScreen", "dispose()");
        mapBatch.dispose();
        hudBatch.dispose();
        ctrl.dispose();
        if (DEBUG) debugShapes.dispose();
    }

    @Override
    public void show()
    {
        // Gdx.app.debug("GameScreen", "show()");
    }

    @Override
    public void hide()
    {
        // Gdx.app.debug("GameScreen", "hide()");
    }

    @Override
    public void pause()
    {
        // Gdx.app.debug("GameScreen", "pause()");
    }

    @Override
    public void resume()
    {
        // Gdx.app.debug("GameScreen", "resume()");
    }
}
