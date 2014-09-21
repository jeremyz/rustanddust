package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.tankontank.engine.Pawn;

public interface Animation extends Disposable
{
    public Pawn getPawn();

    public boolean animate(float delta);
}
