package ch.asynk.rustanddust.engine.gfx;

import com.badlogic.gdx.utils.Disposable;

public interface Animation extends Disposable, Drawable
{
    public boolean animate(float delta);
}
