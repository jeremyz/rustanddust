package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import ch.asynk.rustanddust.engine.util.IterableQueue;
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
    private IterableQueue<MsgInfo> queue;

    public LabelStack(BitmapFont font, float padding)
    {
        super(font, padding);
        this.visible = false;
        this.queue = new IterableQueue<MsgInfo>(3);
    }

    public void pushWrite(String text, float duration, Position position)
    {
        if (visible)
            queue.enqueue(new MsgInfo(text, duration, position));
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
        if (!visible) return false;
        elapsed += delta;
        if (elapsed >= duration) {
           visible = false;
           if (queue.size() > 0) {
               MsgInfo info = queue.dequeue();
               write(info.text, info.duration, info.position);
               return true;
           }
        }
        return false;
    }
}
