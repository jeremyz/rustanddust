package ch.asynk.rustanddust.game.map;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.SelectedTile;
import ch.asynk.rustanddust.engine.gfx.Moveable;
import ch.asynk.rustanddust.engine.gfx.animations.AnimationSequence;
import ch.asynk.rustanddust.engine.gfx.animations.BounceAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.DiceAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.FireAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.TankFireAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.InfantryFireAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.PromoteAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.DestroyAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.SoundAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.RunnableAnimation;
import ch.asynk.rustanddust.engine.gfx.animations.MoveToAnimation.MoveToAnimationCb;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Army;
import ch.asynk.rustanddust.game.Player;
import ch.asynk.rustanddust.game.Ctrl.EventType;

public abstract class Map3Animations extends Map2Moves implements MoveToAnimationCb
{
    protected final DestroyAnimation destroyAnimation;
    private final Sound tankMoveSound;
    private final Sound infantryMoveSound;
    private Sound sound;
    private long soundId = -1;

    public Map3Animations(final RustAndDust game, Texture map, SelectedTile hex)
    {
        super(game, map, hex);

        this.destroyAnimation = new DestroyAnimation();
        this.tankMoveSound = game.manager.get(game.SND_TANK_MOVE, Sound.class);
        this.infantryMoveSound = game.manager.get(game.SND_INF_MOVE, Sound.class);

        DiceAnimation.init(
                game.manager.get(game.PNG_DICE, Texture.class), 16, 9, game.manager.get(game.SND_DICE, Sound.class)
                );
        PromoteAnimation.init(
                game.factory.getHudRegion(game.factory.HUD_STARS),
                game.manager.get(game.SND_PROMOTE_US, Sound.class),
                game.manager.get(game.SND_PROMOTE_GE, Sound.class)
                );
        FireAnimation.init(
                game.manager.get(game.PNG_INF_FIRE, Texture.class), 1, 8,
                game.manager.get(game.PNG_TANK_FIRE, Texture.class), 1, 8,
                game.manager.get(game.PNG_EXPLOSIONS, Texture.class), 16, 8,
                game.manager.get(game.SND_INF_FIRE, Sound.class),
                game.manager.get(game.SND_TANK_FIRE, Sound.class),
                game.manager.get(game.SND_TANK_FIRE_SHORT, Sound.class),
                game.manager.get(game.SND_EXPLOSION, Sound.class),
                game.manager.get(game.SND_EXPLOSION_SHORT, Sound.class)
                );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        destroyAnimation.dispose();
        tankMoveSound.dispose();
        infantryMoveSound.dispose();
        DiceAnimation.free();
        PromoteAnimation.free();
        FireAnimation.free();
    }

    // -> implement MoveToAnimationCb

    @Override
    public void moveToAnimationEnter(Moveable moveable, float x, float y, float r)
    {
        claim(moveable, getHexAt(x, y));
    }

    @Override
    public void moveToAnimationLeave(Moveable moveable, float x, float y, float r)
    {
        unclaim(moveable, getHexAt(x, y));
    }

    @Override
    public void moveToAnimationDone(Moveable moveable, float x, float y, float r)
    {
    }

    // <- implement MoveToAnimationCb

    protected void addBounceAnimation(final Unit unit, float duration)
    {
        addAnimation(BounceAnimation.get(unit, duration));
    }

    protected void addPromoteAnimation(final Unit unit, final Player player, final Runnable after)
    {
        Hex hex = unit.getHex();
        AnimationSequence seq = AnimationSequence.get(2);
        seq.addAnimation(PromoteAnimation.get((unit.getArmy() == Army.US), hex.getX(), hex.getY(), game.config.fxVolume));
        seq.addAnimation(RunnableAnimation.get(unit, after));
        addAnimation(seq);
    }

    protected void addDestroyAnimation(Unit unit)
    {
        destroyAnimation.set(2f, unit);
        addAnimation(destroyAnimation);
    }

    protected void addEngagementAnimation(Unit target)
    {
        FireAnimation.reset();
        Hex to = target.getHex();
        for (Unit u : activatedUnits) {
            Hex from = u.getHex();
            float halfWidth = (u.getWidth() / 2f);
            if (u.isA(Unit.UnitType.INFANTRY))
                addAnimation(InfantryFireAnimation.get(game.config.fxVolume, from.getX(), from.getY(), to.getX(), to.getY(), halfWidth));
            else
                addAnimation(TankFireAnimation.get(game.config.fxVolume, u, from.getX(), from.getY(), to.getX(), to.getY(), halfWidth));
        }
    }

    protected void playMoveSound(Unit unit)
    {
        if (unit.isA(Unit.UnitType.INFANTRY))
            sound = infantryMoveSound;
        else
            sound = tankMoveSound;
        soundId = sound.play(game.config.fxVolume);
    }

    @Override
    protected void animationsDone()
    {
        if (soundId >= 0) {
            addAnimation( SoundAnimation.get(SoundAnimation.Action.FADE_OUT, sound, soundId, game.config.fxVolume, 0.5f));
            soundId = -1;
        } else
            game.ctrl.postEvent(EventType.ANIMATIONS_DONE);
    }
}
