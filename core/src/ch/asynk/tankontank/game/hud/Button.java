package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Button extends Widget
{
    private int idx;
    private TextureRegion regions [];
    private TextureRegion region;

    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int ON = 2;

    public Button(TextureAtlas atlas, String base)
    {
        this.idx = UP;
        this.regions = new TextureRegion[3];
        this.regions[UP] = atlas.findRegion(base + "-up");
        this.regions[DOWN] = atlas.findRegion(base + "-down");
        this.regions[ON] = atlas.findRegion(base + "-on");
        // assumes they all have the same dimension
        rect.width = regions[idx].getRegionWidth();
        rect.height = regions[idx].getRegionHeight();
    }

    @Override
    public void dispose()
    {
    }

    public void hide()
    {
        setUp();
        visible = false;
    }

    public void setUp()
    {
        idx = UP;
    }

    public void setDown()
    {
        idx = DOWN;
    }

    public void setOn()
    {
        idx = ON;
    }

    public boolean isUp()
    {
        return (idx == UP);
    }

    public boolean isDown()
    {
        return (idx == DOWN);
    }

    public boolean isOn()
    {
        return (idx == ON);
    }

    public boolean hit(float x, float y)
    {
        if (idx == ON) return false;
        return super.hit(x,y);
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        batch.draw(regions[idx], rect.x, rect.y, rect.width, rect.height);
    }
}
