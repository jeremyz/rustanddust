package ch.asynk.tankontank.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.utils.viewport.Viewport;

import ch.asynk.tankontank.TankOnTank;

public class Hud extends Stage
{
    private final TankOnTank game;
    private Label fps;

    public Hud(final TankOnTank game, Viewport viewport)
    {
        super(viewport);
        this.game = game;

        fps = new Label("FPS: 0", game.skin);
        fps.setPosition( 10, Gdx.graphics.getHeight() - 40);
        addActor(fps);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    }
}
