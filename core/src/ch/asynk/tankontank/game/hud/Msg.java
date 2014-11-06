package ch.asynk.tankontank.game.hud;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

import ch.asynk.tankontank.TankOnTank;

public class Msg extends Bg
{
    class MsgInfo
    {
        String text;
        float duration;
        Position position;
        MsgInfo(String text, float duration, Position position)
        {
            this.text = text;
            this.duration = duration;
            this.position = position;
        }
    }

    private BitmapFont font;
    private String text;
    private int padding;
    private float x;
    private float y;
    private float duration;
    private float elapsed;
    private boolean visible;
    private ArrayDeque<MsgInfo> stack;

    public Msg(BitmapFont font, TextureRegion region)
    {
        super(region);
        this.visible = false;
        this.font = font;
        this.stack = new ArrayDeque<MsgInfo>();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        font.dispose();
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public void pushWrite(String text, float duration, Position position)
    {
        if (visible) {
            stack.push(new MsgInfo(text, duration, position));
            return;
        } else
            write(text, duration, position);
    }

    public void write(String text, float duration, Position position)
    {
        TextBounds b = font.getBounds(text);
        float w = b.width + (2 * padding);
        float h = b.height + (2 * padding);
        write(text, position.getX(w), position.getY(h), duration, 10);
    }

    public void setTopLeft(float x, float y, int padding)
    {
        TextBounds b = font.getBounds("A");
        this.x = x;
        this.y =  (y - (2 * padding) - b.height);
        this.padding = padding;
    }

    public void write(String text, float duration)
    {
        this.text = text;
        this.duration = duration;
        this.visible = true;
        this.elapsed = 0f;
        TextBounds b = font.getBounds(text);
        set(x, y, (b.width + (padding * 2)), (b.height + (padding * 2)));
    }

    public void write(String text, float x, float y, float duration, int padding)
    {
        this.text = text;
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.padding = padding;
        this.visible = true;
        this.elapsed = 0f;
        TextBounds b = font.getBounds(text);
        set(x, y, (b.width + (padding * 2)), (b.height + (padding * 2)));
    }

    public void animate(float delta)
    {
        if (!visible) return;
        elapsed += delta;
        if (elapsed >= duration) {
           visible = false;
           if (stack.size() > 0) {
               MsgInfo info = stack.pop();
               TankOnTank.debug(info.text);
               write(info.text, info.duration, info.position);
           }
        }
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        font.draw(batch, text, (x + padding), (rect.y + rect.height - padding));
    }
}
