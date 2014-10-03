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

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ch.asynk.tankontank.TankOnTank;

import ch.asynk.tankontank.game.Hud;
import ch.asynk.tankontank.game.Map;
import ch.asynk.tankontank.game.GameCtrl;
import ch.asynk.tankontank.game.GameFactory;
import ch.asynk.tankontank.game.GameFactory.UnitType;
// TEST
import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;

public class GameScreen implements Screen
{
    private static final boolean DEBUG = false;

    private static final float ZOOM_IN_MAX = 0.2f;
    private static final float ZOOM_GESTURE_FACTOR = .01f;
    private static final float ZOOM_SCROLL_FACTOR = .1f;
    private static final int DRAGGED_Z_INDEX = 10;

    private float maxZoomOut;
    private float virtualWidth;
    private float virtualHeight;

    private final OrthographicCamera cam;
    private final FitViewport mapViewport;

    private final Batch mapBatch;
    private final ShapeRenderer debugShapes;

    private final TankOnTank game;
    private GameFactory factory;
    private Map map;
    private Hud hud;
    private GameCtrl ctrl;

    private Vector2 screenToWorld = new Vector2();          // ratio
    private Vector3 touchPos = new Vector3();               // world coordinates
    private Vector2 dragPos = new Vector2();                // screen coordinates

    private GridPoint2 cell = new GridPoint2(-1, -1);    // current map cell

    public GameScreen(final TankOnTank game)
    {
        this.game = game;

        factory = new GameFactory(game.manager);

        hud = new Hud(game, new ScreenViewport());

        map = factory.getMap(game.manager, GameFactory.MapType.MAP_A);
        virtualWidth = map.getWidth();
        virtualHeight = map.getHeight();

        mapBatch = new SpriteBatch();
        cam = new OrthographicCamera(virtualWidth, virtualHeight);
        cam.setToOrtho(false);
        mapViewport = new FitViewport(virtualWidth, virtualHeight, cam);
        mapViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        ctrl = new GameCtrl(map);

        // DEBUG
        debugShapes = new ShapeRenderer();

        // TEST
        Orientation o = Orientation.NORTH;
        addUnit(4, 7, o, UnitType.GE_AT_GUN);
        addUnit(3, 6, o, UnitType.GE_INFANTRY);
        addUnit(3, 5, o, UnitType.GE_KINGTIGER);
        addUnit(2, 4, o, UnitType.GE_PANZER_IV);
        addUnit(2, 3, o, UnitType.GE_PANZER_IV_HQ);
        addUnit(1, 2, o, UnitType.GE_TIGER);
        addUnit(1, 1, o, UnitType.GE_WESPE);

        o = Orientation.SOUTH;
        addUnit(12, 7, o, UnitType.US_AT_GUN);
        addUnit(11, 6, o, UnitType.US_INFANTRY);
        addUnit(11, 5, o, UnitType.US_PERSHING);
        addUnit(10, 4, o, UnitType.US_PERSHING_HQ);
        addUnit(10, 3, o, UnitType.US_PRIEST);
        addUnit(9, 2, o, UnitType.US_SHERMAN);
        addUnit(9, 1, o, UnitType.US_SHERMAN_HQ);
        addUnit(8, 0, o, UnitType.US_WOLVERINE);
        // TEST

        Gdx.input.setInputProcessor(getMultiplexer());
    }

    private void addUnit(int col, int row, Orientation o, UnitType t)
    {
        Pawn p = factory.getUnit(t);
        GridPoint2 coords = new GridPoint2(col, row);
        map.setPawnAt(p, coords, o);
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
                return true;
            }
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    unproject(x, y, touchPos);
                    ctrl.touchDown(touchPos.x, touchPos.y);
                }

                return true;
            }
            @Override
            public boolean touchUp(int x, int y, int pointer, int button)
            {
                if (button == Input.Buttons.LEFT) {
                    unproject(x, y, touchPos);
                    ctrl.touchUp(touchPos.x, touchPos.y);
                }
                return true;
            }
            @Override
            public boolean scrolled(int amount)
            {
                cam.zoom += amount * ZOOM_SCROLL_FACTOR;
                cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_IN_MAX, maxZoomOut);
                clampCameraPos();
                return true;
            }
        });

        return multiplexer;
    }

    private void unproject(int x, int y, Vector3 v)
    {
        cam.unproject(v.set(x, y, 0), mapViewport.getScreenX(), mapViewport.getScreenY(), mapViewport.getScreenWidth(), mapViewport.getScreenHeight());
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();

        map.animate(delta);

        mapBatch.setProjectionMatrix(cam.combined);
        mapBatch.begin();
        map.draw(mapBatch);
        mapBatch.end();

        hud.act(delta);
        hud.draw();

        if (DEBUG) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            debugShapes.setAutoShapeType(true);
            debugShapes.setProjectionMatrix(cam.combined);
            debugShapes.begin();
            map.drawDebug(debugShapes);
            debugShapes.end();
        }
    }

    @Override
    public void resize(int width, int height)
    {
        // Gdx.app.debug("GameScreen", "resize (" + width + "," + height + ")");
        mapViewport.update(width, height);

        maxZoomOut = Math.min((map.getWidth() / cam.viewportWidth), (map.getHeight() / cam.viewportHeight));
        cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_IN_MAX, maxZoomOut);

        screenToWorld.set((cam.viewportWidth / width), (cam.viewportHeight / height));
    }

    @Override
    public void dispose()
    {
        // Gdx.app.debug("GameScreen", "dispose()");
        hud.dispose();
        map.dispose();
        factory.dispose();
        game.unloadAssets();
        mapBatch.dispose();
        debugShapes.dispose();
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
