package ch.asynk.tankontank.screens;

import com.badlogic.gdx.Screen;

import ch.asynk.tankontank.TankOnTank;

public abstract class AbstractScreen implements Screen
{

    protected final TankOnTank game;

    public AbstractScreen(TankOnTank game)
    {
        this.game = game;
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() { }
}
