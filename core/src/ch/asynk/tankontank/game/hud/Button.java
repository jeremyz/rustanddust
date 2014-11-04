package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.tankontank.engine.gfx.Image;

public class Button implements Disposable
{

    public int idx;
    public boolean blocked;
    public boolean visible;
    private Image images [];
    private Image image;
    private Rectangle rect;

    private static final int OFF = 0;
    private static final int ON = 1;
    private static final int DOWN = 2;

    public Button(TextureAtlas atlas, String base)
    {
        this.idx = OFF;
        this.blocked = false;
        this.visible = false;
        this.images = new Image[3];
        this.images[OFF] = new Image(atlas.findRegion(base + "-off"));
        this.images[ON] = new Image(atlas.findRegion(base + "-on"));
        this.images[DOWN] = new Image(atlas.findRegion(base + "-down"));

        this.rect = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void dispose()
    {
        for (Image image : images)
            image.dispose();
    }

    public void hide()
    {
        idx = OFF;
        visible = false;
    }

    public void setOff()
    {
        idx = OFF;
    }

    public void setOn()
    {
        idx = ON;
    }

    public void setDown()
    {
        idx = DOWN;
    }

    public boolean isOn()
    {
        return (idx == ON);
    }

    public boolean isOff()
    {
        return (idx == OFF);
    }

    public boolean isDisabled()
    {
        return (idx == DOWN);
    }

    public Image getImage()
    {
        return images[idx];
    }

    public void setPosition(float x, float y)
    {
        for (Image image : images)
            image.setPosition(x, y);
        rect.set(x, y, getWidth(), getHeight());
    }

    public boolean hit(float x, float y)
    {
        if (blocked || !visible || (idx == ON)) return false;
        return rect.contains(x,y);
    }

    public float getX() { return images[0].getX(); }
    public float getY() { return images[0].getY(); }
    public float getWidth() { return images[0].getWidth(); }
    public float getHeight() { return images[0].getHeight(); }

    public void draw(Batch batch)
    {
        if (!visible) return;
        getImage().draw(batch);
    }
}
