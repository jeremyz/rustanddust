package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.Battle;

class MyList extends List<Battle>
{
    public MyList(Skin skin, Battle... items)
    {
        super(skin);
        setItems(items);
        layout();
        setSize(getPrefWidth(), getPrefHeight());
    }
}

public class OptionsScreen implements Screen
{
    private final TankOnTank game;

    private Stage stage;
    private Label title1;
    private TextButton okButton;
    private CheckBox showMovesCk;
    private CheckBox showTargetsCk;
    private CheckBox showMoveAssistsCk;
    private CheckBox canCancelCk;
    private CheckBox mustValidateCk;
    private CheckBox showEnemyPossibilitiesCk;
    private CheckBox regularPawnsCk;
    private CheckBox debugCk;
    private Label fxLabel;
    private Label fxValue;
    private Slider fxVolume;
    private Label title2;
    private List<Battle> scenarios;

    public OptionsScreen(final TankOnTank game)
    {
        this.game = game;
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    private void apply()
    {
        game.config.showMoves = showMovesCk.isChecked();
        game.config.showTargets = showTargetsCk.isChecked();
        game.config.showMoveAssists = showMoveAssistsCk.isChecked();
        game.config.canCancel = canCancelCk.isChecked();
        game.config.mustValidate = mustValidateCk.isChecked();
        game.config.showEnemyPossibilities = showEnemyPossibilitiesCk.isChecked();
        game.config.regularPawns = regularPawnsCk.isChecked();
        game.config.debug = debugCk.isChecked();
        game.config.fxVolume = fxVolume.getValue();
        game.config.battle = scenarios.getSelected();
    }

    @Override
    public void show()
    {
        TankOnTank.debug("OptionsScreen", "show()");

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        title1 = new Label("Options", skin);
        okButton = new TextButton("OK", skin);
        showMovesCk = new CheckBox("Show Moves", skin);
        showTargetsCk = new CheckBox("Show Targets", skin);
        showMoveAssistsCk = new CheckBox("Show Moves Assists", skin);
        canCancelCk = new CheckBox("Can Cancel", skin);
        mustValidateCk = new CheckBox("Must Validate", skin);
        showEnemyPossibilitiesCk = new CheckBox("Show Enemy Possibilities", skin);
        regularPawnsCk = new CheckBox("Use Reqular Pawns", skin);
        debugCk = new CheckBox("Debug", skin);
        fxLabel = new Label("FX volume", skin);
        fxValue = new Label(String.format("%.1f", game.config.fxVolume), skin);
        fxVolume = new Slider(0f, 1f, 0.1f, false, skin) ;
        title2 = new Label("Scenarios", skin);
        scenarios = new MyList(skin, game.factory.battles);

        showMovesCk.setChecked(game.config.showMoves);
        showTargetsCk.setChecked(game.config.showTargets);
        showMoveAssistsCk.setChecked(game.config.showMoveAssists);
        canCancelCk.setChecked(game.config.canCancel);
        mustValidateCk.setChecked(game.config.mustValidate);
        showEnemyPossibilitiesCk.setChecked(game.config.showEnemyPossibilities);
        regularPawnsCk.setChecked(game.config.regularPawns);
        debugCk.setChecked(game.config.debug);
        fxVolume.setValue(game.config.fxVolume);

        okButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                apply();
                game.setScreen(new GameScreen(game));
            }
        });

        fxVolume.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                fxValue.setText(String.format("%.1f", fxVolume.getValue()));
            }
        });

        stage.addActor(title1);
        stage.addActor(showMovesCk);
        stage.addActor(showTargetsCk);
        stage.addActor(showMoveAssistsCk);
        stage.addActor(canCancelCk);
        stage.addActor(mustValidateCk);
        stage.addActor(showEnemyPossibilitiesCk);
        stage.addActor(regularPawnsCk);
        stage.addActor(debugCk);
        stage.addActor(fxLabel);
        stage.addActor(fxValue);
        stage.addActor(fxVolume);
        stage.addActor(okButton);
        stage.addActor(title2);
        stage.addActor(scenarios);
    }

    @Override
    public void resize(int width, int height)
    {
        // TankOnTank.debug("OptionsScreen", "resize (" + width + "," + height + ")");

        stage.getViewport().update(width, height, true);

        float x = ((width / 2) - 100f);
        float y = (height - 100f);
        title1.setPosition((x - 20f), y);
        y -= 20f;
        showMovesCk.setPosition(x, y);
        y -= 20f;
        showTargetsCk.setPosition(x, y);
        y -= 20f;
        showMoveAssistsCk.setPosition(x, y);
        y -= 20f;
        canCancelCk.setPosition(x, y);
        y -= 20f;
        mustValidateCk.setPosition(x, y);
        y -= 20f;
        showEnemyPossibilitiesCk.setPosition(x, y);
        y -= 20f;
        regularPawnsCk.setPosition(x, y);
        y -= 20f;
        debugCk.setPosition(x, y);
        y -= 20f;
        fxLabel.setPosition(x, y);
        fxVolume.setPosition((x + fxLabel.getWidth() + 10), y);
        fxValue.setPosition((fxVolume.getX() + fxVolume.getWidth() + 10), y);
        y -= 40f;
        title2.setPosition((x - 20f), y);
        y -= scenarios.getHeight();
        scenarios.setPosition(x, y);
        y -= 20f;
        x += 200f;
        okButton.setPosition(x, y);
    }

    @Override
    public void dispose()
    {
        // TankOnTank.debug("LoadScreen", "dispose()");
        stage.dispose();
    }

    @Override
    public void hide()
    {
        // TankOnTank.debug("LoadScreen", "hide()");
    }

    @Override
    public void pause()
    {
        // TankOnTank.debug("LoadScreen", "pause()");
    }

    @Override
    public void resume()
    {
        // TankOnTank.debug("LoadScreen", "resume()");
    }
}
