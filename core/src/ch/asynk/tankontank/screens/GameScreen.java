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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.actors.Pawn;
import ch.asynk.tankontank.actors.HexMap;
import ch.asynk.tankontank.actors.Unit;
import ch.asynk.tankontank.utils.UnitFactory;
import ch.asynk.tankontank.utils.UnitFactory.UnitType;

public class GameScreen extends AbstractScreen
{
    static private final float ZOOM_MAX = 0.2f;
    static private final float ZOOM_GESTURE_FACTOR = .01f;
    static private final float ZOOM_SCROLL_FACTOR = .1f;

    private float maxZoomOut;
    final OrthographicCamera cam;

    private HexMap map;
    private Image selectedHex;
    private Label fps;

    private Stage hud;
    private Stage gameStage;

    private Vector2 screenToViewport = new Vector2();       // ratio
    private Vector3 touchPos = new Vector3();               // world coordinates
    private Vector2 dragPos = new Vector2();                // screen coordinates

    private Pawn draggedPawn = null;
    private GridPoint2 cell = new GridPoint2(-1, -1);    // current map cell

    public GameScreen(final TankOnTank game)
    {
        super(game);

        fps = new Label("FPS: 0", game.skin);
        fps.setPosition( 10, Gdx.graphics.getHeight() - 40);

        map = new HexMap(11, 9, game.manager.get("images/map_a.png", Texture.class));
        selectedHex = new Image(game.manager.get("images/hex.png", Texture.class));
        selectedHex.setVisible(false);

        cam = new OrthographicCamera();
        cam.setToOrtho(false);
        // cam.position.set((map.getWidth()/2), (map.getHeight()/2), 0);

        gameStage = new Stage(new FitViewport(map.getWidth(), map.getHeight(), cam));
        gameStage.addActor(map);
        gameStage.addActor(selectedHex);

        UnitFactory.init(game.manager, map);
        addUnit(gameStage, UnitType.GE_AT_GUN, 1, 4, 0);
        addUnit(gameStage, UnitType.GE_INFANTRY, 2, 4, 0);
        addUnit(gameStage, UnitType.GE_KINGTIGER, 3, 4, 0);
        addUnit(gameStage, UnitType.GE_PANZER_IV, 4, 4, 0);
        addUnit(gameStage, UnitType.GE_PANZER_IV_HQ, 5, 4, 0);
        addUnit(gameStage, UnitType.GE_TIGER, 6, 4, 0);
        addUnit(gameStage, UnitType.GE_WESPE, 7, 4, 0);

        addUnit(gameStage, UnitType.US_AT_GUN, 1, 3, 0);
        addUnit(gameStage, UnitType.US_INFANTRY, 2, 3, 0);
        addUnit(gameStage, UnitType.US_PERSHING, 3, 3, 0);
        addUnit(gameStage, UnitType.US_PERSHING_HQ, 4, 3, 0);
        addUnit(gameStage, UnitType.US_PRIEST, 5, 3, 0);
        addUnit(gameStage, UnitType.US_SHERMAN, 6, 3, 0);
        addUnit(gameStage, UnitType.US_SHERMAN_HQ, 7, 3, 0);
        addUnit(gameStage, UnitType.US_WOLVERINE, 8, 3, 0);

        hud = new Stage(new ScreenViewport());
        hud.addActor(fps);

        Gdx.input.setInputProcessor(getMultiplexer());
    }

    private void addUnit(Stage stage, UnitType t, int col, int row, int angle)
    {
        Unit u = UnitFactory.getUnit(t);
        u.moveTo(col, row, angle);
        stage.addActor(u);
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
                cam.zoom = MathUtils.clamp(cam.zoom, ZOOM_MAX, maxZoomOut);
                clampCameraPos();
                return true;
            }
        }));
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int x, int y, int pointer)
            {
                float deltaX = ((x - dragPos.x) * cam.zoom * screenToViewport.x);
                float deltaY = ((dragPos.y - y) * cam.zoom * screenToViewport.y);
                dragPos.set(x, y);
                if (draggedPawn == null) {
                    cam.translate(-deltaX, -deltaY, 0);
                    clampCameraPos();
                } else {
                    draggedPawn.moveBy(deltaX, deltaY);
                    cam.unproject(touchPos.set(x, y, 0));
                    map.getCellAt(cell, touchPos.x, touchPos.y);
                    map.setImageCenterAt(selectedHex, cell);

                }
                return true;
            }
            @Override
            public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    cam.unproject(touchPos.set(x, y, 0));
                    map.getCellAt(cell, touchPos.x, touchPos.y);
                    draggedPawn = map.getTopPawnAt(cell);
                    if (draggedPawn != null) draggedPawn.setZIndex(Pawn.DRAGGED_Z_INDEX);
                    map.setImageCenterAt(selectedHex, cell);
                    selectedHex.setVisible(true);
                }
                return true;
            }
            @Override
            public boolean touchUp(int x, int y, int pointer, int button)
            {
                if (button == Input.Buttons.LEFT) {
                    cam.unproject(touchPos.set(x, y, 0));
                    if (draggedPawn != null) {
                        map.getCellAt(cell, touchPos.x, touchPos.y);
                        draggedPawn.moveTo(cell);
                    }
                    selectedHex.setVisible(false);
                }
                return true;
            }
            @Override
            public boolean scrolled(int amount)
            {
                cam.zoom += amount * ZOOM_SCROLL_FACTOR;
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
        UnitFactory.dispose();
        game.unloadAssets();
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
