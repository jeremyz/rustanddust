package ch.asynk.rustanddust.screens;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.rustanddust.RustAndDust;

import ch.asynk.rustanddust.game.Ctrl;

public class GameScreen implements Screen
{
    private static boolean DEBUG = false;

    private static final boolean FIXED_HUD = true;
    private static final float INPUT_DELAY = 0.1f;
    private static final float ZOOM_IN_MAX = 0.3f;
    private static final float ZOOM_OUT_MAX = 1f;
    private static final float ZOOM_GESTURE_FACTOR = .01f;
    private static final float ZOOM_SCROLL_FACTOR = .1f;
    private static final int DRAGGED_Z_INDEX = 10;
    private static final int DRAG_THRESHOLD = 6;

    private final GameCamera cam;

    private final SpriteBatch batch;
    private ShapeRenderer debugShapes = null;

    private final RustAndDust game;
    private Ctrl ctrl;

    private boolean paused;
    private int dragged;
    private boolean blocked;
    private float inputDelay = 0f;
    private Vector2 dragPos = new Vector2();
    private Vector3 hudTouch = new Vector3();
    private Vector3 mapTouch = new Vector3();

    public GameScreen(final RustAndDust game)
    {
        DEBUG = game.config.debug;

        this.game = game;
        this.dragged = 0;
        this.blocked = false;

        this.batch = new SpriteBatch();
        this.ctrl = Ctrl.getCtrl(game);
        this.cam = new GameCamera(ctrl.map.getWidth(),  ctrl.map.getHeight(), ZOOM_OUT_MAX, ZOOM_IN_MAX, game.hudCorrection, FIXED_HUD);

        if (DEBUG) this.debugShapes = new ShapeRenderer();

        Gdx.input.setInputProcessor(getMultiplexer());

        paused = false;
    }

    private InputMultiplexer getMultiplexer()
    {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new GestureDetector(new GestureAdapter() {
            @Override
            public boolean zoom(float initialDistance, float distance)
            {
                if (initialDistance > distance)
                    cam.zoom(ZOOM_GESTURE_FACTOR);
                else
                    cam.zoom(-ZOOM_GESTURE_FACTOR);
                ctrl.hud.resize(cam.getHudLeft(), cam.getHudBottom(), cam.getHudWidth(), cam.getHudHeight());
                blocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
        }));
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int x, int y, int pointer)
            {
                dragged += 1;
                int dx = (int) (dragPos.x - x);
                int dy = (int) (dragPos.y - y);
                dragPos.set(x, y);
                cam.unprojectHud(x, y, hudTouch);
                if (!ctrl.drag(hudTouch.x, hudTouch.y, -dx, dy))
                    cam.translate(dx, dy);
                return true;
            }
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (blocked) return true;
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    cam.unproject(x, y, mapTouch);
                    cam.unprojectHud(x, y, hudTouch);
                    ctrl.touchDown(hudTouch.x, hudTouch.y, mapTouch.x, mapTouch.y);
                }
                return true;
            }
            @Override
            public boolean touchUp(int x, int y, int pointer, int button)
            {
                if (blocked) return true;
                if (dragged > DRAG_THRESHOLD) {
                    dragged = 0;
                    return true;
                }
                dragged = 0;
                if (button == Input.Buttons.LEFT) {
                    cam.unproject(x, y, mapTouch);
                    cam.unprojectHud(x, y, hudTouch);
                    ctrl.touchUp(hudTouch.x, hudTouch.y, mapTouch.x, mapTouch.y);
                }
                return true;
            }
            @Override
            public boolean scrolled(int amount)
            {
                cam.zoom(amount * ZOOM_SCROLL_FACTOR);
                ctrl.hud.resize(cam.getHudLeft(), cam.getHudBottom(), cam.getHudWidth(), cam.getHudHeight());
                return true;
            }
        });

        return multiplexer;
    }

    @Override
    public void render(float delta)
    {
        if (paused) return;

        if (inputDelay > 0f) {
            inputDelay -= delta;
            if (inputDelay <= 0f)
                blocked = false;
        }

        ctrl.processEvent(delta);
        ctrl.hud.animate(delta);
        ctrl.map.animate(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // cam.update();
        cam.applyMapViewport();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        ctrl.map.draw(batch);
        batch.end();

        if (DEBUG) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            debugShapes.setAutoShapeType(true);
            debugShapes.setProjectionMatrix(cam.combined);
            debugShapes.begin();
            ctrl.map.drawDebug(debugShapes);
            debugShapes.end();
        }

        cam.applyHudViewport();
        batch.setProjectionMatrix(cam.getHudMatrix());
        batch.begin();
        ctrl.hud.draw(batch, DEBUG);
        batch.end();

        if (DEBUG) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            debugShapes.setAutoShapeType(true);
            debugShapes.setProjectionMatrix(cam.getHudMatrix());
            debugShapes.begin();
            debugShapes.rect(cam.getHudLeft(), cam.getHudBottom(), cam.getHudWidth(), cam.getHudHeight());
            ctrl.hud.drawDebug(debugShapes);
            debugShapes.end();
        }
    }

    @Override
    public void resize(int width, int height)
    {
        if (paused) return;

        // RustAndDust.debug("GameScreen", "resize (" + width + "," + height + ")");
        cam.updateViewport(width, height);
        ctrl.hud.resize(cam.getHudLeft(), cam.getHudBottom(), cam.getHudWidth(), cam.getHudHeight());
    }

    @Override
    public void dispose()
    {
        // RustAndDust.debug("GameScreen", "dispose()");
        batch.dispose();
        ctrl.dispose();
        if (DEBUG) debugShapes.dispose();
    }

    @Override
    public void show()
    {
        // RustAndDust.debug("GameScreen", "show()");
    }

    @Override
    public void hide()
    {
        // RustAndDust.debug("GameScreen", "hide()");
    }

    @Override
    public void pause()
    {
        paused = true;
        // RustAndDust.debug("pause() ");
    }

    @Override
    public void resume()
    {
        // RustAndDust.debug("resume() ");
        paused = false;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}
