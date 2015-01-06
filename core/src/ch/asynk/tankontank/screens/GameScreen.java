package ch.asynk.tankontank.screens;

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

import ch.asynk.tankontank.TankOnTank;

import ch.asynk.tankontank.game.Ctrl;

public class GameScreen implements Screen
{
    private static boolean DEBUG = false;

    private static final float INPUT_DELAY = 0.1f;
    private static final float ZOOM_IN_MAX = 0.3f;
    private static final float ZOOM_OUT_MAX = 1f;
    private static final float ZOOM_GESTURE_FACTOR = .01f;
    private static final float ZOOM_SCROLL_FACTOR = .1f;
    private static final int DRAGGED_Z_INDEX = 10;

    private final GameCamera cam;

    private final SpriteBatch batch;
    private ShapeRenderer debugShapes = null;

    private final TankOnTank game;
    private Ctrl ctrl;

    private boolean blocked;
    private float inputDelay = 0f;
    private Vector2 dragPos = new Vector2();

    public GameScreen(final TankOnTank game)
    {
        DEBUG = game.config.debug;

        this.game = game;
        this.blocked = false;

        this.batch = new SpriteBatch();
        this.ctrl = new Ctrl(game, game.config.battle);
        this.cam = new GameCamera(ctrl.map.getWidth(),  ctrl.map.getHeight(), ZOOM_OUT_MAX, ZOOM_IN_MAX);

        if (DEBUG) this.debugShapes = new ShapeRenderer();

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
                    cam.zoom(ZOOM_GESTURE_FACTOR);
                else
                    cam.zoom(-ZOOM_GESTURE_FACTOR);
                blocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
        }));
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int x, int y, int pointer)
            {
                cam.translate((dragPos.x - x), (dragPos.y - y));
                dragPos.set(x, y);
                return true;
            }
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (blocked) return true;
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    cam.unproject(x, y, ctrl.mapTouch);
                    cam.unprojectHud(x, y, ctrl.hudTouch);
                    ctrl.touchDown();
                }
                return true;
            }
            @Override
            public boolean touchUp(int x, int y, int pointer, int button)
            {
                if (blocked) return true;
                if (button == Input.Buttons.LEFT) {
                    cam.unproject(x, y, ctrl.mapTouch);
                    cam.unprojectHud(x, y, ctrl.hudTouch);
                    ctrl.touchUp();
                }
                return true;
            }
            @Override
            public boolean scrolled(int amount)
            {
                cam.zoom(amount * ZOOM_SCROLL_FACTOR);
                return true;
            }
        });

        return multiplexer;
    }

    @Override
    public void render(float delta)
    {
        if (inputDelay > 0f) {
            inputDelay -= delta;
            if (inputDelay <= 0f)
                blocked = false;
        }

        ctrl.hud.animate(delta);
        ctrl.map.animate(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // cam.update();
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

        batch.setProjectionMatrix(cam.getHudMatrix());
        batch.begin();
        ctrl.hud.draw(batch, DEBUG);
        batch.end();

        if (DEBUG) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            debugShapes.setAutoShapeType(true);
            debugShapes.setProjectionMatrix(cam.getHudMatrix());
            debugShapes.begin();
            ctrl.hud.drawDebug(debugShapes);
            debugShapes.end();
        }
    }

    @Override
    public void resize(int width, int height)
    {
        // TankOnTank.debug("GameScreen", "resize (" + width + "," + height + ")");
        cam.updateViewport(width, height);
        ctrl.hud.resize(cam.getHudWidth(), cam.getHudHeight());
    }

    @Override
    public void dispose()
    {
        // TankOnTank.debug("GameScreen", "dispose()");
        batch.dispose();
        ctrl.dispose();
        if (DEBUG) debugShapes.dispose();
    }

    @Override
    public void show()
    {
        // TankOnTank.debug("GameScreen", "show()");
    }

    @Override
    public void hide()
    {
        // TankOnTank.debug("GameScreen", "hide()");
    }

    @Override
    public void pause()
    {
        // TankOnTank.debug("GameScreen", "pause()");
    }

    @Override
    public void resume()
    {
        // TankOnTank.debug("GameScreen", "resume()");
    }
}
