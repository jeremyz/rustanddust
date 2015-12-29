package ch.asynk.rustanddust.engine.gfx;

import ch.asynk.rustanddust.engine.Faction;

public interface Moveable extends Drawable
{
    public void setAlpha(float alpha);
    public boolean canAim();
    public float getX();
    public float getY();
    public float getWidth();
    public float getHeight();
    public float getRotation();
    public float getTurretRotation();
    public void setPosition(float x, float y);
    public void setPosition(float x, float y, float r);
    public void setTurretRotation(float r);
    public Faction getFaction();
}
