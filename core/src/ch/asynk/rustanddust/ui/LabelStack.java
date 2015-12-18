package ch.asynk.rustanddust.ui;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import ch.asynk.rustanddust.engine.gfx.Animation;

public class LabelStack extends Label implements Animation
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

    private float duration;
    private float elapsed;
    private ArrayDeque<MsgInfo> stack;

    public LabelStack(BitmapFont font, float padding)
    {
        super(font, padding);
        this.visible = false;
        this.stack = new ArrayDeque<MsgInfo>();
    }

    public void pushWrite(String text, float duration, Position position)
    {
        if (visible)
            stack.push(new MsgInfo(text, duration, position));
        else
            write(text, duration, position);
    }

    public void write(String text, float duration, Position position)
    {
        this.position = position;
        write(text, duration);
    }

    public void write(String text, float duration)
    {
        this.duration = duration;
        this.visible = true;
        this.elapsed = 0f;
        write(text);
    }

    @Override
    public boolean animate(float delta)
    {
        if (!visible) return true;
        elapsed += delta;
        if (elapsed >= duration) {
           visible = false;
           if (stack.size() > 0) {
               MsgInfo info = stack.pop();
               write(info.text, info.duration, info.position);
           }
        }
        return false;
    }
}
