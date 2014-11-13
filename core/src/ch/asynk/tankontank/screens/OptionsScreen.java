package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.game.Battle;

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
    private CheckBox debugCk;
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
        game.config.debug = debugCk.isChecked();
        game.config.battle = scenarios.getSelected();
    }

    @Override
    public void show()
    {
        TankOnTank.debug("OptionsScreen", "show()");

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        title1 = new Label("Options", game.skin);
        okButton = new TextButton("OK", game.skin);
        showMovesCk = new CheckBox("Show Moves", game.skin);
        showTargetsCk = new CheckBox("Show Targets", game.skin);
        showMoveAssistsCk = new CheckBox("Show Moves Assists", game.skin);
        canCancelCk = new CheckBox("Can Cancel", game.skin);
        mustValidateCk = new CheckBox("Must Validate", game.skin);
        showEnemyPossibilitiesCk = new CheckBox("Show Enemy Possibilities", game.skin);
        debugCk = new CheckBox("Debug", game.skin);
        title2 = new Label("Scenarios", game.skin);
        scenarios = new List<Battle>(game.skin);
        scenarios.setItems(game.factory.battles);
        scenarios.setWidth(170);
        scenarios.setSelected(game.factory.battles[0]);

        showMovesCk.setChecked(game.config.showMoves);
        showTargetsCk.setChecked(game.config.showTargets);
        showMoveAssistsCk.setChecked(game.config.showMoveAssists);
        canCancelCk.setChecked(game.config.canCancel);
        mustValidateCk.setChecked(game.config.mustValidate);
        showEnemyPossibilitiesCk.setChecked(game.config.showEnemyPossibilities);
        debugCk.setChecked(game.config.debug);

        okButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                apply();
                game.setScreen(new GameScreen(game));
            }
        });

        stage.addActor(title1);
        stage.addActor(showMovesCk);
        stage.addActor(showTargetsCk);
        stage.addActor(showMoveAssistsCk);
        stage.addActor(canCancelCk);
        stage.addActor(mustValidateCk);
        stage.addActor(showEnemyPossibilitiesCk);
        stage.addActor(debugCk);
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
        title1.setPosition(x, y);
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
        debugCk.setPosition(x, y);
        y -= 40f;
        title2.setPosition(x, y);
        y -= scenarios.getHeight();
        scenarios.setPosition(x, y);
        x += 200f;
        y -= 40f;
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
