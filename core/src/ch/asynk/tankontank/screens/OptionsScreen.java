package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import ch.asynk.tankontank.TankOnTank;

public class OptionsScreen implements Screen
{
    private final TankOnTank game;

    private Stage stage;
    private Label title;
    private TextButton okButton;
    private CheckBox showMovesCk;
    private CheckBox showTargetsCk;
    private CheckBox showMoveAssistsCk;
    private CheckBox canCancelCk;
    private CheckBox mustValidateCk;
    private CheckBox showEnemyPossibilitiesCk;

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
    }

    @Override
    public void show()
    {
        Gdx.app.debug("OptionsScreen", "show()");

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        title = new Label("Options", game.skin);
        okButton = new TextButton("OK", game.skin);
        showMovesCk = new CheckBox("Show Moves", game.skin);
        showTargetsCk = new CheckBox("Show Targets", game.skin);
        showMoveAssistsCk = new CheckBox("Show Moves Assists", game.skin);
        canCancelCk = new CheckBox("Can Cancel", game.skin);
        mustValidateCk = new CheckBox("Must Validate", game.skin);
        showEnemyPossibilitiesCk = new CheckBox("Show Enemy Possibilities", game.skin);

        showMovesCk.setChecked(game.config.showMoves);
        showTargetsCk.setChecked(game.config.showTargets);
        showMoveAssistsCk.setChecked(game.config.showMoveAssists);
        canCancelCk.setChecked(game.config.canCancel);
        mustValidateCk.setChecked(game.config.mustValidate);
        showEnemyPossibilitiesCk.setChecked(game.config.showEnemyPossibilities);

        okButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                apply();
                game.setScreen(new GameScreen(game));
            }
        });

        stage.addActor(title);
        stage.addActor(showMovesCk);
        stage.addActor(showTargetsCk);
        stage.addActor(showMoveAssistsCk);
        stage.addActor(canCancelCk);
        stage.addActor(mustValidateCk);
        stage.addActor(showEnemyPossibilitiesCk);
        stage.addActor(okButton);
    }

    @Override
    public void resize(int width, int height)
    {
        // Gdx.app.debug("OptionsScreen", "resize (" + width + "," + height + ")");

        stage.getViewport().update(width, height, true);

        float x = ((width / 2) - 100f);
        float y = (height - 100f);
        title.setPosition(x, y);
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
        x += 200f;
        y -= 40f;
        okButton.setPosition(x, y);
    }

    @Override
    public void dispose()
    {
        // Gdx.app.debug("LoadScreen", "dispose()");
        stage.dispose();
    }

    @Override
    public void hide()
    {
        // Gdx.app.debug("LoadScreen", "hide()");
    }

    @Override
    public void pause()
    {
        // Gdx.app.debug("LoadScreen", "pause()");
    }

    @Override
    public void resume()
    {
        // Gdx.app.debug("LoadScreen", "resume()");
    }
}
