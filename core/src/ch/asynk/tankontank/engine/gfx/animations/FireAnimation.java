package ch.asynk.tankontank.engine.gfx.animations;

import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class FireAnimation
{
    public static Random random = new Random();

    public static Sprites tankFire;
    public static Sprites explosion;

    public static Sound tankFireSnd;
    public static Sound tankFireSndLong;
    public static Sound explosionSnd;
    public static Sound explosionSndLong;

    public static double tankFireSndLongId;
    public static double explosionSndLongId;

    public static void init(
            Texture tankFireT, int sCols, int sRows,
            Texture explosionT, int eCols, int eRows,
            Sound tankFireS,
            Sound tankFireLongS,
            Sound explosionS,
            Sound explosionLongS)
    {
        tankFire = new Sprites(tankFireT, sCols, sRows);
        explosion = new Sprites(explosionT, eCols, eRows);
        tankFireSnd = tankFireS;
        tankFireSndLong = tankFireLongS;
        explosionSnd = explosionS;
        explosionSndLong = explosionLongS;

        reset();
    }

    public static void reset()
    {
        tankFireSndLongId = -1;
        explosionSndLongId = -1;
    }

    public static void free()
    {
        tankFire.dispose();
        explosion.dispose();

        tankFireSnd.dispose();
        tankFireSndLong.dispose();
        explosionSnd.dispose();
        explosionSndLong.dispose();
    }

    public static void tankFireSndPlay(float volume)
    {
        if (tankFireSndLongId == -1)
            tankFireSndLongId = tankFireSndLong.play(volume);
        else
            tankFireSnd.play(volume);
    }

    public static void explosionSndPlay(float volume)
    {
        if (explosionSndLongId == -1)
            explosionSndLongId = explosionSndLong.play(volume);
        else
            explosionSnd.play(volume);
    }
}
