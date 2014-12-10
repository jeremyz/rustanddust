package ch.asynk.tankontank.game.hud;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.engine.gfx.Animation;

public class Msg extends Label implements Animation
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

    private Patch bg;
    private float duration;
    private float elapsed;
    private ArrayDeque<MsgInfo> stack;

    public Msg(BitmapFont font, TextureAtlas atlas)
    {
        super(font, 20f);
        this.visible = false;
        this.bg = new Patch(atlas.createPatch("typewriter"));
        this.stack = new ArrayDeque<MsgInfo>();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        bg.dispose();
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
        this.duration = duration;
        this.visible = true;
        this.elapsed = 0f;
        write(text);
        setPosition(position.getX(getWidth()), position.getY(getHeight()));
        bg.set(rect);
    }

    public void write(String text, float duration)
    {
        this.duration = duration;
        this.visible = true;
        this.elapsed = 0f;
        write(text);
        bg.set(rect);
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

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;
        bg.draw(batch);
        super.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes)
    {
        if (!visible) return;
        super.drawDebug(shapes);
        bg.drawDebug(shapes);
    }
}
