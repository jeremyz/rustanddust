package ch.asynk.tankontank.engine.gfx;

import com.badlogic.gdx.utils.Disposable;

public interface Animation extends Disposable
{
    public Node getNode();

    public boolean act(float delta);
}
