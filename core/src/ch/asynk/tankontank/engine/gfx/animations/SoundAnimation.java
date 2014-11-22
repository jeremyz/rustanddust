package ch.asynk.tankontank.engine.gfx.animations;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SoundAnimation extends TimedAnimation
{
    public enum Action
    {
        FADE_IN,
        FADE_OUT
    };

    private Sound sound;
    private long soundId;
    private Action action;
    private float volume;

    private static final Pool<SoundAnimation> soundAnimationPool = new Pool<SoundAnimation>() {
        @Override
        protected SoundAnimation newObject() {
            return new SoundAnimation();
        }
    };

    public static SoundAnimation get(Action action, Sound sound, long soundId, float volume, float duration)
    {
        SoundAnimation a = soundAnimationPool.obtain();

        a.action = action;
        a.sound = sound;
        a.soundId = soundId;
        a.volume = volume;
        a.duration = duration;

        return a;
    }

    @Override
    public void dispose()
    {
        soundAnimationPool.free(this);
    }

    @Override
    protected void begin()
    {
    }

    @Override
    protected void end()
    {
        dispose();
    }

    @Override
    protected void update(float percent)
    {
        float v;
        switch(action) {
            case FADE_IN:
                v = ( volume * percent);
                sound.setVolume(soundId, v);
                break;
            case FADE_OUT:
                v = (volume - ( volume * percent));
                sound.setVolume(soundId, v);
                break;
        }
    }

    @Override
    public void draw(Batch batch)
    {
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
    }
}
