package ch.asynk.tankontank.engine.gfx;

import ch.asynk.tankontank.engine.Faction;

public interface Moveable extends Drawable
{
    public float getX();
    public float getY();
    public float getWidth();
    public float getHeight();
    public float getRotation();
    public void setPosition(float x, float y);
    public void setPosition(float x, float y, float r);
    public Faction getFaction();
}
