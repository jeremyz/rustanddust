package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.actors.HexMap;

public class GameScreen extends AbstractScreen
{
    static private final float ZOOM_MAX = 0.1f;
    static private final float ZOOM_GESTURE_FACTOR = 300.f;
    static private final float ZOOM_SCROLL_FACTOR = 10.0f;

    private float maxZoomOut;
    final OrthographicCamera cam;

    private HexMap map;
    private Label fps;
    private Label camInfo;
    private Label cellInfo;

    private Stage hud;
    private Stage gameStage;

    private Vector2 screenToViewport = new Vector2();       // ratio
    private Vector3 touchPos = new Vector3();               // world coordinates
    private Vector2 dragPos = new Vector2();                // screen coordinates

    public GameScreen(final TankOnTank game)
    {
        super(game);

        map = new HexMap(10, 8, game.manager.get("images/map_a.png", Texture.class));
        fps = new Label("FPS: 0", game.skin);
        camInfo = new Label("", game.skin);
        cellInfo = new Label("", game.skin);
        fps.setPosition( 10, Gdx.graphics.getHeight() - 40);
        camInfo.setPosition( 10, Gdx.graphics.getHeight() - 50);
        cellInfo.setPosition( 10, Gdx.graphics.getHeight() - 70);

        cam = new OrthographicCamera();
        cam.setToOrtho(false);
        // cam.position.set((map.getWidth()/2), (map.getHeight()/2), 0);

        gameStage = new Stage(new FitViewport(map.getWidth(), map.getHeight(), cam));
        gameStage.addActor(map);

        hud = new Stage(new ScreenViewport());
        hud.addActor(fps);
        hud.addActor(camInfo);
        hud.addActor(cellInfo);


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
                cam.translate(((dragPos.x - x) * cam.zoom * screenToViewport.x), ((y - dragPos.y) * cam.zoom * screenToViewport.y), 0);
                dragPos.set(x, y);
                clampCameraPos();
                return true;
            }
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    cam.unproject(touchPos.set(x, y, 0));
                    map.selectCell(touchPos.x, touchPos.y);
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

        fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        camInfo.setText("Camera: " + (int) cam.position.y + " ; " + (int) cam.position.y + " x " + String.format("%.2f", cam.zoom));
        cellInfo.setText("Cell: " + map.cell.x + " ; " + map.cell.y);

        gameStage.act(delta);
        gameStage.draw();

        hud.act(delta);
        hud.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        Gdx.app.debug("GameScreen", "resize (" + width + "," + height + ")");
        hud.getViewport().update(width, height, true);
        gameStage.getViewport().update(width, height);
        maxZoomOut = Math.min((map.getWidth() / cam.viewportWidth), (map.getHeight() / cam.viewportHeight));
        cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_MAX, maxZoomOut);
        screenToViewport.set((cam.viewportWidth / width), (cam.viewportHeight / height));
    }

    @Override
    public void dispose()
    {
        Gdx.app.debug("GameScreen", "dispose()");
        hud.dispose();
        gameStage.dispose();
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
